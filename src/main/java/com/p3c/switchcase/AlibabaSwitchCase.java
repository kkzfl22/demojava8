package com.p3c.switchcase;

/**
 * 控制语句
 *
 * <p>1,在一个switch块内，第个case要么通bread/return等来终止，要么注释说明程序将继续执行到哪一个case为止；
 * 在一个switch块内，都必须包含一个default语句，并且放在最后，即使它什么代码也没有。
 *
 * <p>2，在if/else/for/while/do等语句中，必须使用大括号。即使只有一行代码，也应避免采用单行的编码方式；
 *
 * <p>3，在高并发场景中，避免使用"等于"判断 作为中断或者退出的条件 说明，如果并发控制没有处理好，容易首先等值判断被“击穿”的情况，应用使用大于或者小于的区间判断条件来代替。
 *
 * <p>4，在表达异步的分支时，尽量少于if-else方式，如果不得不使用if()..else if()..else，请勿超过3层。
 *
 * <p>5，除常用的方法（如getXXX/isXXX）外，不要在条件判断中执行其他复杂的语句，可将复杂逻辑判断的结果赋值给一个有意义的布尔变量名，以提高可读取性
 *
 * <p>6,循环体中的语句要老师性能。以下操作尽量至循环体外处理，如定义对象或变量、获取数据库连接、避免不必要的try-catch操作。
 *
 * <p>7，避免采用取反逻辑运算符。
 *
 * <p>8，接口入参保护，这种场景常见的是作批量操作的接口。
 *
 * <p>9，下列情形需要进行参数校验；
 *
 * <p>9.1）调用频次低的方法。
 *
 * <p>9.2）执行时间开销很大的方法。情情形中参数校验时间机乎可以忽略不计，但如果因为参数错误导致中间执行回退，或者错误，那就得不偿失。
 *
 * <p>9.3）需要极高稳定性和可用性的方法。
 *
 * <p>9.4）对外提供的开放接口，不管是否为RPC/API/HTTP接口。 9.5）敏感权限入口
 *
 * <p>10,下列情形不需要进行参数校验
 *
 * 10.1)极有可能被循环调用的方法。但方法说明里必须注意外部参数检查要求。
 *
 * 10.2)底层调用频度比较高的方法，
 *
 * 10.3）被声明成private只会被自己代码所调用的方法，如果能够确定调用方法的代码传入参数已经做过检查或者肯定不会有问题
 */
public class AlibabaSwitchCase {}
