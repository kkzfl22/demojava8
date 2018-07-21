package com.demo.effectivejava.thirty.seven.demo;

import java.io.Serializable;

public class TriskResult implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String cid;

	private String criskId;

	private String criskKey;

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCriskId() {
		return criskId;
	}

	public void setCriskId(String criskId) {
		this.criskId = criskId;
	}

	public String getCriskKey() {
		return criskKey;
	}

	public void setCriskKey(String criskKey) {
		this.criskKey = criskKey;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TriskResult [cid=");
		builder.append(cid);
		builder.append(", criskId=");
		builder.append(criskId);
		builder.append(", criskKey=");
		builder.append(criskKey);
		builder.append("]");
		return builder.toString();
	}

}
