package ru.tuchanet.sftpclient.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import ru.tuchanet.sftpclient.service.SftpItem;
import ru.tuchanet.sftpclient.service.SftpService;

@Controller
public class SftpController {
	
	@Autowired
	SftpService sftpService;
	
	@GetMapping("/list")
	public String list(Model model, 
			@RequestParam(name = "folder", required = false, defaultValue = "/") String folder,
			@RequestParam(name = "sort", required = false, defaultValue = "type") String sort,
			@RequestParam(name = "dir", required = false, defaultValue = "desc") String dir) {
		
		List<SftpItem> list;
		Exception error = null;
		
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
		model.addAttribute("dir", dir);
		model.addAttribute("svc", sftpService.toString());
		
		return "sftp/list";
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
