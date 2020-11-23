package com.iktpreobuka.test.controllers.util;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;

public class RESTError {
	
	@JsonView(Views.Admin.class)
	private Integer code;
	
	@JsonView(Views.Admin.class)
	private String message;

	public RESTError() {
		super();
	}

	public RESTError(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	

	public RESTError(String message) {
		super();
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
