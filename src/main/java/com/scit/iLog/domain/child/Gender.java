package com.scit.iLog.domain.child;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum Gender {
    MAN("남"), WOMAN("여"), NONE("선택안함");

	private final String genderKr;

	public String getGenderKr()
	{
		return genderKr;
	}
}