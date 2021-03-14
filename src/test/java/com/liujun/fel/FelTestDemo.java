//package com.liujun.fel;
//
//import com.greenpineyu.fel.Expression;
//import com.greenpineyu.fel.FelEngine;
//import com.greenpineyu.fel.FelEngineImpl;
//import com.greenpineyu.fel.context.ArrayCtxImpl;
//import com.greenpineyu.fel.context.FelContext;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author liujun
// * @version 0.0.1
// */
//public class FelTestDemo {
//
//
//    /**
//     * 测试性能
//     */
//    private static void speed() {
//        final Map<String, Object> vars = new HashMap<>();
//        vars.put("i", 100);
//        vars.put("pi", 3.14d);
//        vars.put("d", -3.9);
//        vars.put("b", (byte) 4);
//        vars.put("bool", false);
//        final Map<String, Integer> m = new HashMap<String, Integer>();
//        m.put("d", 5);
//        vars.put("m", m);
//        vars.put("s", "hello world");
//
//        String[] exps = new String[10];
//        int index = 0;
//        exps[index++] = "1000+100.0*99-(600-3*15)%(((68-9)-3)*2-100)+10000%7*71";
//        exps[index++] = "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ==i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ";
//        exps[index++] = "pi*d+b-(1000-d*b/pi)/(pi+99-i*d)-i*pi*d/b";
//        exps[index++] = "s.substring(m.d )";
//        exps[index++] = "s.substring(1).substring(2).indexOf('world')";
//
//        JavaExp[] javaEls = buildJavaExp(vars, m);
//
//        int times = 100 * 1000 * 1000;
//        int j = 0;
//        for (String exp : exps) {
//            if (exp == null) {
//                break;
//            }
//            fel(exp, vars, times);
//            javaEl(javaEls[j++],times);
//        }
//    }
//
//    private static JavaExp[] buildJavaExp(final Map<String, Object> vars,
//                                          final Map<String, Integer> m) {
//        int index = 0;
//        JavaExp[] javaEls= new JavaExp[10];
//        javaEls[index++] = new JavaExp(){
//            public Object eval() {
//                return 1000+100.0*99-(600-3*15)%(((68-9)-3)*2-100)+10000%7*71;
//            };
//        };
//        javaEls[index++] = new JavaExp(){
//            public Object eval() {
//                int i = (Integer)vars.get("i");
//                double pi = (Double)vars.get("pi");
//                double d = (Double)vars.get("d");
//                byte b = (Byte)vars.get("b");
//                return i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ==i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ;
//            };
//        };
//        javaEls[index++] = new JavaExp(){
//            public Object eval() {
//                int i = (Integer)vars.get("i");
//                double pi = (Double)vars.get("pi");
//                double d = (Double)vars.get("d");
//                byte b = (Byte)vars.get("b");
//                return pi*d+b-(1000-d*b/pi)/(pi+99-i*d)-i*pi*d/b;
//            };
//        };
//        javaEls[index++] = new JavaExp(){
//            public Object eval() {
//                String s = (String) vars.get("s");
//                return s.substring(m.get("d"));
//            };
//        };
//        javaEls[index++] = new JavaExp(){
//            public Object eval() {
//                String s = (String) vars.get("s");
//                return s.substring(1).substring(2).indexOf("world");
//            };
//        };
//        return javaEls;
//    }
//
//    private static long javaEl(JavaExp el,int times){
//        long start = System.currentTimeMillis();
//        Object result = null;
//        int i = 0;
//        while (i++ < times) {
//            result = el.eval();
//        }
//        long end = System.currentTimeMillis();
//        long cost = end - start;
//        System.out.println("java el --------cost[" + cost + " ]---value[" + result
//                + "]");
//        return cost;
//    }
//
//
//
//    private static void fel(String exp, Map<String, Object> vars, int times) {
//        FelContext ctx = new ArrayCtxImpl(vars);
//        fel(exp, ctx, times);
//    }
//
//    private static long fel(String exp, FelContext ctx, int times) {
//        FelEngine engine = new FelEngineImpl();
//        Expression expObj = engine.compile(exp, ctx);
//        Object evalResult = null;
//        long start = System.currentTimeMillis();
//        Object result = null;
//        int i = 0;
//        while (i++ < times) {
//            result = expObj.eval(ctx);
//        }
//        long end = System.currentTimeMillis();
//        long cost = end - start;
//        System.out.println("fel --------cost[" + cost + " ]---value[" + result
//                + "] ------exp[" + exp + "]");
//        return cost;
//    }
//
//}
