# java调用shell脚本和命令-Runtime方式

使用java调用命令行在项目的开发中经常会使用到，在我最近的一个项目中，也使用到了java调用命令和shell脚本。我将用这篇文章记录下来java调用命令行的实现。

这个是java中使用最多的一种方案吧，对于Runtime在JAVA的API中是这样解释的：

>每个 Java 应用程序都有一个 `Runtime` 类实例，使应用程序能够与其运行的环境相连接。可以通过  `getRuntime` 方法获取当前运行时。 
>
>应用程序不能创建自己的 Runtime 类实例。

从这个API中可以得到一个非常重要的信息，那就是应用程序与其运行的环境相连接。也就是说在执行命令行时，相关的一些运行环境已经帮你设置好了，直接执行命令或者脚本即可。

再来看下具体执行的相关方法的API。

>
>| ` 返回类型` | `方法及参数`                                                 |
>| ----------- | ------------------------------------------------------------ |
>| ` Process`  | `exec(String command)`        在单独的进程中执行指定的字符串命令。 |
>| ` Process`  | `exec(String[] cmdarray)`        在单独的进程中执行指定命令和变量。 |
>| ` Process`  | `exec(String[] cmdarray,  String[] envp)`        在指定环境的独立进程中执行指定命令和变量。 |
>| ` Process`  | `exec(String[] cmdarray,  String[] envp,  File dir)`        在指定环境和工作目录的独立进程中执行指定的命令和变量。 |
>| ` Process`  | `exec(String command, String[] envp)`        在指定环境的单独进程中执行指定的字符串命令。 |
>| ` Process`  | `exec(String command, String[] envp, File dir)`        在有指定环境和工作目录的独立进程中执行指定的字符串命令。 |
>

通过观察这些API发现这些API使用都比较简单，只需要根据自己的需要调用适合的参数即可。再来看看返回对象吧。

` Process`对象的API解释:

>`ProcessBuilder.start()`  和 `Runtime.exec`  方法创建一个本机进程，并返回 `Process`  子类的一个实例，该实例可用来控制进程并获得相关信息。`Process`  类提供了执行从进程输入、执行输出到进程、等待进程完成、检查进程的退出状态以及销毁（杀掉）进程的方法。 
>
>创建进程的方法可能无法针对某些本机平台上的特定进程很好地工作，比如，本机窗口进程，守护进程，Microsoft Windows 上的 Win16/DOS  进程，或者 shell 脚本。创建的子进程没有自己的终端或控制台。它的所有标准 io（即 stdin、stdout 和 stderr）操作都将通过三个流 (`getOutputStream()`、`getInputStream()`  和 `getErrorStream()`)  重定向到父进程。父进程使用这些流来提供到子进程的输入和获得从子进程的输出。因为有些本机平台仅针对标准输入和输出流提供有限的缓冲区大小，如果读写子进程的输出流或输入流迅速出现失败，则可能导致子进程阻塞，甚至产生死锁。 
>
>当没有 `Process` 对象的更多引用时，不是删掉子进程，而是继续异步执行子进程。 
>
>对于带有 `Process` 对象的 Java 进程，没有必要异步或并发执行由 `Process`  对象表示的进程。 

再来看下对象的信息

>
>| `返回信息`               | `方法及参数`                                                 |
>| ------------------------ | ------------------------------------------------------------ |
>| `abstract  void`         | `destroy()`        杀掉子进程。                              |
>| `abstract  int`          | `exitValue()`        返回子进程的出口值。                    |
>| `abstract  InputStream`  | `getErrorStream()`        获取子进程的错误流。               |
>| `abstract  InputStream`  | `getInputStream()`        获取子进程的输入流。               |
>| `abstract  OutputStream` | `getOutputStream()`        获取子进程的输出流。              |
>| `abstract  int`          | `waitFor()`        导致当前线程等待，如有必要，一直要等到由该 `Process` 对象表示的进程已经终止。 |
>

在这个返回值的API中，有两个方法需要特别注意下,`exitValue()`和`waitFor()`方法

exitValue()方法如果子进程未终止，则抛出一个IllegalThreadStateException 异常，如果子进程终止，则返回一个退出值。

waitFor()方法将阻塞调用线程，直到子进程终止，如果子进程已经终止则立即返回。

因此当子进程已经终止时，他们的行为将是相同的，当子进程在运行时，他们的行为将有所不同。exitValue将抛出异常，而waitFor则阻塞调用线程。

## 1. 代码实现

