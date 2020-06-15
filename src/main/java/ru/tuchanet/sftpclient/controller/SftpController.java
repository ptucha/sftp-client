package ru.tuchanet.sftpclient.controller;

import java.util.ArrayList;
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
	public String list(Model model, @RequestParam(name = "dir", required = false, defaultValue = "/") String dir) {
		
		List<SftpItem> list;
		Exception error = null;
		
		try {
			list = sftpService.list(dir);
		} catch (SftpException | JSchException e) {
			list = new ArrayList<>();
			error = e;
			e.printStackTrace();
		}
		
		model.addAttribute("error", error);
		model.addAttribute("list", list);
		model.addAttribute("dir", dir);
		model.addAttribute("svc", sftpService.toString());
		
		return "sftp/list";
	}
	

}
