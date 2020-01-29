package org.egov.noc.util;

import java.util.Arrays;

import org.egov.noc.model.ErrorResponseInfo;
import org.egov.noc.model.Errors;
import org.egov.noc.model.RequestData;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserUtil {

	@Autowired
	RestTemplate restTemplate;

	@Value("${egov.user.hostname}")
	private String host;

	@Value("${egov.user.uri}")
	private String path;
	

	public Errors validateUser(RequestData requestData) {
		String url = host + path;
	
		Errors er=new Errors();
		ErrorResponseInfo res=new ErrorResponseInfo();
		JSONObject userSearchRequest=new JSONObject();
		userSearchRequest.put("uuid", Arrays.asList(requestData.getRequestInfo().getUserInfo().getUuid()));
		try {
			JsonNode response = restTemplate.postForObject(url, userSearchRequest, JsonNode.class);
		
		ObjectMapper objectMapper = new ObjectMapper();
		if (response.get("user").size()>0) {
			res.setMessage("success");
			
		}
		else {
			res.setCode("Invalid User");
			res.setMessage("No user found for the uuids: ["+requestData.getRequestInfo().getUserInfo().getUuid()+"]");
		}
		}
		catch(Exception e)
		{
			res.setMessage("Error While Connecing user sevice");
		}
		er.setError(res);
		return er;
		
	}


}
