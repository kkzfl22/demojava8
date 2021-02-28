# java调用shell脚本和命令-ProcessBuilder方式
在上一节中，我使用Runtime的方式，实现了对命令行和脚本文件的运行。最后我留下了一个小小的问题那就是这个InputStream和errorStream需要分开获取，那有没有其他方，不用分开获取流呢？

## 1.相关文档

答案当然是有的，这就是这章节要使用的`ProcessBuilder`方式了。

同样的，也先看下`ProcessBuilder`的API吧。

>类用于创建操作系统进程。 
>
>每个 `ProcessBuilder` 实例管理一个进程属性集。`start()`)  方法利用这些属性创建一个新的 `Process` 实例。`start()`)  方法可以从同一实例重复调用，以利用相同的或相关的属性创建新的子进程。 
>
>每个进程生成器管理这些进程属性： 
>
>- *命令*  是一个字符串列表，它表示要调用的外部程序文件及其参数（如果有）。在此，表示有效的操作系统命令的字符串列表是依赖于系统的。例如，每一个总体变量，通常都要成为此列表中的元素，但有一些操作系统，希望程序能自己标记命令行字符串——在这种系统中，Java  实现可能需要命令确切地包含这两个元素。 
>- *环境* 是从*变量* 到*值* 的依赖于系统的映射。初始值是当前进程环境的一个副本（请参阅 `System.getenv()`）。 
>- *工作目录*。默认值是当前进程的当前工作目录，通常根据系统属性 `user.dir` 来命名。 
>- *redirectErrorStream* 属性。最初，此属性为  `false`，意思是子进程的标准输出和错误输出被发送给两个独立的流，这些流可以通过 `Process.getInputStream()`)  和 `Process.getErrorStream()`)  方法来访问。如果将值设置为 `true`，标准错误将与标准输出合并。这使得关联错误消息和相应的输出变得更容易。在此情况下，合并的数据可从  `Process.getInputStream()`)  返回的流读取，而从 `Process.getErrorStream()`)  返回的流读取将直接到达文件尾。 
>
>修改进程构建器的属性将影响后续由该对象的 `start()`)  方法启动的进程，但从不会影响以前启动的进程或 Java 自身的进程。 
>
>大多数错误检查由 `start()`)  方法执行。可以修改对象的状态，但这样 `start()`)  将会失败。例如，将命令属性设置为一个空列表将不会抛出异常，除非包含了 `start()`)。 
>
>**注意，此类不是同步的。**如果多个线程同时访问一个 `ProcessBuilder`，而其中至少一个线程从结构上修改了其中一个属性，它*必须*  保持外部同步。 
>
>要利用一组明确的环境变量启动进程，在添加环境变量之前，首先调用 `Map.clear()`

这个API的解释中有一处特别的说明redirectErrorStream这个属性。这个设置为true即可实现流的合并操作。

看完了相关的方法的API吧。

| **构造方法摘要**                                             |
| ------------------------------------------------------------ |
| `ProcessBuilder(List<String> command)`        利用指定的操作系统程序和参数构造一个进程生成器。 |
| `ProcessBuilder(String... command)`        利用指定的操作系统程序和参数构造一个进程生成器。 |

| **方法摘要**          |                                                              |
| --------------------- | ------------------------------------------------------------ |
| ` List<String>`       | `command()`        返回此进程生成器的操作系统程序和参数。    |
| ` ProcessBuilder`     | `command(List<String> command)`        设置此进程生成器的操作系统程序和参数。 |
| ` ProcessBuilder`     | `command(String... command)`        设置此进程生成器的操作系统程序和参数。 |
| ` File`               | `directory()`        返回此进程生成器的工作目录。            |
| ` ProcessBuilder`     | `directory(File directory)`        设置此进程生成器的工作目录。 |
| ` Map<String,String>` | `environment()`        返回此进程生成器环境的字符串映射视图。 |
| ` boolean`            | `redirectErrorStream()`        通知进程生成器是否合并标准错误和标准输出。 |
| ` ProcessBuilder`     | `redirectErrorStream(boolean redirectErrorStream)`        设置此进程生成器的 `redirectErrorStream` 属性。 |
| ` Process`            | `start()`        使用此进程生成器的属性启动一个新进程。      |



好了接下来就开始代码实现了。

## 2.基础代码实现

