package ru.tuchanet.sftpclient.controller;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import ru.tuchanet.sftpclient.service.SftpItem;
import ru.tuchanet.sftpclient.service.SftpService;

@Controller
public class SftpController {
	
	@Autowired
	SftpService sftpService;

	@Value("${ssh.path}")
	private String  path;
	
	@RequestMapping("*")
	public String any() {
		return "redirect:/list";
	}
	
	
	@GetMapping("/list")
	public String list(Model model, 
			@RequestParam(name = "folder", required = false) String folder,
			@RequestParam(name = "sort", required = false, defaultValue = "type") String sort,
			@RequestParam(name = "dir", required = false, defaultValue = "desc") String dir) {
		
		if(folder == null) {
			folder = path;
		}
		
		List<SftpItem> list;
		Exception error = null;
		
		// Strip .. in folder name
		folder = Paths.get(folder).normalize().toString();
		
		try {
			list = sftpService.list(folder);
		} catch (SftpException | JSchException e) {
			list = new ArrayList<>();
			error = e;
			e.printStackTrace();
		}
		
		sort(list, sort, dir);
		
		model.addAttribute("error", error);
		model.addAttribute("list", list);
		model.addAttribute("folder", folder);
		model.addAttribute("sort", sort);
		model.addAttribute("dir", dir);
		model.addAttribute("svc", sftpService.toString());
		
		return "sftp/list";
	}

	
	@GetMapping("/download")
	@ResponseBody
	public ResponseEntity<InputStreamResource> download(Model model, HttpServletResponse response, 
			@RequestParam(name = "file", required = true, defaultValue = "/") String file) throws SftpException, JSchException, UnsupportedEncodingException {
		
		List<SftpItem> list = sftpService.list(file);
		
		if(list.size() > 0 && ! list.get(0).isDir()) {
			SftpItem item = list.get(0);
			String fileName = Paths.get(file).getFileName().toString();
			return ResponseEntity.ok()
					.header("Content-Type", "application/octet-stream")
					.header("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20") + "\"")
					.header("Content-Length", Long.toString(item.getSize()))
					.body(new InputStreamResource(sftpService.getFile(file)));
		}
		
		return ((BodyBuilder) ResponseEntity.notFound())
				.body(new InputStreamResource(new ByteArrayInputStream("File not found".getBytes())));
	}	
	
	
	private void sort(List<SftpItem> list, String sort, String dir) {
		Comparator<SftpItem> comparator;
		
		if("name".equals(sort)) {
			comparator = Comparator.comparing(SftpItem::getName);
		}else if("size".equals(sort)) {
			comparator = Comparator.comparingLong(SftpItem::getSize);
		}else if("mtime".equals(sort)) {
			comparator = Comparator.comparingInt(SftpItem::getMtime);
		}else {
			comparator = Comparator.comparing(SftpItem::isDir);
		}
		
		if("desc".equals(dir)) {
			comparator = comparator.reversed();
		}
		
		list.sort(comparator);
		
		
	}
	

}
