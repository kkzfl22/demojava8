package com.p3c.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集合处理
 *
 * <p>1，关于hashCode和equals的处理，遵循如下规则
 *
 * <p>1.1)只要重写equals，就必须重写hashCode.
 *
 * <p>1.2)因为Set存储的是不重复的对象，依据hashCode和equals进行判断 ，所以Set存储的对象必须重写这两种上方法。
 *
 * <p>1.3）如果自定义对象作为Map的键，那么必须重写hashCode和equals
 *
 * <p>2,ArrayList的SubList结果不可强转成ArrayList，否则会抛出ClasssCaseException异常,即java.util.RadomAccessSubList
 * cannt be cast to java.util.ArrayList
 *
 * <p>3,在subList场景中，高度注意对集合元素个数的修改，会导致子列表的遍历、增加、删除均产生ConcurrentModificationException异常.
 *
 * <p>4,使用集合转数组的方法，必须使用集合的toArray(T[] array)，传入的是类型完全一样的数组，大小就是list.size()
 *
 * <p>5,在使用工具类Arrays.asList()把数据转换成集合时，不能使用其修改集合相关的方法，它的add/remove/clear方法会抛出
 * UnsupportedOperationException异常
 *
 * <p>6,泛型通配符<? extends T>用来接收返回的数据，此写法的泛型不能使用add方法，而<? supper T>不能使用get方法， 因为其作为 接口调用接口赋值时易出错
 *
 * <p>7，不要在foreach循环里进行元素的remove/add操作。remove元素使用Iterator方式，如果并发操作，需要对Iterator对象加锁
 *
 * <p>8,在JDK7及以上版本中，Comparator要满足如下三个条件，不然Arrays.sort,collections.sort会报IllegalArgumentExctption
 *
 * <p>8.1)x,y的比较结果和y,x的比较结果相反。
 *
 * <p>8.2）x > Y ,y > z ，则x > z
 *
 * <p>8.3) x=y,则x,z比较结果和y、z比较结果相同.
 *
 * <p>9,在集合初始化时指定集合初始化大小
 *
 * <p>10，使用entrySet遍历类集合K/V，而不是使用keySet方式遍历
 *
 * 11,高度注意Map类集合K/V能不能存储null值的情况
 *
 * 11.1)Hashtable key不能允许为null value 不允许为null,super:Dictionary 说明:线程安全
 *
 * 11.2)ConcurrentHashMap key不能允许为null, value 不允许为null super:AbstractMap 说明:锁分段技术(JDK8&CAS)
 *
 * 11.3)TreeMap key不允许为null value 允许为null Super: AbstractMap 说明:线程不安全
 *
 * 11.4)HashMap key允许为null,value 允许为null,Super:AbstractMap 说明线程不安全
 *
 * 12，参考：合理利用集合的有序性(sort)和稳定性(order)，避免集合的无序性(unsort)和不稳定性(unorder)带来的负面影响.
 * 有序性:遍历的结果是按某种比较规则依次排列的。
 * 稳定性：集合每次遍历的元素次序是一定的。
 *
 * 13，参考：利用Set元素唯一的特性，可以快速对一个集合进行云重操作，避免使用List的contains方法进行遍历，对比、去重操作。
 *
 *
 */
public class AlibabaCollection {

  public void listSubList() {
    List<String> list = new ArrayList<>();

    list.add("1111");
    list.add("2222");
    list.add("3333");
    list.add("4444");

    List<String> subList = list.subList(0, 2);

    System.out.println(subList);

    // return SubList
    ArrayList<String> subToCaseArray = (ArrayList<String>) subList;
  }

  public void hashMapUse() {
    // 在initialCapacity=(需要存储元素个数/负载因子）+1。注意负载因子(即loader factor）默认为0.75,如果暂时无法确定初始值，请设置为16(即默认值）
    Map<String, String> map = new HashMap<>(32);
  }

  public static void main(String[] args) {
    AlibabaCollection collection = new AlibabaCollection();
    collection.listSubList();
  }
}