这个过程与Runtime也基本无二异。就是将命令行参数传递给ProcessBuilder.将errorStream与inputStram合并，即设置redirectErrorStream属性为true

```java
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);
```

接下来就是将读取流的信息即可。

来看下完整的代码实现:

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
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      // 将错误输出流转移到标准输出流中,但使用Runtime不可以
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      String dataMsg = reader(process.getInputStream());
      int rsp = process.waitFor();
      logger.info("run command {}, response {}", command, rsp);
      return dataMsg;
    } catch (IOException | InterruptedException e) {
      logger.error("command : {} ,exception", command, e);
    }

    return null;
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



### 2.1 环境测试-windows

先来看下测试的代码：

```java
public class TestSynchronousLocalShellCommand {

  /** 同步执行命令 */
  @Test
  public void synchornousDoCommand() {
    // 运行一个正常的命令
    this.runCommand("ping www.baidu.com");
    // 运行一个bat脚本
    this.runCommand("D:/run/bat/run.bat");
    // 错误命令
    this.runCommand("abcdef");
  }

  /**
   * 运行command
   *
   * @param commandStr 错误命令行
   */
  private void runCommand(String commandStr) {
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandStr);

    String commandRsp = command.doCommand();
    Assert.assertNotNull(commandRsp);
    System.out.println(commandRsp);
  }
}
```

### 2.2 首先运行的问题

这里就可以看到结果：

```java
11:57:58.177 [main] ERROR com.liujun.command.processbuilder.SynchronousLocalShellCommand - command : ping www.baidu.com ,exception
java.io.IOException: Cannot run program "ping www.baidu.com": CreateProcess error=2, 系统找不到指定的文件。
	at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
	at com.liujun.command.processbuilder.SynchronousLocalShellCommand.doCommand(SynchronousLocalShellCommand.java:44)
	at com.liujun.command.processbuilder.TestSynchronousLocalShellCommand.runCommand(TestSynchronousLocalShellCommand.java:33)
	at com.liujun.command.processbuilder.TestSynchronousLocalShellCommand.synchornousDoCommand(TestSynchronousLocalShellCommand.java:18)
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
	... 29 common frames omitted

java.lang.AssertionError
	at com.liujun.command.processbuilder.TestSynchronousLocalShellCommand.runCommand(TestSynchronousLocalShellCommand.java:34)
	at com.liujun.command.processbuilder.TestSynchronousLocalShellCommand.synchornousDoCommand(TestSynchronousLocalShellCommand.java:18)
```

这个结果居然是提示找不到命令。那是哪里出现问题了？

### 2.3 问题的解决方案

像在命令行一般提示找不到指定文件，我的第一想法就是在系统上下文中找不到指定的运行文件。而像ping这样命令如果在windows运行的话，第一步当然打开cmd.exe，然后输入ping www.baidu.com就能看到了。

![](D:\java\workspace\selfwork\demojava8\src\main\java\com\liujun\command\img\windows运行的基本命令.png)

![](D:\java\workspace\selfwork\demojava8\src\main\java\com\liujun\command\img\windows下运行ping命令.png)

经过这样一分析，我这个程序的运行是缺少了上下文了，那如何添加上下文呢？

当检查了**ProcessBuilder**后发现这个参数是接受一个集合为参数的，将命令行添加即可。但还存在一个问题，那就是当我手动执行时，是分成了两个步骤先是打开cmd,然后输入ping命令。而我执行是需要一次执行命令的。这该怎么办呢？

这时候不访看看cmd的帮助文档?