好了，接下来就是代码环节了：

```java
public class SynchronousLocalShellCommand {

  private Logger logger = LoggerFactory.getLogger(SynchronousLocalShellCommand.class);

  /** 命令信息 */
  private final String command;

  public SynchronousLocalShellCommand(String command) {
    this.command = command;
  }

  /**
   * 执行命令并返回结果
   *
   * @return 命令执行结果
   */
  public String doCommand() {

    // 获取操作流
    Process process = null;
    String errorMsg = null;
    try {
      process = Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
      errorMsg = e.getMessage();
    }

    // 当对象获取不成功时，返回对象
    if (null == process) {
      return errorMsg;
    }

    // 获取子进程的输入流。输入流获得由该 Process 对象表示的进程的标准输出流。
    String commandRspOk = reader(process.getInputStream());
    // 进行错误信息的读取操作
    String commandRspError = reader(process.getErrorStream());

    // 构建响应结果
    String commandRsp = commandRspOk + Symbol.LINE + Symbol.LINE + commandRspError;

    int rsp = -1;
    try {
      // 等待进程结束。
      rsp = process.waitFor();
    } catch (InterruptedException e) {
      logger.error("run command {} InterruptedException", command, rsp);
    }

    logger.info("run command {}, response {}", command, rsp);
    return commandRsp;
  }

  /**
   * 数据读取操作
   *
   * @param input 输入流
   */
  private String reader(InputStream input) {
    StringBuilder outDat = new StringBuilder();
    try (InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        outDat.append(line);
        outDat.append(Symbol.LINE);
      }
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }
    return outDat.toString();
  }
}
```



### 1.1 环境测试-linux

然后我们将在linux系统上做一个测试。下面是一段测试代码

```java
public class RuntimeLinuxMain {

  public static void main(String[] args) {
    RuntimeLinuxMain instance = new RuntimeLinuxMain();
    // 同步的执行
    instance.synchornousDoCommand();
  }


  /** 同步执行命令 */
  private void synchornousDoCommand() {
     //执行一个ping命令，将在5次后返回
    this.runSyncCommand("ping -c 5 www.baidu.com");
      //执行一个shell脚本
    this.runSyncCommand("/home/liujun/datarun/shell/run.sh");
      //执行一个错误的命令，不存在的
    this.runSyncCommand("adfadsfa");
  }

  /**
   * 执行同步的命令操作
   *
   * @param commandStr
   */
  private void runSyncCommand(String commandStr) {
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandStr);
    String commandRsp = command.doCommand();
    System.out.println("同步执行结果:" + commandRsp);
    System.out.println("结束---------------");
  }
}
```

以下是测试的一个shell脚本的内容:

```shell
#!/bin/bash
echo "Hello World !"
```

运行结果

```shell
[liujun@fk03 datarun]$ java -cp demojava8-0.0.1-SNAPSHOT.jar:./lib/* com.liujun.command.RuntimeLinuxMain
12:29:14.348 [main] INFO com.liujun.command.runtime.SynchronousLocalShellCommand - run command ping -c 5 www.baidu.com, response 0
同步执行结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=9.94 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=9.81 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=3 ttl=52 time=9.19 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=4 ttl=52 time=8.95 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=5 ttl=52 time=9.00 ms

--- www.a.shifen.com ping statistics ---
5 packets transmitted, 5 received, 0% packet loss, time 4006ms
rtt min/avg/max/mdev = 8.953/9.382/9.948/0.426 ms



结束---------------
12:29:14.359 [main] INFO com.liujun.command.runtime.SynchronousLocalShellCommand - run command /home/liujun/datarun/shell/run.sh, response 0
同步执行结果:Hello World !



结束---------------
12:29:14.363 [main] ERROR com.liujun.command.runtime.SynchronousLocalShellCommand - command : adfadsfa ,exception
java.io.IOException: Cannot run program "adfadsfa": error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
	at java.lang.Runtime.exec(Runtime.java:620)
	at java.lang.Runtime.exec(Runtime.java:450)
	at java.lang.Runtime.exec(Runtime.java:347)
	at com.liujun.command.runtime.SynchronousLocalShellCommand.doCommand(SynchronousLocalShellCommand.java:44)
	at com.liujun.command.RuntimeLinuxMain.runSyncCommand(RuntimeLinuxMain.java:91)
	at com.liujun.command.RuntimeLinuxMain.synchornousDoCommand(RuntimeLinuxMain.java:81)
	at com.liujun.command.RuntimeLinuxMain.main(RuntimeLinuxMain.java:23)
Caused by: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.forkAndExec(Native Method)
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:247)
	at java.lang.ProcessImpl.start(ProcessImpl.java:134)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1029)
	... 7 common frames omitted
同步执行结果:Cannot run program "adfadsfa": error=2, No such file or directory
结束---------------
[liujun@fk03 datarun]$ 
```

