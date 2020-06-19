package ru.tuchanet.sftpclient.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

@Service
@SuppressWarnings("unchecked") // jsch is an old library
public class SftpService implements InitializingBean{

	
	@Value("${ssh.host}")
	private String  host;
	@Value("${ssh.port}")
	private Integer port;
	@Value("${ssh.user}")
	private String  user;
	@Value("${ssh.pass}")
	private String  pass;

	ChannelSftp sftp = null;
	Session session = null;
	

	@Override
	public void afterPropertiesSet() throws Exception {
		initSession();
	}
	
	private void initSession() throws JSchException {
		
		JSch jsch = new JSch();
		
		Hashtable<String, String> config = new Hashtable<>();
		config.put("StrictHostKeyChecking", "no");
		
		session = jsch.getSession(user, host, port);
		session.setPassword(pass);
		session.setConfig(config);
		sftp = null;
		
	}
	
	
	private ChannelSftp getSFTP() throws JSchException{
		
		if(! session.isConnected()) {
			try {
				session.connect();
			}catch(JSchException e) {
				
				// Jsch disconnection require to recreate session.
				// It will be "Packet corrupted" exception otherwise.
				
				initSession();
				session.connect();
				
			}
		}
		
		if(sftp == null) {
			sftp = (ChannelSftp) session.openChannel("sftp");
		}
		
		if(! sftp.isConnected()) {
			sftp.connect();
		}
		
		return sftp;
	}
	
	
	public List<SftpItem> list(String dir) throws SftpException, JSchException {
		List<SftpItem> list = new ArrayList<>();
		
		Vector<LsEntry> ls;
		try {
			ls = getSFTP().ls(dir);
		} catch (Exception e) {
			
			// Reconnect on error and retry
			initSession();
			ls = getSFTP().ls(dir);
			
		}
		
		for(LsEntry entry : ls){
			String name = entry.getFilename();
			SftpATTRS attrs = entry.getAttrs();
			if(!".".equals(name) && !"..".equals(name)) {
				list.add(new SftpItem(
					name,
					attrs.isDir(),
					attrs.getMTime(),
					attrs.getSize()
				));
			}
		}
		
		return list;
	}
	
	
	@Override
	public String toString() {
		return "SftpService [host=" + host + ", port=" + port + ", user=" + user + ", pass=" + pass + "]";
	}

	public InputStream getFile(String file) throws SftpException, JSchException {
		InputStream istream;
		
		try {
			istream = getSFTP().get(file);
		}catch (Exception e) {
			// Reconnect on error and retry
			initSession();
			istream = getSFTP().get(file);
		}
				
		return istream;
	}

	
	
	
}