>C:\Users\liujun>cmd /?
>启动 Windows 命令解释器的一个新实例
>
>CMD [/A | /U] [/Q] [/D] [/E:ON | /E:OFF] [/F:ON | /F:OFF] [/V:ON | /V:OFF]
>    [[/S] [/C | /K] string]
>
>/C      执行字符串指定的命令然后终止
>/K      执行字符串指定的命令但保留
>/S      修改 /C 或 /K 之后的字符串处理(见下)
>/Q      关闭回显
>/D      禁止从注册表执行 AutoRun 命令(见下)
>/A      使向管道或文件的内部命令输出成为 ANSI
>/U      使向管道或文件的内部命令输出成为
>        Unicode
>/T:fg   设置前台/背景颜色(详细信息见 COLOR /?)
>/E:ON   启用命令扩展(见下)
>/E:OFF  禁用命令扩展(见下)
>/F:ON   启用文件和目录名完成字符(见下)
>/F:OFF  禁用文件和目录名完成字符(见下)
>/V:ON   使用 ! 作为分隔符启用延迟的环境变量
>        扩展。例如，/V:ON 会允许 !var! 在执行时
>        扩展变量 var。var 语法会在输入时
>        扩展变量，这与在一个 FOR
>        循环内不同。
>/V:OFF  禁用延迟的环境扩展。
>
>注意，如果字符串加有引号，可以接受用命令分隔符 "&&"
>分隔多个命令。另外，由于兼容性
>原因，/X 与 /E:ON 相同，/Y 与 /E:OFF 相同，且 /R 与
>/C 相同。任何其他开关都将被忽略。
>
>如果指定了 /C 或 /K，则会将该开关之后的
>命令行的剩余部分作为一个命令行处理，其中，会使用下列逻辑
>处理引号(")字符:
>
>    1.  如果符合下列所有条件，则会保留
>        命令行上的引号字符:
>    
>        - 不带 /S 开关
>        - 正好两个引号字符
>        - 在两个引号字符之间无任何特殊字符，
>          特殊字符指下列字符: &<>()@^|
>        - 在两个引号字符之间至少有
>          一个空格字符
>        - 在两个引号字符之间的字符串是某个
>          可执行文件的名称。
>    
>    2.  否则，老办法是看第一个字符
>        是否是引号字符，如果是，则去掉首字符并
>        删除命令行上最后一个引号，保留
>        最后一个引号之后的所有文本。
>
>如果 /D 未在命令行上被指定，当 CMD.EXE 开始时，它会寻找
>以下 REG_SZ/REG_EXPAND_SZ 注册表变量。如果其中一个或
>两个都存在，这两个变量会先被执行。
>
>    HKEY_LOCAL_MACHINE\Software\Microsoft\Command Processor\AutoRun
>    
>        和/或
>    
>    HKEY_CURRENT_USER\Software\Microsoft\Command Processor\AutoRun
>
>命令扩展是按默认值启用的。你也可以使用 /E:OFF ，为某一
>特定调用而停用扩展。你
>可以在机器上和/或用户登录会话上
>启用或停用 CMD.EXE 所有调用的扩展，这要通过设置使用
>REGEDIT.EXE 的注册表中的一个或两个 REG_DWORD 值:
>
>    HKEY_LOCAL_MACHINE\Software\Microsoft\Command Processor\EnableExtensions
>    
>        和/或
>    
>    HKEY_CURRENT_USER\Software\Microsoft\Command Processor\EnableExtensions
>
>到 0x1 或 0x0。用户特定设置
>比机器设置有优先权。命令行
>开关比注册表设置有优先权。
>
>在批处理文件中，SETLOCAL ENABLEEXTENSIONS 或 DISABLEEXTENSIONS 参数
>比 /E:ON 或 /E:OFF 开关有优先权。请参阅 SETLOCAL /? 获取详细信息。
>
>命令扩展包括对下列命令所做的
>更改和/或添加:
>
>    DEL or ERASE
>    COLOR
>    CD or CHDIR
>    MD or MKDIR
>    PROMPT
>    PUSHD
>    POPD
>    SET
>    SETLOCAL
>    ENDLOCAL
>    IF
>    FOR
>    CALL
>    SHIFT
>    GOTO
>    START (同时包括对外部命令调用所做的更改)
>    ASSOC
>    FTYPE
>
>有关特定详细信息，请键入 commandname /? 查看。
>
>延迟环境变量扩展不按默认值启用。你
>可以用/V:ON 或 /V:OFF 开关，为 CMD.EXE 的某个调用而
>启用或停用延迟环境变量扩展。你
>可以在机器上和/或用户登录会话上启用或停用 CMD.EXE 所有
>调用的延迟扩展，这要通过设置使用 REGEDIT.EXE 的注册表中的
>一个或两个 REG_DWORD 值:
>
>    HKEY_LOCAL_MACHINE\Software\Microsoft\Command Processor\DelayedExpansion
>    
>        和/或
>    
>    HKEY_CURRENT_USER\Software\Microsoft\Command Processor\DelayedExpansion
>
>到 0x1 或 0x0。用户特定设置
>比机器设置有优先权。命令行开关
>比注册表设置有优先权。
>
>在批处理文件中，SETLOCAL ENABLEDELAYEDEXPANSION 或 DISABLEDELAYEDEXPANSION
>参数比 /V:ON 或 /V:OFF 开关有优先权。请参阅 SETLOCAL /?
>获取详细信息。
>
>如果延迟环境变量扩展被启用，
>惊叹号字符可在执行时间被用来
>代替一个环境变量的数值。
>
>你可以用 /F:ON 或 /F:OFF 开关为 CMD.EXE 的某个
>调用而启用或禁用文件名完成。你可以在计算上和/或
>用户登录会话上启用或禁用 CMD.EXE 所有调用的完成，
>这可以通过使用 REGEDIT.EXE 设置注册表中的下列
> REG_DWORD 的全部或其中之一:
>
>    HKEY_LOCAL_MACHINE\Software\Microsoft\Command Processor\CompletionChar
>    HKEY_LOCAL_MACHINE\Software\Microsoft\Command Processor\PathCompletionChar
>    
>        和/或
>    
>    HKEY_CURRENT_USER\Software\Microsoft\Command Processor\CompletionChar
>    HKEY_CURRENT_USER\Software\Microsoft\Command Processor\PathCompletionChar
>
>由一个控制字符的十六进制值作为一个特定参数(例如，0x4
>是Ctrl-D，0x6 是 Ctrl-F)。用户特定设置优先于机器设置。
>命令行开关优先于注册表设置。
>
>如果完成是用 /F:ON 开关启用的，两个要使用的控制符是:
>目录名完成用 Ctrl-D，文件名完成用 Ctrl-F。要停用
>注册表中的某个字符，请用空格(0x20)的数值，因为此字符
>不是控制字符。
>
>如果键入两个控制
>字符中的一个，完成会被调用。完成功能将路径字符串带到光标的左边，
>如果没有通配符，将通配符附加到左边，并建立相符的路径列表。然后，
>显示第一个相符的路径。
>如果没有相符的路径，则发出嘟嘟声，不影响显示。
>之后，重复按同一个控制
>字符会循环显示相符路径的列表。
>将 Shift 键跟控制字符同时按下，会倒着显示列表。
>如果对该行进行了任何编辑，并再次按下控制
>字符，保存的相符路径的列表会被丢弃，新的
>会被生成。如果在文件和目录名完成之间切换，
>会发生同样现象。两个控制字符之间的唯一区别是
>文件完成字符匹配文件和目录名，
>而目录完成字符只符合目录名。
>如果文件完成被用于内置式目录命令
>(CD、MD 或 RD)，就会使用目录完成。
>
>用引号将相符路径括起来，
>完成代码可以正确处理含有空格或其他特殊字符的文件名。
>同时，如果备份，然后从行内调用文件完成，
>则调用完成时位于光标右方的文字会被
>弃用。
>
>需要引号的特殊字符是:
><space>
> &()[]{}^=;!'+,`~
>
>C:\Users\liujun>

通过这个命令就可以看到在执行cmd命令时，加入/c 参数就可以一个命令来运行了，例如

>cmd.exe /c ping www.baidu.com

当执行后就可以看到:

![image-20210228173420367](C:\Users\liujun\AppData\Roaming\Typora\typora-user-images\image-20210228173420367.png)

然后窗口消失。这正是所要的。



接下来我对代码做一个改动吧，这需要接收一个集合为参数。

那看下完整的代码吧：

```java
public class SynchronousLocalShellCommand {

  private Logger logger = LoggerFactory.getLogger(SynchronousLocalShellCommand.class);

  /** 命令信息 */
  private final List<String> command;

  public SynchronousLocalShellCommand(List<String> command) {
    this.command = command;
  }

  /**
   * 执行命令并返回结果
   *
   * @return 命令执行结果
   */
  public String doCommand() {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      // 将错误输出流转移到标准输出流中,但使用Runtime不可以
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();
      String dataMsg = reader(process.getInputStream());
      int rsp = process.waitFor();
      logger.info("run command {}, response {}", command, rsp);
      return dataMsg;
    } catch (IOException | InterruptedException e) {
      logger.error("command : {} ,exception", command, e);
    }

    return null;
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

对测试代码也进行修改：

```java
![修改后的首次运行乱码](D:\java\workspace\selfwork\demojava8\src\main\java\com\liujun\command\img\修改后的首次运行乱码.png)public class TestSynchronousLocalShellCommand {

  /** 同步执行命令 */
  @Test
  public void synchornousDoCommand() {
    // 运行一个正常的命令
    this.runCommand("ping www.baidu.com");
    // 运行一个bat脚本
    this.runCommand("D:/run/bat/run.bat");
    // 错误命令
    this.runCommand("abcdef");
  }

  /**
   * 运行command
   *
   * @param commandStr 错误命令行
   */
  private void runCommand(String commandStr) {
    List<String> commandList = Arrays.asList("cmd.exe","/C",commandStr);
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandList);

    String commandRsp = command.doCommand();
    Assert.assertNotNull(commandRsp);
    System.out.println(commandRsp);
  }
}
```

当再次运行代码时：

![](D:\java\workspace\selfwork\demojava8\src\main\java\com\liujun\command\img\修改后的首次运行乱码.png)

还是存在乱码问题。

这个还是由于我本地windows使用的是GBK所引起的。那就先将解码改为GBK吧

```java
public class SynchronousLocalShellCommand {
......

