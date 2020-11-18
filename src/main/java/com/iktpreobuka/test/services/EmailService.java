package com.iktpreobuka.test.services;

import com.iktpreobuka.test.models.EmailObject;


public interface EmailService {

	void sendSimpleMessage (EmailObject object);
	void sendTemplateMessage (EmailObject object) throws Exception; 
	void sendMessageWithAttachment (EmailObject object, String pathToAttachment) throws Exception;
}