现在来分析下执行结果：

```shell
ping -c 5 www.baidu.com
```

可以看到这条命令的结果是成功的执行了！并且拿到了结果:

```
PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=9.94 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=9.81 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=3 ttl=52 time=9.19 ms
```

第二条命令是执行一个shell脚本文件

```shell
/home/liujun/datarun/shell/run.sh
```

这个执行同样成功了，也拿到了输出的结果:

```shell
Hello World !
```

再说最后一个错误的命执行吧。

```
adfadsfa
```

而这个命令收到就是一个异常输出的日志了，而且还有一段错误的输出:

```shell
Cannot run program "adfadsfa": error=2, No such file or directory
```

## 

### 1.2 环境测试-windows

由于我本机是windows是环境，我就使用junit做的单元测试。

还是先来看代码吧:

```java
public class TestSynchronousLocalShellCommand {

  /** 同步执行命令 */
  @Test
  public void synchronousDoCommand() {
    // ping命令在windows上执行三次后会正常退出
    this.runCommand("ping www.baidu.com");
    // 执行一个window脚本
    this.runCommand("D:/run/bat/run.bat");
    // 执行一个错误命令
    this.runCommand("adfadsfa");
  }

  private void runCommand(String commandStr) {
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandStr);
    String commandRsp = command.doCommand();
    Assert.assertNotNull(commandRsp);
    System.out.println("命令:" + commandStr + ",执行结果:" + commandRsp);
    System.out.println("结束----------");
  }
}
```

bat脚本

```shell
echo %JAVA_HOME%
echo "hello world!!"
```



在windows上执行特别注意下编码，不然输出就是乱码。如

![](D:\java\workspace\selfwork\demojava8\src\main\java\com\liujun\command\img\windows-runtime-failcode.png)

这个可在读取时指定编码以解决此问题,修改后的代码如下:

```java
  private String reader(InputStream input) {
    StringBuilder outDat = new StringBuilder();
    try (InputStreamReader inputReader = new InputStreamReader(input, Charset.forName("GBK"));
        BufferedReader bufferedReader = new BufferedReader(inputReader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        outDat.append(line);
        outDat.append(Symbol.LINE);
      }
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }
    return outDat.toString();
  }
```

重点在Charset.forName("GBK")指定编码.

那我们再看下windows执行结果:

```shell
16:18:22.751 [main] INFO com.liujun.command.runtime.SynchronousLocalShellCommand - run command ping www.baidu.com, response 0
命令:ping www.baidu.com,执行结果:
正在 Ping www.a.shifen.com [180.101.49.11] 具有 32 字节的数据:
来自 180.101.49.11 的回复: 字节=32 时间=11ms TTL=52
来自 180.101.49.11 的回复: 字节=32 时间=17ms TTL=52
来自 180.101.49.11 的回复: 字节=32 时间=11ms TTL=52
来自 180.101.49.11 的回复: 字节=32 时间=52ms TTL=52

180.101.49.11 的 Ping 统计信息:
    数据包: 已发送 = 4，已接收 = 4，丢失 = 0 (0% 丢失)，
往返行程的估计时间(以毫秒为单位):
    最短 = 11ms，最长 = 52ms，平均 = 22ms



结束----------
16:18:22.791 [main] INFO com.liujun.command.runtime.SynchronousLocalShellCommand - run command D:/run/bat/run.bat, response 0
命令:D:/run/bat/run.bat,执行结果:
D:\java\workspace\selfwork\demojava8>echo C:\java\jdk8\jdk1.8.0_241 
C:\java\jdk8\jdk1.8.0_241

D:\java\workspace\selfwork\demojava8>echo "hello world!!" 
"hello world!!"



结束----------
16:18:22.796 [main] ERROR com.liujun.command.runtime.SynchronousLocalShellCommand - command : adfadsfa ,exception
java.io.IOException: Cannot run program "adfadsfa": CreateProcess error=2, 系统找不到指定的文件。
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
	at java.lang.Runtime.exec(Runtime.java:620)
	at java.lang.Runtime.exec(Runtime.java:450)
	at java.lang.Runtime.exec(Runtime.java:347)
	at com.liujun.command.runtime.SynchronousLocalShellCommand.doCommand(SynchronousLocalShellCommand.java:44)
	at com.liujun.command.runtime.TestSynchronousLocalShellCommand.runCommand(TestSynchronousLocalShellCommand.java:27)
	at com.liujun.command.runtime.TestSynchronousLocalShellCommand.synchronousDoCommand(TestSynchronousLocalShellCommand.java:22)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.executeTestMethod(JUnit4TestRunnerDecorator.java:162)
	at mockit.integration.junit4.internal.JUnit4TestRunnerDecorator.invokeExplosively(JUnit4TestRunnerDecorator.java:71)
	at mockit.integration.junit4.internal.MockFrameworkMethod.invokeExplosively(MockFrameworkMethod.java:37)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
Caused by: java.io.IOException: CreateProcess error=2, 系统找不到指定的文件。
	at java.lang.ProcessImpl.create(Native Method)
	at java.lang.ProcessImpl.<init>(ProcessImpl.java:444)
	at java.lang.ProcessImpl.start(ProcessImpl.java:140)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1029)
	... 32 common frames omitted
命令:adfadsfa,执行结果:Cannot run program "adfadsfa": CreateProcess error=2, 系统找不到指定的文件。
结束----------
```