  /**
   * 数据读取操作
   *
   * @param input 输入流
   */
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
}
```



当我再次运行时:

```shell
17:45:56.375 [main] INFO com.liujun.command.processbuilder.SynchronousLocalShellCommand - run command [cmd.exe, /C, ping www.baidu.com], response 0

正在 Ping www.a.shifen.com [112.80.248.75] 具有 32 字节的数据:
来自 112.80.248.75 的回复: 字节=32 时间=13ms TTL=55
来自 112.80.248.75 的回复: 字节=32 时间=15ms TTL=55
来自 112.80.248.75 的回复: 字节=32 时间=13ms TTL=55
来自 112.80.248.75 的回复: 字节=32 时间=13ms TTL=55

112.80.248.75 的 Ping 统计信息:
    数据包: 已发送 = 4，已接收 = 4，丢失 = 0 (0% 丢失)，
往返行程的估计时间(以毫秒为单位):
    最短 = 13ms，最长 = 15ms，平均 = 13ms

17:45:56.417 [main] INFO com.liujun.command.processbuilder.SynchronousLocalShellCommand - run command [cmd.exe, /C, D:/run/bat/run.bat], response 0

D:\java\workspace\selfwork\demojava8>echo C:\java\jdk8\jdk1.8.0_241 
C:\java\jdk8\jdk1.8.0_241

