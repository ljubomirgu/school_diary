package com.iktpreobuka.test.controllers;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iktpreobuka.test.services.FileHandler;

@CrossOrigin(origins="http://localhost:3000")
@Controller
@RequestMapping(path = "/")
public class UploadController {

	@Autowired
	private FileHandler fileHandler;
	

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "download";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/downloadStatus")
	public String uploadStatus() {
		return "downloadStatus";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/download")
	public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		
		String result = null;
		try {
			result = fileHandler.singleFileUpload(file, redirectAttributes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
		
	
}