现在对结果分析下：

> ping www.baidu.com

这个查看到如下信息，这是已经成功的执行。

```shell
命令:ping www.baidu.com,执行结果:
正在 Ping www.a.shifen.com [180.101.49.11] 具有 32 字节的数据:
来自 180.101.49.11 的回复: 字节=32 时间=11ms TTL=52
来自 180.101.49.11 的回复: 字节=32 时间=17ms TTL=52
```

再来看run.bat脚本的执行也可查看到如下信息，这就说明数据也成功了。

```
命令:D:/run/bat/run.bat,执行结果:
D:\java\workspace\selfwork\demojava8>echo C:\java\jdk8\jdk1.8.0_241 
C:\java\jdk8\jdk1.8.0_241

D:\java\workspace\selfwork\demojava8>echo "hello world!!" 
"hello world!!"
```

只剩下最后一个错误的命令了

> adfadsfa

这个收到的是一个错误的提示：

```shell
java.io.IOException: Cannot run program "adfadsfa": CreateProcess error=2, 系统找不到指定的文件。
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
	at java.lang.Runtime.exec(Runtime.java:620)
	at java.lang.Runtime.exec(Runtime.java:450)
	at java.lang.Runtime.exec(Runtime.java:347)
	at com.liujun.command.runtime.SynchronousLocalShellCommand.doCommand(SynchronousLocalShellCommand.java:44)
	at com.liujun.command.runtime.TestSynchronousLocalShellCommand.runCommand(TestSynchronousLocalShellCommand.java:27)
	at com.liujun.command.runtime.TestSynchronousLocalShellCommand.synchronousDoCommand(TestSynchronousLocalShellCommand.java:22)
```

至此windows的测试也已经通过了。



## 2. 新的需求点-增加异步

上面的代码已经基本满足简单命令执行的要求了。但是如果在命令执行中就要拿到输出信息呢。这类的场景还是挺多的。比如一个较大的shell脚本文件执行。或者其他命令是长时间执行的。但我们想要的可不是这种一定要等到执行结束后才拿到结果。命令的边执行就可以有信息的输出以做查看。针对这类情况，那应该如何解决呢？

这是一个很常见的异步的场景。就是在命令执行中边获取命令的输出。那一般解决方案就是使用一个线程来执行命令并将命令的信息的输出到内存中，需要读取信息直接访问这块内存就可以了。

要实现多线程就得实现异步运行的相关接口。而且我们这个线程运行后不需要返回值，所以使用Runnable即可

```java
public class AsynchronousLocalShellCommand implements Runnable 
```

当任务执行时，将创建相关的对象。并提交线程池运行。

```java
 /** 开始执行命令 */
  public void doCommand() throws IOException {
    // 获取命令执行工作进程对象
    this.process = Runtime.getRuntime().exec(command);

    if (null != process) {
      // 获得由该 Process 对象表示的进程的标准输出流。
      input = process.getInputStream();
      // 如果正常流获取不到，则获取错误的输出
      error = process.getErrorStream();

      // 成功时才将任务提交线程池运行
      TaskThreadDataPool.INSTANCE.submit(this);
    }
  }
```

当线程被调度到后。将相关的流的数据记录到内存即可

