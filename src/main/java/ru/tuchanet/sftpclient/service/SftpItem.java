package ru.tuchanet.sftpclient.service;

public class SftpItem {

	private String name;
	private boolean isDir = false;
	private int mtime = 0;
	private long size = 0;

	public SftpItem() {}

	public SftpItem(String name, boolean isDir, int mtime, long l) {
		super();
		this.name = name;
		this.isDir = isDir;
		this.mtime = mtime;
		this.size = l;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public int getMtime() {
		return mtime;
	}

	public void setMtime(int mtime) {
		this.mtime = mtime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
}