D:\java\workspace\selfwork\demojava8>echo "hello world!!" 
"hello world!!"

17:45:56.449 [main] INFO com.liujun.command.processbuilder.SynchronousLocalShellCommand - run command [cmd.exe, /C, abcdef], response 1
'abcdef' 不是内部或外部命令，也不是可运行的程序
或批处理文件。
```

可以发现。这三个命令都已经按照预期来执行了。



虽然是按预期的执行了，但是与Runtime的执行方式相比。还是存在着不同的。当一条命令不存在时，runtime是抛出一个异常。而ProcessBuilder则是在输出信息中提示了错误。





### 2.4 环境测试-linux

当我window测试完后成，就需要对linux做一个简单的测试了。也同样来看下linux测试的代码吧.

```java
public class ProcessBuilderLinuxMain {

  public static void main(String[] args) {
    ProcessBuilderLinuxMain instance = new ProcessBuilderLinuxMain();
    // 同步的执行
    instance.synchornousDoCommand();
  }


  /** 异步执行命令 */
  private void synchornousDoCommand() {
    this.runSyncCommand("ping -c 5 www.baidu.com");
    this.runSyncCommand("/home/liujun/datarun/shell/run.sh");
    this.runSyncCommand("adfadsfa");
  }

  /**
   * 执行同步的命令操作
   *
   * @param commandStr
   */
  private void runSyncCommand(String commandStr) {
    List<String> commandList = Arrays.asList("bash", "-c", commandStr);
    SynchronousLocalShellCommand command = new SynchronousLocalShellCommand(commandList);
    String commandRsp = command.doCommand();
    System.out.println("同步执行结果:" + commandRsp);
    System.out.println("结束---------------");
  }
}
```

有一个特别需要注意的问题那个就在linux上执行命令一般用的是bash。这个参数需要查下API。直接给结果。参数使用-c,

这个在linux下，编码需要切换下：

```java
InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.UTF_8);
```

再来看下执行结果:

```shell
[liujun@fk03 datarun]$ java -cp demojava8-0.0.1-SNAPSHOT.jar:./lib/* com.liujun.command.ProcessBuilderLinuxMain
19:20:20.855 [main] INFO com.liujun.command.processbuilder.SynchronousLocalShellCommand - run command [bash, -c, ping -c 5 www.baidu.com], response 0
同步执行结果:PING www.a.shifen.com (180.101.49.12) 56(84) bytes of data.
64 bytes from 180.101.49.12 (180.101.49.12): icmp_seq=1 ttl=52 time=10.1 ms
64 bytes from 180.101.49.12 (180.101.49.12): icmp_seq=2 ttl=52 time=8.37 ms
64 bytes from 180.101.49.12 (180.101.49.12): icmp_seq=3 ttl=52 time=8.13 ms
64 bytes from 180.101.49.12 (180.101.49.12): icmp_seq=4 ttl=52 time=8.25 ms
64 bytes from 180.101.49.12 (180.101.49.12): icmp_seq=5 ttl=52 time=9.60 ms

--- www.a.shifen.com ping statistics ---
5 packets transmitted, 5 received, 0% packet loss, time 4006ms
rtt min/avg/max/mdev = 8.135/8.900/10.135/0.819 ms

结束---------------
19:20:20.866 [main] INFO com.liujun.command.processbuilder.SynchronousLocalShellCommand - run command [bash, -c, /home/liujun/datarun/shell/run.sh], response 0
同步执行结果:Hello World !

结束---------------
19:20:20.869 [main] INFO com.liujun.command.processbuilder.SynchronousLocalShellCommand - run command [bash, -c, adfadsfa], response 127
同步执行结果:bash: adfadsfa: command not found

结束---------------
[liujun@fk03 datarun]$ 
```

通过观察发现，三条命令都会得到相应的结果。



## 3.需要异步执行的场景

当命令长时间执行或者有的脚本边执行边有信息输出时，我们就不能等着命令执行结束了。再才给出提示。而是应该边执行就边给出提示。那还是先来看看代码实现吧。

### 3.1 代码实现

```java
public class AsynchronousLocalShellCommand implements Runnable {

  private Logger logger = LoggerFactory.getLogger(AsynchronousLocalShellCommand.class);

  /** 命令信息 */
  private final List<String> command;

  /** 运行的处理流程 */
  private Process process;

  /** 数据的输出流 */
  private InputStream input;

  /** 输出信息 */
  private final StringBuilder outDat = new StringBuilder();

  /** 进程执行结束后的结果,-1 初始化，0，成功执行结束 */
  private AtomicInteger processOutCode = new AtomicInteger(-1);

  /** 运行标识 */
  private AtomicBoolean runFlag = new AtomicBoolean(true);

  public AsynchronousLocalShellCommand(List<String> command) {
    this.command = command;
  }

  /** 开始执行命令 */
  public void doCommand() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      // 将错误输出流转移到标准输出流中,但使用Runtime不可以
      processBuilder.redirectErrorStream(true);
      this.process = processBuilder.start();
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }

    // 获取子进程的输入流。输入流获得由该 Process 对象表示的进程的标准输出流。
    input = process.getInputStream();

    // 成功时才将任务提交线程池运行
    TaskThreadDataPool.INSTANCE.submit(this);
  }

  @Override
  public void run() {
    // 进行数据读取操作
    reader(this.input);

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
  public String getOutDat() {
    return outDat.toString();
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
    // 停止子进程
    process.destroy();
  }

  /**
   * 数据读取操作
   *
   * @param input 输入流
   */
  private void reader(InputStream input) {
    try (InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null && runFlag.get()) {
        outDat.append(line);
        outDat.append(Symbol.LINE);
      }
    } catch (IOException e) {
      logger.error("command : {} ,exception", command, e);
    }
  }
}
```

### 3.2 环境测试-windows

windows环境还是使用单元测试:

```java
public class TestAsynchronousLocalShellCommand {