```java
  @Override
  public void run() {
    // 读取input中的数据，记录到successData这个StringBuilder对象中
    reader(this.input, successData);
    // 错误的数据流读取操作，记录到errorData这个StringBuilder对象中
    reader(this.error, errorData);

    int rsp = -1;
    try {
      // 等待进程结束，不能直接调用exitValue() ，这将导致还没有运行完成就返回了结果
      rsp = process.waitFor();
    } catch (InterruptedException e) {
      logger.error("run command {} InterruptedException", command, rsp);
      // 停止操作
      rsp = -5;
    }
    processOutCode.set(rsp);
    logger.info("run command {}, response {}", command, rsp);
  }
```

最后再提供数据访问方法

```java
 /**
   * 获取执行结果
   *
   * @return
   */
  public String getSuccessData() {
    return successData.toString();
  }
```

这就是一个异步获取实现。

下面这是一个完整的代码：

```java
public class AsynchronousLocalShellCommand implements Runnable {

  private Logger logger = LoggerFactory.getLogger(AsynchronousLocalShellCommand.class);

  /** 命令信息 */
  private final String command;

  /** 命令执行的工作进程 */
  private Process process;

  /** 数据的输出流 */
  private InputStream input;

  /** 错误的输出流 */
  private InputStream error;

  /** 成功执行输出信息 */
  private final StringBuilder successData = new StringBuilder();

  /** 失败时输出的信息 */
  private final StringBuilder errorData = new StringBuilder();

  /** 进程执行结束后的结果,-1 初始化，0，成功执行结束 */
  private AtomicInteger processOutCode = new AtomicInteger(-1);

  /** 运行标识 */
  private AtomicBoolean runFlag = new AtomicBoolean(true);

  public AsynchronousLocalShellCommand(String command) {
    this.command = command;
  }

  /** 开始执行命令 */
  public void doCommand() throws IOException {
    // 获取操作流
    this.process = Runtime.getRuntime().exec(command);

    if (null != process) {
      // 获得由该 Process 对象表示的进程的标准输出流。
      input = process.getInputStream();
      // 如果正常流获取不到，则获取错误的输出
      error = process.getErrorStream();

      // 成功时才将任务提交线程池运行
      TaskThreadDataPool.INSTANCE.submit(this);
    }
  }

  @Override
  public void run() {
    // 读取input中的数据，记录到successData这个StringBuilder对象中
    reader(this.input, successData);
    // 错误的数据流读取操作，记录到errorData这个StringBuilder对象中
    reader(this.error, errorData);

    int rsp = -1;
    try {
      // 等待进程结束，不能直接调用exitValue() ，这将导致还没有运行完成就返回了结果
      rsp = process.waitFor();
    } catch (InterruptedException e) {
      logger.error("run command {} InterruptedException", command, rsp);
      // 停止操作
      rsp = -5;
    }
    processOutCode.set(rsp);
    logger.info("run command {}, response {}", command, rsp);
  }

  /**
   * 获取执行结果
   *
   * @return
   */
  public String getSuccessData() {
    return successData.toString();
  }

  /**
   * 获取错误的信息
   *
   * @return
   */
  public String getErrorData() {
    return errorData.toString();
  }

  /**
   * 返回进程执行的结果码
   *
   * @return
   */
  public int rspProcess() {
    return processOutCode.get();
  }

  /**
   * 停止操作
   *
   * @param
   */
  public void stop() {
    // 停止读取输出操作
    this.runFlag.set(false);
    if (null != process) {
      // 停止子进程
      process.destroy();
    }
  }

  /**
   * 数据读取操作
   *
   * @param input 输入流
   * @param data 记录数据对象
   */
  private void reader(InputStream input, StringBuilder data) {
    try (InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null && runFlag.get()) {
        data.append(line);
        data.append(Symbol.LINE);
      }
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }
  }
}
```

写完这个代码，

### 2.1 环境测试-linux

我们再来执行下异步的测试，先看下异步测试的代码:

```java
public class RuntimeLinuxMain {

  public static void main(String[] args) {
    RuntimeLinuxMain instance = new RuntimeLinuxMain();
    // 异步的执行
    instance.asynchronousDoCommand();
  }

  /** 异步执行命令 */
  private void asynchronousDoCommand() {
    // 执行一个正常的命令,一直会执行
    this.runAsyncCommand("ping www.baidu.com");
    // 运行一个脚本文件
    this.runAsyncCommand("/home/liujun/datarun/shell/run.sh");
    // 执行一个错误命令
    this.runAsyncCommand("adfadsfa");
    TaskThreadDataPool.INSTANCE.shutdown();
  }

  /**
   * 异步的执行操作
   *
   * @param commandStr
   */
  private void runAsyncCommand(String commandStr) {
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandStr);
    boolean submitFlag = false;
    try {
      command.doCommand();
      submitFlag = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 当提交失败时，不再继续获取数据
    if (!submitFlag) {
      return;
    }
    for (int i = 0; i < 5; i++) {
      int code = command.rspProcess();
      // 获取正常的结果
      String successData = command.getSuccessData();
      // 获取错误的信息
      String errorData = command.getErrorData();
      System.out.println(
          "当前第:" + i + "次，code:" + code + "，成功结果:" + successData + ",失败结果:" + errorData);
      // 由于这个ping执行需要一定的时间每次休眠1秒
      ThreadUtils.sleep(1);
    }

    //停止运行
    command.stop();
    System.out.println("结束---------------");
  }

 }
```

获取输出的结果：

```shell
[liujun@fk03 datarun]$ java -cp demojava8-0.0.1-SNAPSHOT.jar:./lib/* com.liujun.command.RuntimeLinuxMain
当前第:0次，code:-1，成功结果:,失败结果:
当前第:1次，code:-1，成功结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=10.0 ms
,失败结果:
当前第:2次，code:-1，成功结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=10.0 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=9.60 ms
,失败结果:
当前第:3次，code:-1，成功结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=10.0 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=9.60 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=3 ttl=52 time=9.30 ms
,失败结果:
当前第:4次，code:-1，成功结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=10.0 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=9.60 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=3 ttl=52 time=9.30 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=4 ttl=52 time=7.34 ms
,失败结果:
结束---------------
当前第:0次，code:-1，成功结果:,失败结果:
15:35:34.771 [command-2] INFO com.liujun.command.runtime.AsynchronousLocalShellCommand - run command /home/liujun/datarun/shell/run.sh, response 0
15:35:34.774 [command-1] ERROR com.liujun.command.runtime.AsynchronousLocalShellCommand - command : ping www.baidu.com ,exception
java.io.IOException: Stream closed
	at java.io.BufferedInputStream.getBufIfOpen(BufferedInputStream.java:170)
	at java.io.BufferedInputStream.read(BufferedInputStream.java:336)
	at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)
	at sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)
	at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)
	at java.io.InputStreamReader.read(InputStreamReader.java:184)
	at java.io.BufferedReader.fill(BufferedReader.java:161)
	at java.io.BufferedReader.readLine(BufferedReader.java:324)
	at java.io.BufferedReader.readLine(BufferedReader.java:389)
	at com.liujun.command.runtime.AsynchronousLocalShellCommand.reader(AsynchronousLocalShellCommand.java:143)
	at com.liujun.command.runtime.AsynchronousLocalShellCommand.run(AsynchronousLocalShellCommand.java:78)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
15:35:34.778 [command-1] INFO com.liujun.command.runtime.AsynchronousLocalShellCommand - run command ping www.baidu.com, response 143
当前第:1次，code:0，成功结果:Hello World !
,失败结果:
当前第:2次，code:0，成功结果:Hello World !
,失败结果:
当前第:3次，code:0，成功结果:Hello World !
,失败结果:
当前第:4次，code:0，成功结果:Hello World !
,失败结果:
结束---------------
java.io.IOException: Cannot run program "adfadsfa": error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
	at java.lang.Runtime.exec(Runtime.java:620)
	at java.lang.Runtime.exec(Runtime.java:450)
	at java.lang.Runtime.exec(Runtime.java:347)
	at com.liujun.command.runtime.AsynchronousLocalShellCommand.doCommand(AsynchronousLocalShellCommand.java:60)
	at com.liujun.command.RuntimeLinuxMain.runAsyncCommand(RuntimeLinuxMain.java:48)
	at com.liujun.command.RuntimeLinuxMain.asynchronousDoCommand(RuntimeLinuxMain.java:34)
	at com.liujun.command.RuntimeLinuxMain.main(RuntimeLinuxMain.java:21)
Caused by: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.forkAndExec(Native Method)
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:247)
	at java.lang.ProcessImpl.start(ProcessImpl.java:134)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1029)
	... 7 more
[liujun@fk03 datarun]$ 
```

我们再来分析下执行结果：

首先是命令

> ping www.baidu.com

