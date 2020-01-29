package org.egov.noc.web.contract;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.noc.model.NOCApplicationDetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PetsResponse
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NocResponse {

	@JsonProperty("ResposneInfo")
	private ResponseInfo resposneInfo;

	@JsonProperty("nocApplicationDetail")
	private List<NOCApplicationDetail> nocApplicationDetail = new ArrayList<>();

}