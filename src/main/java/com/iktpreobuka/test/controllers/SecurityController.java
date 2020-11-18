package com.iktpreobuka.test.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin(origins="http://localhost:3000")
@Controller
public class SecurityController {
	
	@RequestMapping(value="/username", method = RequestMethod.GET)
	@ResponseBody
	public String loggedUsername(Principal principal) {
		return principal.getName();
	}

}