我们看到第1次输出时，数据都没有，这说明第一次命令还未执行完成。

```
当前第:0次，code:-1，成功结果:,失败结果:
```

再看第二次第三次,就可以观察到日志边执行输出的情况了。

```shell
当前第:1次，code:-1，成功结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=10.0 ms
,失败结果:
当前第:2次，code:-1，成功结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=10.0 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=9.60 ms
,失败结果:
当前第:3次，code:-1，成功结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=10.0 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=9.60 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=3 ttl=52 time=9.30 ms
```

那这每一个命令就已经 OK了，

但这里却有一个异步的信息的信息:

```sh
15:35:34.771 [command-2] INFO com.liujun.command.runtime.AsynchronousLocalShellCommand - run command /home/liujun/datarun/shell/run.sh, response 0
15:35:34.774 [command-1] ERROR com.liujun.command.runtime.AsynchronousLocalShellCommand - command : ping www.baidu.com ,exception
java.io.IOException: Stream closed
	at java.io.BufferedInputStream.getBufIfOpen(BufferedInputStream.java:170)
	at java.io.BufferedInputStream.read(BufferedInputStream.java:336)
	at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)
	at sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)
	at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)
	at java.io.InputStreamReader.read(InputStreamReader.java:184)
	at java.io.BufferedReader.fill(BufferedReader.java:161)
	at java.io.BufferedReader.readLine(BufferedReader.java:324)
	at java.io.BufferedReader.readLine(BufferedReader.java:389)
	at com.liujun.command.runtime.AsynchronousLocalShellCommand.reader(AsynchronousLocalShellCommand.java:143)
	at com.liujun.command.runtime.AsynchronousLocalShellCommand.run(AsynchronousLocalShellCommand.java:78)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
15:35:34.778 [command-1] INFO com.liujun.command.runtime.AsynchronousLocalShellCommand - run command ping www.baidu.com, response 143

```

这里出现了异常。难道我们的程序有什么不对吗？

其实这是对的。因为我们的程序在执行了5次之后就调用了stop方法。stop方法

```
  /**
   * 停止操作
   *
   * @param
   */
  public void stop() {
    // 停止读取输出操作
    this.runFlag.set(false);
    if (null != process) {
      // 停止子进程
      process.destroy();
    }
  }
```

这样在stop方法被调用后。进程当然就停止了，然后关闭了流通道。这样就会报出来Stream closed的异常了。



再看第二个shell脚本的执行。

> /home/liujun/datarun/shell/run.sh

同样的第一次执行时。脚本未执行完成，没有任何信息。

```
当前第:0次，code:-1，成功结果:,失败结果:
```

再等待一会后。相关的信息就输出了:

```sh
当前第:1次，code:0，成功结果:Hello World !
,失败结果:
当前第:2次，code:0，成功结果:Hello World !
,失败结果:
当前第:3次，code:0，成功结果:Hello World !
```



再就只有最后一个错误的命令调用了

> adfadsfa

这个异步命令都还没有开始执行就已经报出了异常。

```java
java.io.IOException: Cannot run program "adfadsfa": error=2, No such file or directory
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
	at java.lang.Runtime.exec(Runtime.java:620)
	at java.lang.Runtime.exec(Runtime.java:450)
	at java.lang.Runtime.exec(Runtime.java:347)
	at com.liujun.command.runtime.AsynchronousLocalShellCommand.doCommand(AsynchronousLocalShellCommand.java:60)
	at com.liujun.command.RuntimeLinuxMain.runAsyncCommand(RuntimeLinuxMain.java:48)
	at com.liujun.command.RuntimeLinuxMain.asynchronousDoCommand(RuntimeLinuxMain.java:34)
	at com.liujun.command.RuntimeLinuxMain.main(RuntimeLinuxMain.java:21)
Caused by: java.io.IOException: error=2, No such file or directory
	at java.lang.UNIXProcess.forkAndExec(Native Method)
	at java.lang.UNIXProcess.<init>(UNIXProcess.java:247)
	at java.lang.ProcessImpl.start(ProcessImpl.java:134)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1029)
	... 7 more
```

### 2.2 环境测试-windows

同样的给出单元测试的代码:

