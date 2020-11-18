package com.iktpreobuka.test.entities.dto;

import javax.validation.constraints.NotNull;

import com.iktpreobuka.test.enumerations.EUserRole;

public class RoleDto {

	@NotNull(message = "User role must be provided.")
	private EUserRole userRole;
}
