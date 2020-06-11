package ru.krtech.rsmev.sftp_sync;

import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo {

	private String password;

	public MyUserInfo(String password) {
		this.password = password;
	}
	
	@Override
	public String getPassphrase() {
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean promptPassword(String message) {
		return false;
	}

	@Override
	public boolean promptPassphrase(String message) {
		return false;
	}

	@Override
	public boolean promptYesNo(String message) {
		return false;
	}

	@Override
	public void showMessage(String message) {
	}

}
