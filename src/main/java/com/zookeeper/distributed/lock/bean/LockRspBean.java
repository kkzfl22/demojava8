package com.zookeeper.distributed.lock.bean;

/**
 * 加锁成功
 *
 * @since 2017年6月30日 上午11:28:13
 * @author liujun
 * @version 0.0.1
 */
public class LockRspBean {

  /** 加锁的结果 */
  private boolean rsp = false;

  /** 加锁的路径 */
  private String lockPath;

  /** 等待的节点信息 */
  private String waitPath;

  public LockRspBean(boolean rsp, String lockPath) {
    super();
    this.rsp = rsp;
    this.lockPath = lockPath;
  }

  public LockRspBean(boolean rsp, String lockPath, String waitPath) {
    super();
    this.rsp = rsp;
    this.lockPath = lockPath;
    this.waitPath = waitPath;
  }

  public boolean isRsp() {
    return rsp;
  }

  public void setRsp(boolean rsp) {
    this.rsp = rsp;
  }

  public String getLockPath() {
    return lockPath;
  }

  public void setLockPath(String lockPath) {
    this.lockPath = lockPath;
  }

  public String getWaitPath() {
    return waitPath;
  }

  public void setWaitPath(String waitPath) {
    this.waitPath = waitPath;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LockRspBean [rsp=");
    builder.append(rsp);
    builder.append(", lockPath=");
    builder.append(lockPath);
    builder.append(", waitPath=");
    builder.append(waitPath);
    builder.append("]");
    return builder.toString();
  }
}
