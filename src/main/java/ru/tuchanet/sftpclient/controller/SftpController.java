package ru.tuchanet.sftpclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.tuchanet.sftpclient.service.SftpService;

@Controller
public class SftpController {
	
	@Autowired
	SftpService sftpService;
	
	@GetMapping("/list")
	public String list(Model model, @RequestParam(name = "dir", required = false, defaultValue = "") String dir) {
		
		model.addAttribute("svc", sftpService.toString());
		model.addAttribute("dir", dir);
		
		return "sftp/list";
	}
	

}
