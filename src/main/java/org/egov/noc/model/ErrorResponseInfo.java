package org.egov.noc.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseInfo {
	private String code;
	private String message;
	private String description;
	private String params;
}
