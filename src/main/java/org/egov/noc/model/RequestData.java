package org.egov.noc.model;

import org.egov.common.contract.request.RequestInfo;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestData {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@JsonProperty("applicationType")
	private String applicationType;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("applicationStatus")
	private String applicationStatus;
	
	@JsonProperty("dataPayload")
	private JSONObject dataPayload;

	@JsonProperty("auditDetails")
	private AuditDetail auditDetails;
	
	

}