  /** 测试异步命令的执行 */
  @Test
  public void asynchronous() {
    // 运行一个正常的命令
    this.runCommand("ping www.baidu.com");
    // 运行一个bat脚本
    this.runCommand("D:/run/bat/run.bat");
    // 错误命令
    this.runCommand("abcdef");

    TaskThreadDataPool.INSTANCE.shutdown();
  }

  private void runCommand(String commandStr) {
    List<String> commandList = Arrays.asList("cmd.exe", "/c", commandStr);
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandList);
    command.doCommand();
    for (int i = 0; i < 3; i++) {
      int code = command.rspProcess();
      String outData = command.getOutDat();
      Assert.assertNotNull(outData);
      System.out.println("第" + i + ",结果码,code:" + code + ",rspmsg:" + outData);
      ThreadUtils.sleep(1);
    }

    // 不再运行
    command.stop();

    int code = command.rspProcess();
    String outData = command.getOutDat();

    System.out.println("命令" + commandStr + ",结果码,code:" + code + ",rspmsg:" + outData);
    System.out.println("结束----------");
  }
}
```



同样的先来看看测试代码的运行结果：

```java
第0,结果码,code:-1,rspmsg:
第1,结果码,code:-1,rspmsg:
正在 Ping www.a.shifen.com [180.101.49.11] 具有 32 字节的数据:
来自 180.101.49.11 的回复: 字节=32 时间=12ms TTL=50

