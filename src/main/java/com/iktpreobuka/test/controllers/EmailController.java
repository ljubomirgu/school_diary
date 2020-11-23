package com.iktpreobuka.test.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.test.models.EmailObject;
import com.iktpreobuka.test.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins="http://localhost:3000")
@RestController 
@RequestMapping(path = "/") 
public class EmailController {
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private EmailService emailService;
	

	@RequestMapping(method = RequestMethod.POST, value = "/simpleEmail")
	public ResponseEntity<?> sendSimpleMessage(@RequestBody EmailObject object) {
		logger.info("EmailController - sendSimpleMessage - starts.");

			if (object == null || object.getTo() == null || object.getText() == null) {
				return null;
			}
		try {
			emailService.sendSimpleMessage(object);
			logger.info("EmailController - sendSimpleMessage - finished.");
			return new ResponseEntity<>("Your mail has been sent!", HttpStatus.OK);
		} catch (Exception e) {
			logger.info("EmailController - sendSimpleMessage - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	private static String PATH_TO_ATTACHMENT = "D://BRAINS//SpringWorkspace//school_diary//logs//spring-boot-logging.log";

	@RequestMapping(method = RequestMethod.POST, value = "/emailWithAttachment") 
	public String sendMessageWithAttachment(@RequestBody EmailObject object) throws Exception {
		if(object==null || object.getTo()==null || object.getText()==null) {
			return null; 
			} 
		emailService.sendMessageWithAttachment(object, PATH_TO_ATTACHMENT); 
		return "Your mail has been sent!"; 
	}
	
	
		
	@RequestMapping(method = RequestMethod.POST, value = "/templateEmail") 
	public String sendTemplateMessage(@RequestBody EmailObject object) throws Exception { 
		if(object==null || object.getTo()==null || object.getText()==null) {
			return null; 
			} 
		emailService.sendTemplateMessage(object);
		return "Your mail has been sent!"; 
	}
}
