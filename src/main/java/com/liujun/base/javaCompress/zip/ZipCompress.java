package com.liujun.base.javaCompress.zip;

import com.utils.IOUtils;

import java.io.*;
import java.util.zip.*;

/**
 * 进行文件压缩成zip包格式
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/06/26
 */
public class ZipCompress {

  public static final ZipCompress INSTANCE = new ZipCompress();

  /**
   * 读取指定目录下的文件，将文件压缩成指定的包名
   *
   * @param srcPath
   * @param outzipName
   */
  public void zipCompress(String srcPath, String outzipName) {

    File outPath = new File(outzipName);

    FileOutputStream foutputStream = null;
    // 使用指定校验和创建输出流
    CheckedOutputStream csum = null;
    // 缓冲区输出
    BufferedOutputStream bufferOutput = null;
    // 输出流
    ZipOutputStream zos = null;
    try {
      // ----压缩文件：
      foutputStream = new FileOutputStream(outPath);
      // 进行文件的校验
      csum = new CheckedOutputStream(foutputStream, new CRC32());
      // 设置缓冲区
      bufferOutput = new BufferedOutputStream(csum);
      // 压缩输出流
      zos = new ZipOutputStream(bufferOutput);

      // 设置为只打包，不压缩
      zos.setLevel(Deflater.NO_COMPRESSION);

      File srcFile = new File(srcPath);

      // 进行打zip包
      this.zip(zos, srcFile, "");

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.close(zos);
      IOUtils.close(bufferOutput);
      IOUtils.close(csum);
      IOUtils.close(foutputStream);
    }
  }

  private void zip(ZipOutputStream zos, File finput, String basePath) throws IOException {

    if (finput.isDirectory()) {
      File[] flist = finput.listFiles();

      if (flist.length == 0) {
        zos.putNextEntry(new ZipEntry(basePath));
      }

      for (File fileItem : flist) {
        String filePath = basePath + "/" + fileItem.getName();

        zip(zos, fileItem, filePath);
      }

    } else {
      // 添加文件实体
      zos.putNextEntry(new ZipEntry(basePath));
      // 将文件加入到压缩包中
      this.fileCopy(finput, zos);
    }
  }

  private void fileCopy(File finput, ZipOutputStream zos) {
    FileInputStream read = null;
    BufferedInputStream buffredInput = null;
    try {
      read = new FileInputStream(finput);
      buffredInput = new BufferedInputStream(read);

      byte[] bufferData = new byte[1024 * 256];

      int size = 0;

      while ((size = buffredInput.read(bufferData)) != -1) {
        zos.write(bufferData, 0, size);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.close(buffredInput);
      IOUtils.close(read);
    }
  }
}