第2,结果码,code:-1,rspmsg:
正在 Ping www.a.shifen.com [180.101.49.11] 具有 32 字节的数据:
来自 180.101.49.11 的回复: 字节=32 时间=12ms TTL=50
来自 180.101.49.11 的回复: 字节=32 时间=12ms TTL=50

命令ping www.baidu.com,结果码,code:-1,rspmsg:
正在 Ping www.a.shifen.com [180.101.49.11] 具有 32 字节的数据:
来自 180.101.49.11 的回复: 字节=32 时间=12ms TTL=50
来自 180.101.49.11 的回复: 字节=32 时间=12ms TTL=50
来自 180.101.49.11 的回复: 字节=32 时间=13ms TTL=50

结束----------
第0,结果码,code:-1,rspmsg:
19:50:08.443 [command-2] INFO com.liujun.command.processbuilder.AsynchronousLocalShellCommand - run command [cmd.exe, /c, D:/run/bat/run.bat], response 0
19:50:08.491 [command-1] INFO com.liujun.command.processbuilder.AsynchronousLocalShellCommand - run command [cmd.exe, /c, ping www.baidu.com], response 1
第1,结果码,code:0,rspmsg:
D:\java\workspace\selfwork\demojava8>echo C:\java\jdk8\jdk1.8.0_241 
C:\java\jdk8\jdk1.8.0_241

D:\java\workspace\selfwork\demojava8>echo "hello world!!" 
"hello world!!"

第2,结果码,code:0,rspmsg:
D:\java\workspace\selfwork\demojava8>echo C:\java\jdk8\jdk1.8.0_241 
C:\java\jdk8\jdk1.8.0_241

D:\java\workspace\selfwork\demojava8>echo "hello world!!" 
"hello world!!"

命令D:/run/bat/run.bat,结果码,code:0,rspmsg:
D:\java\workspace\selfwork\demojava8>echo C:\java\jdk8\jdk1.8.0_241 
C:\java\jdk8\jdk1.8.0_241

D:\java\workspace\selfwork\demojava8>echo "hello world!!" 
"hello world!!"

结束----------
第0,结果码,code:-1,rspmsg:
19:50:11.444 [command-2] INFO com.liujun.command.processbuilder.AsynchronousLocalShellCommand - run command [cmd.exe, /c, abcdef], response 1
第1,结果码,code:1,rspmsg:'abcdef' 不是内部或外部命令，也不是可运行的程序
或批处理文件。

第2,结果码,code:1,rspmsg:'abcdef' 不是内部或外部命令，也不是可运行的程序
或批处理文件。

命令abcdef,结果码,code:1,rspmsg:'abcdef' 不是内部或外部命令，也不是可运行的程序
或批处理文件。

结束----------

```

注：windows平台上编码使用

观察代码的执行结果 。可发现命令都正确的执行。并得到了预期的结果。

### 3.3 环境测试-Linux测试

那接下来就是linux环境环境测试了。

还是先看代码:

```java
public class ProcessBuilderLinuxMain {

  public static void main(String[] args) {
    ProcessBuilderLinuxMain instance = new ProcessBuilderLinuxMain();
    // 异步的执行
    instance.asynchronousDoCommand();
  }

