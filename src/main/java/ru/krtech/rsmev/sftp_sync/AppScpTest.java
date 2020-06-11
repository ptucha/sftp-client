package ru.krtech.rsmev.sftp_sync;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class AppScpTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		
		String fwUser = "p.tucha";
		String fwPass = "90CktBcgDhf";
		String fwHost = "rsmev-smev2";
		Integer fwPort = 22;
		Integer LPort = 2223;
		String RHost = "172.20.17.106";
		Integer RPort = 2222;

		Session fwSession = null;

//		String user = "pavel";
//		String password = "sony";
//		String host = "192.168.0.1";
//		Integer port = 22;

		String user = "user04";
		String password = "pass";
		String host = "127.0.0.1";
		Integer port = 2223;

		JSch jsch = new JSch();

		ChannelSftp sftp = null;
		Session session = null;

		try {

			// ssh-keyscan -t rsa HOSTNAME > known_hosts.txt
			// jsch.setKnownHosts("known_hosts.txt");

			// ssh rsmev-smev2 -L2222:172.20.17.106:2222

			// Port forwarding
			Hashtable<String, String> config = new Hashtable<>();
			config.put("StrictHostKeyChecking", "no");

			System.out.println("Создание перенаправления");
			fwSession = jsch.getSession(fwUser, fwHost, fwPort);
			fwSession.setPassword(fwPass);
			fwSession.setConfig(config);
			fwSession.connect();
			fwSession.setPortForwardingL(LPort, RHost, RPort);

			System.out.println("Подключение к SFTP серверу");
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();

			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();

			Vector<LsEntry> list = sftp.ls("./upload");

			Path destPath = Paths.get("./toRegion");
			deleteDirectory(destPath);
			Files.createDirectories(destPath);

			zip(destPath, destPath.getParent().resolve("toRegion.zip"));

			for (LsEntry entry : list) {
				if (entry.getFilename().startsWith("2020")) {
					Vector<LsEntry> list2 = sftp.ls("./upload/" + entry.getFilename() + "/toRegion/");
					for (LsEntry e : list2) {
						String f = e.getFilename();
						if (!f.equals(".") && !f.equals("..")) {
							String file = "./upload/" + entry.getFilename() + "/toRegion/" + f;
							System.out.println(file);
							sftp.get(file, "./toRegion");
						}
					}
				}
			}

			System.out.println("Архивация файлов");
			zip(destPath, destPath.getParent().resolve("toRegion.zip"));

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {

			if (session != null && session.isConnected())
				session.disconnect();

			if (fwSession != null && fwSession.isConnected())
				fwSession.disconnect();

			if (sftp != null && !sftp.isClosed())
				sftp.quit();
			
			System.out.println("Готово");
		}
	}

	public static void zip(Path src, Path zipFile) {

		try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile.toString()))) {

			if (Files.isDirectory(src)) {

			} else if (Files.isRegularFile(src)) {

			}

//			ZipEntry entry = new ZipEntry();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	public static void deleteDirectory(Path dir) throws IOException {
		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

		});
	}

}
