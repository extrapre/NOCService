package org.egov.noc.web.contract;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.noc.model.AuditDetail;
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
public class ReponseData {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("applicationType")
	private String applicationType;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("dataPayload")
	private JSONObject dataPayload;

	@JsonProperty("auditDetails")
	private AuditDetail auditDetails;

}
