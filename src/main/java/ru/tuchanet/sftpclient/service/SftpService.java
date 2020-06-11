package ru.tuchanet.sftpclient.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SftpService {

	
	@Value("${ssh.host}")
	private String  host;
	@Value("${ssh.port}")
	private Integer port;
	@Value("${ssh.user}")
	private String  user;
	@Value("${ssh.pass}")
	private String  pass;
	
	
	
	@Override
	public String toString() {
		return "SftpService [host=" + host + ", port=" + port + ", user=" + user + ", pass=" + pass + "]";
	}

	
	
	
}
