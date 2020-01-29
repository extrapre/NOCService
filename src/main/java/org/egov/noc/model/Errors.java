package org.egov.noc.model;


import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Errors {
	
	
	private ResponseInfo responseInfo;
	private ErrorResponseInfo error;
	


     
}