  /** 异步执行命令 */
  private void asynchronousDoCommand() {
    // 执行一个正常的命令，带终止
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
    List<String> commandList = Arrays.asList("bash", "-c", commandStr);
    AsynchronousLocalShellCommand command = new AsynchronousLocalShellCommand(commandList);
    command.doCommand();
    for (int i = 0; i < 3; i++) {
      int code = command.rspProcess();
      String outData = command.getOutDat();
      System.out.println("当前第:" + i + "次，code:" + code + "，响应结果 :" + outData);
      ThreadUtils.sleep(1);
    }

    // 不再运行
    command.stop();

    int code = command.rspProcess();
    String outData = command.getOutDat();

    System.out.println("结果状态：" + code + "，执行结果:" + outData);
    System.out.println("结束--------");
  }
}
```

然后看下结果:

```
[liujun@fk03 datarun]$ java -cp demojava8-0.0.1-SNAPSHOT.jar:./lib/* com.liujun.command.ProcessBuilderLinuxMain
当前第:0次，code:-1，响应结果 :
当前第:1次，code:-1，响应结果 :PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=9.35 ms

当前第:2次，code:-1，响应结果 :PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=9.35 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=10.0 ms

结果状态：-1，执行结果:PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=1 ttl=52 time=9.35 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=2 ttl=52 time=10.0 ms
64 bytes from 180.101.49.11 (180.101.49.11): icmp_seq=3 ttl=52 time=9.22 ms

结束--------
当前第:0次，code:-1，响应结果 :
21:55:53.227 [command-2] INFO com.liujun.command.processbuilder.AsynchronousLocalShellCommand - run command [bash, -c, /home/liujun/datarun/shell/run.sh], response 0
21:55:53.226 [command-1] INFO com.liujun.command.processbuilder.AsynchronousLocalShellCommand - run command [bash, -c, ping www.baidu.com], response 143
当前第:1次，code:0，响应结果 :Hello World !

当前第:2次，code:0，响应结果 :Hello World !

结果状态：0，执行结果:Hello World !

结束--------
当前第:0次，code:-1，响应结果 :
21:55:56.227 [command-2] INFO com.liujun.command.processbuilder.AsynchronousLocalShellCommand - run command [bash, -c, adfadsfa], response 127
当前第:1次，code:127，响应结果 :bash: adfadsfa: command not found

当前第:2次，code:127，响应结果 :bash: adfadsfa: command not found

结果状态：127，执行结果:bash: adfadsfa: command not found

结束--------
[liujun@fk03 datarun]$ 
```

通过结果的观察发现在linux上执行也都返回了预期的结果。



## 4. 对比Runtime与ProcessBuilder

通过上一个篇讲解Runtime方式与本篇讲解ProcessBuilder的运行。对于这两种方式。对于其差异做一个小结。

Runtime方式： 此为最常见的一种运行方式，历史最悠久，使应用程序能够与其运行的环境相连接，但是在读取上还存在一些不便性，正常的输出流与错误流得分开读取。其他功能基本相同。

ProcessBuilder：此为jdk1.5加入的，它没有将应用程序与其运行的环境相连接。这个就需要自己设置其相关的信息。但它提供了将正常流与流程流合并在一起的解决办法，只需要设置redirectErrorStream属性即可。

最后讲他他们的联系吧。

通过翻看

Runtime的源码 ：

```java
public class Runtime {
......
    public Process exec(String command) throws IOException {
        return exec(command, null, null);
    }
......
    public Process exec(String command, String[] envp, File dir)
        throws IOException {
        if (command.length() == 0)
            throw new IllegalArgumentException("Empty command");

        StringTokenizer st = new StringTokenizer(command);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            cmdarray[i] = st.nextToken();
        return exec(cmdarray, envp, dir);
    }
......
    public Process exec(String[] cmdarray, String[] envp, File dir)
        throws IOException {
        return new ProcessBuilder(cmdarray)
            .environment(envp)
            .directory(dir)
            .start();
    }    
......    
}    
```

最后这个Runtime调用的也是ProcessBuilder只是加入了环境信息。和相关的目录信息。



## 5.总结

通过本章节，我对使用ProcessBuilder做了一个比较详细的解释。也对其相关的场景做了一些简单的测试。通过两种方式的对比及实现。在实际的项目中我还是推荐使用ProcessBuilder，这种方式可以将错误流与正常流进行合并，操作上带来了一些便利。