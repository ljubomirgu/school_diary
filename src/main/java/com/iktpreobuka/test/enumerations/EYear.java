package com.iktpreobuka.test.enumerations;

public enum EYear {
 //FIFTH, SIXTH, SEVENTH, EIGHTH
	V(5), VI(6), VII(7), VIII(8);
	
	private final Integer value;
	
	EYear(Integer value)
	{
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}
	
}
