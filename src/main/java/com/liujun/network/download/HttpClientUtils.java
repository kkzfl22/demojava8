package com.liujun.network.download;

import com.utils.retry.RetryFunctionImpl;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.*;

/**
 * 下载极客邦的音频文件
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/28
 */
public class HttpClientUtils {

  public static final HttpClientUtils INSTANCE = new HttpClientUtils();

  /** 输入下载的参数 */
  private static final String DOWNLOAD_PARAM_URL = "url";

  /** 输出参数 */
  private static final String DOWNLOAD_PARAM_OUTPUT = "output";

  /** 参数的id */
  private static final String DOWNLOAD_PARAM_ID = "download_id";

  public static void main(String[] args) {

    //    String url =
    //        "https://res001.geekbang.org/media/audio/74/a9/74e20b5883f0d5ce4603b0fd6c398aa9/ld/";
    //    String outPath = "D:/java/test/meda/sksj/003/";
    //    INSTANCE.runFileFlow(19, 42, outPath, url, "003.ts");

    String url = "https://res001.geekbang.org/media/audio/9e/e7/9e9694e286b2c5b19d0bf8c97961abe7/";
    String name = "009";
    String outPath = "D:/java/test/meda/sksj/" + name + "/";
    INSTANCE.runFileFlow(10, 33, outPath, url, name + ".ts", "ld-");
    System.out.println("下载完成");
  }

  public void runFileFlow(
      int min, int sec, String outPath, String url, String margeName, String suffix) {
    List<String> list = INSTANCE.countDownloadFIle(min, sec, suffix);

    File outFs = new File(outPath);

    if (!outFs.exists()) {
      outFs.mkdirs();
    }

    for (int i = 0; i < list.size(); i++) {
      String item = list.get(i);
      retryMethod(url, outPath, item);

      System.out.println("download over :" + item);
    }

    FileMarge.MARGEINSTANCE.margetFile(outPath, outPath + margeName);
  }

  public List<String> countDownloadFIle(int min, int sec, String suffix) {
    int maxSec = min * 60 + sec;

    int maxBig = 5;

    int runcount = maxSec % 10;

    runcount = runcount == 0 ? maxSec / 10 + 1 : maxSec / 10 + 2;

    List<String> list = new ArrayList<>(runcount);

    for (int i = 1; i <= runcount; i++) {
      StringBuilder resultMsg = new StringBuilder();

      resultMsg.append(suffix);
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

  public void retryMethod(String url, String outPath, String nameid) {
    Map<String, Object> runParam = new HashMap<>();

    runParam.put(DOWNLOAD_PARAM_URL, url);
    runParam.put(DOWNLOAD_PARAM_OUTPUT, outPath);
    runParam.put(DOWNLOAD_PARAM_ID, nameid);

    Boolean downRsp = (Boolean) RetryFunctionImpl.INSTANCE.applyRun(this::retryDownload, runParam);

    System.out.println("下载结果:" + downRsp);
  }

  /**
   * 进行下载重试操作
   *
   * @param param 参数信息
   * @return 参数信息
   */
  public boolean retryDownload(Map<String, Object> param) {

    String url = String.valueOf(param.get(DOWNLOAD_PARAM_URL));
    String outPath = String.valueOf(param.get(DOWNLOAD_PARAM_OUTPUT));
    String downloadId = String.valueOf(param.get(DOWNLOAD_PARAM_ID));

    try {
      downLoadByHttpClient(url, outPath, downloadId);

      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  public static void downLoadByHttpClient(String url, String outPath, String nameid)
      throws IOException {

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
        throw e;
      } finally {
        IOUtils.closeQuietly(out);
      }
    } else {
      System.out.println("Demo.example -------->" + "获取信息失败");
    }
  }
}
