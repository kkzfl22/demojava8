package com.liujun.network.download;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 下载极客邦的音频文件
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/28
 */
public class HttpClientUtils {

  private static final HttpClientUtils INSTANCE = new HttpClientUtils();

  public static void main(String[] args) {

    //    String url =
    //        "https://res001.geekbang.org/media/audio/74/a9/74e20b5883f0d5ce4603b0fd6c398aa9/ld/";
    //    String outPath = "D:/java/test/meda/sksj/003/";
    //    INSTANCE.runFileFlow(19, 42, outPath, url, "003.ts");

    String url =
        "https://res001.geekbang.org/media/audio/e6/a1/e6e57438c034f24899cabc658ee62ba1/ld/";
    String outPath = "D:/java/test/meda/sksj/004/";
    INSTANCE.runFileFlow(12, 44, outPath, url, "004.ts");
  }

  public void runFileFlow(int min, int sec, String outPath, String url, String margeName) {
    List<String> list = INSTANCE.countDownloadFIle(min, sec);

    File outFs = new File(outPath);

    if (!outFs.exists()) {
      outFs.mkdirs();
    }

    for (int i = 0; i < list.size(); i++) {
      String item = list.get(i);
      downLoadByHttpClient(url, outPath, item);

      System.out.println("download over :" + item);
    }

    INSTANCE.margetFile(outPath, outPath + margeName);
  }

  public void margetFile(String inputPath, String margeOutFile) {
    File readfile = new File(inputPath);

    if (readfile.exists()) {
      String[] name = readfile.list();

      Arrays.sort(name);

      for (int i = 0; i < name.length; i++) {
        marge(inputPath + "/" + name[i], margeOutFile);
      }
    }
  }

  public void marge(String input, String output) {
    FileInputStream inputStream = null;
    FileOutputStream outputStream = null;
    try {
      inputStream = new FileInputStream(input);
      outputStream = new FileOutputStream(output, true);

      byte[] buffer = new byte[8 * 1024];
      int readIndex = -1;

      while ((readIndex = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, readIndex);
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e2) {
      e2.printStackTrace();
    } finally {
      IOUtils.closeQuietly(outputStream);
      IOUtils.closeQuietly(inputStream);
    }

    new File(input).delete();
  }

  public List<String> countDownloadFIle(int min, int sec) {
    int maxSec = min * 60 + sec;

    int maxBig = 5;

    int runcount = maxSec % 10;

    runcount = runcount == 0 ? maxSec / 10 : maxSec / 10 + 2;

    List<String> list = new ArrayList<>(runcount);

    for (int i = 1; i <= runcount; i++) {
      StringBuilder resultMsg = new StringBuilder();

      resultMsg.append("ld-");
      String curBig = String.valueOf(i);
      for (int j = curBig.length(); j < maxBig; j++) {
        resultMsg.append("0");
      }
      resultMsg.append(i);
      resultMsg.append(".ts");
      list.add(resultMsg.toString());
    }

    return list;
  }

  public static void downLoadByHttpClient(String url, String outPath, String nameid) {

    // 创建httpclient实例，采用默认的参数配置
    CloseableHttpClient httpClient = HttpClients.createDefault();
    // 使用Get方法提交
    HttpGet get = new HttpGet(url + nameid);

    // 请求的参数配置，分别设置连接池获取连接的超时时间，连接上服务器的时间，服务器返回数据的时间
    RequestConfig config =
        RequestConfig.custom()
            .setConnectionRequestTimeout(5000)
            .setConnectTimeout(5000)
            .setSocketTimeout(5000)
            .build();
    // 配置信息添加到Get请求中
    get.setConfig(config);
    // 通过httpclient的execute提交 请求 ，并用CloseableHttpResponse接受返回信息
    CloseableHttpResponse response = null;
    try {
      response = httpClient.execute(get);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 服务器返回的状态
    int statusCode = response.getStatusLine().getStatusCode();
    // 判断返回的状态码是否是200 ，200 代表服务器响应成功，并成功返回信息
    if (statusCode == HttpStatus.SC_OK) {
      // EntityUtils 获取返回的信息。官方不建议使用使用此类来处理信息
      //            System.out.println("Demo.example -------->" +
      // EntityUtils.toString(response.getEntity() , Consts.UTF_8));

      FileOutputStream out = null;
      try {
        out = new FileOutputStream(outPath + nameid);
        IOUtils.copy(response.getEntity().getContent(), out);
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        IOUtils.closeQuietly(out);
      }
    } else {
      System.out.println("Demo.example -------->" + "获取信息失败");
    }
  }
}