```java
public class TestAsynchronousLocalShellCommand {

  /** 测试异步命令的执行 */
  @Test
  public void asynchronous() throws IOException {

    // ping命令在windows上执行三次后会正常退出
    this.runCommand("ping www.baidu.com");
    // 执行一个window脚本
    this.runCommand("D:/run/bat/run.bat");
    // 执行一个错误命令
    this.runCommand("adfadsfa");

    // 停止线程池
    TaskThreadDataPool.INSTANCE.shutdown();
  }

  /**
   * 命令的执行操作
   *
   * @param commandStr
   */
  private void runCommand(String commandStr) throws IOException {
    // 进行命令的执行操作
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandStr);
    // 开始执行命令
    command.doCommand();

    for (int i = 0; i < 3; i++) {
      int code = command.rspProcess();
      String outData = command.getSuccessData();
      String errorMsg = command.getErrorData();
      System.out.println("errorcode :" + code + ",成功信息:" + outData + ",errorData:" + errorMsg);
      ThreadUtils.sleep(1);
    }

    // 不再运行
    command.stop();
    System.out.println("结束----------");
  }
}
```

查看结果:

```shell
第0次，errorcode :-1,成功信息:,errorData:
第1次，errorcode :-1,成功信息:
正在 Ping www.a.shifen.com [180.101.49.12] 具有 32 字节的数据:
来自 180.101.49.12 的回复: 字节=32 时间=18ms TTL=52
,errorData:
第2次，errorcode :-1,成功信息:
正在 Ping www.a.shifen.com [180.101.49.12] 具有 32 字节的数据:
来自 180.101.49.12 的回复: 字节=32 时间=18ms TTL=52
来自 180.101.49.12 的回复: 字节=32 时间=10ms TTL=52
,errorData:
结束----------
第0次，errorcode :-1,成功信息:,errorData:
16:31:50.825 [command-1] INFO com.liujun.command.runtime.AsynchronousLocalShellCommand - run command ping www.baidu.com, response 1
16:31:50.861 [command-2] INFO com.liujun.command.runtime.AsynchronousLocalShellCommand - run command D:/run/bat/run.bat, response 0
第1次，errorcode :0,成功信息:
D:\java\workspace\selfwork\demojava8>echo C:\java\jdk8\jdk1.8.0_241 
C:\java\jdk8\jdk1.8.0_241

D:\java\workspace\selfwork\demojava8>echo "hello world!!" 
"hello world!!"
,errorData:
第2次，errorcode :0,成功信息:
D:\java\workspace\selfwork\demojava8>echo C:\java\jdk8\jdk1.8.0_241 
C:\java\jdk8\jdk1.8.0_241

D:\java\workspace\selfwork\demojava8>echo "hello world!!" 
"hello world!!"
,errorData:
结束----------

java.io.IOException: Cannot run program "adfadsfa": CreateProcess error=2, 系统找不到指定的文件。

	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
	at java.lang.Runtime.exec(Runtime.java:620)
	at java.lang.Runtime.exec(Runtime.java:450)
	at java.lang.Runtime.exec(Runtime.java:347)
	at com.liujun.command.runtime.AsynchronousLocalShellCommand.doCommand(AsynchronousLocalShellCommand.java:60)
	at com.liujun.command.runtime.TestAsynchronousLocalShellCommand.runCommand(TestAsynchronousLocalShellCommand.java:41)
	at com.liujun.command.runtime.TestAsynchronousLocalShellCommand.asynchronous(TestAsynchronousLocalShellCommand.java:26)
Caused by: java.io.IOException: CreateProcess error=2, 系统找不到指定的文件。
	at java.lang.ProcessImpl.create(Native Method)
	at java.lang.ProcessImpl.<init>(ProcessImpl.java:444)
	at java.lang.ProcessImpl.start(ProcessImpl.java:140)
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1029)
	... 6 more
```

此异步执行也同样得到了预期的结果。



## 总结

这篇文章我总结了自己使用Runtime这种方来来调用命令行以及调用shell脚本相关的一些思考及实现。用两种方式实现了个功能。先是最基本的命令的执行。这类命令较简单，直接执行然后返回。在一些简单的场景中可直接使用。但是随着需求的增加。命令也变得越来越复杂，时间执行也变得越来越长。这种只能等到最后看结果的方式，不被人所接受，我们需要一种能够边执行命令边输出的方式，这时候异步执行被加入了进来，我通过加入线程的方式，展示了如何实现边执行边读取的方案。

同时我将代码也上传至了github。这是地址:

https://github.com/kkzfl22/demojava8/blob/master/src/main/java/com/liujun/command/RuntimeLinuxMain.java

至此基本功能已经OK，但还有个小小的问题那就是这个InputStream和errorStream需要分开获取。下篇文章我将寻找另外一种方案来解决这个问题。