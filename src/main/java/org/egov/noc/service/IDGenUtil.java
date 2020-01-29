package org.egov.noc.service;

import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.egov.noc.model.IdGenModel;
import org.egov.noc.model.IdGenRequestModel;
import org.egov.noc.wf.model.ProcessInstanceRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IDGenUtil {

	@Autowired
	RestTemplate restTemplate;

	@Value("${egov.idgen.hostname}")
	private String host;

	@Value("${egov.idgen.uri}")
	private String path;

	@Value("${egov.wf.hostname}")
	private String workflowHost;

	@Value("${egov.wf.uri}")
	private String workflowPath;

	public String generateApplicationId(String tenantId) {

		String url = host + path;
		ObjectMapper objectMapper = new ObjectMapper();
		RequestInfo requestInfo = new RequestInfo();
		IdGenModel generatedValue = null;
		String applicationId = null;
		List<IdGenModel> idList = Arrays
				.asList(IdGenModel.builder().count(1).idName("ch.pms").tenantId(tenantId).build());
		IdGenRequestModel mcq = new IdGenRequestModel();
		mcq.setRequestInfo(requestInfo);
		mcq.setIdRequests(idList);

		JsonNode response = restTemplate.postForObject(url, mcq, JsonNode.class).findValue("idResponses");

		if (!isNull(response) && response.isArray()) {

			for (JsonNode objNode : response) {
				try {
					generatedValue = objectMapper.treeToValue(objNode, IdGenModel.class);
					applicationId = generatedValue.getId();
				} catch (JsonProcessingException e) {
					log.error("Failed to fetch roles from MDMS", e);
					throw new CustomException("MDMS_ROLE_FETCH_FAILED", "Unable to fetch roles from MDMS");
				}
			}
		}

		return applicationId;
	}

	public void createWorkflowRequest(ProcessInstanceRequest workflowRequest) {

		String url = workflowHost + workflowPath;

		JsonNode response = restTemplate.postForObject(url, workflowRequest, JsonNode.class)
				.findValue("ProcessInstances");

		if (!isNull(response) && response.isArray()) {
			log.info("Workflow Created Success");
		} else {
			log.info("Workflow Creation Failed : Reason " + response);
		}

	}
}
