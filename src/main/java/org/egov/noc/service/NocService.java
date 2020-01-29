package org.egov.noc.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.noc.config.ApplicationProperties;
import org.egov.noc.model.NOCApplicationDetail;
import org.egov.noc.model.RequestData;
import org.egov.noc.repository.NocRepository;
import org.egov.noc.web.contract.NocResponse;
import org.egov.noc.web.contract.ReponseData;
import org.egov.noc.web.contract.factory.ResponseFactory;
import org.egov.tracer.model.CustomException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NocService {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	private ApplicationProperties appProps;

	@Autowired
	private NocRepository nocRepository;

	@Autowired
	private ResponseFactory responseInfoFactory;

	@Autowired
	@Qualifier("validatorJSON")
	private JSONObject jsonObject;

	public NocResponse searchNoc(RequestData requestInfo) {
		List<NOCApplicationDetail> nocs = nocRepository.findPets(requestInfo);
		return NocResponse.builder()
				.resposneInfo(responseInfoFactory.getResponseInfo(requestInfo.getRequestInfo(), HttpStatus.OK))
				.nocApplicationDetail(nocs).build();
	}

	public NocResponse searchApplicaion(RequestData py) {
		List<NOCApplicationDetail> nocs = nocRepository.findPet(py.getDataPayload().get("applicationUuid").toString(),py.getApplicationStatus());
		return NocResponse.builder().resposneInfo(responseInfoFactory.getResponseInfo(py.getRequestInfo(), HttpStatus.OK)).nocApplicationDetail(nocs).build();
	}

	//// add
	public ReponseData sendNocCreateToTableJsonValue(RequestData requestData) {
		String responseValidate = "";
		ReponseData reponseData = null;
		try {

			responseValidate = validateJsonData(requestData);
			if (responseValidate.equals("")) {

				String applicationId = nocRepository.saveNocTableJson(requestData);
				if (applicationId!=null) {
					nocRepository.saveNOCDetails(requestData, applicationId);
					reponseData = new ReponseData();
					ResponseInfo responseInfo = new ResponseInfo();
					responseInfo.setStatus("SUCCESS");
					requestData.getDataPayload().put("applicationId", applicationId);
					reponseData.setDataPayload(requestData.getDataPayload());
					reponseData.setResponseInfo(responseInfo);
				}else
				{
					ResponseInfo responseInfo = new ResponseInfo();
					responseInfo.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
					responseInfo.setMsgId("ID Generation Failed");
					reponseData.setResponseInfo(responseInfo);
				}

			} else {

				reponseData = new ReponseData();
				ResponseInfo responseInfo = new ResponseInfo();
				responseInfo.setStatus("fail");
				responseInfo.setResMsgId(responseValidate);
				reponseData.setResponseInfo(responseInfo);
				reponseData.setApplicationType(requestData.getApplicationType());
				reponseData.setAuditDetails(requestData.getAuditDetails());
				reponseData.setDataPayload(requestData.getDataPayload());

			}

		} catch (Exception e) {
			log.debug("PetsService createAsync:" + e);
			throw new CustomException("EGBS_PETS_SAVE_ERROR", e.getMessage());

		}

		return reponseData;
	}

	/*private String validateJsonData(RequestData requestData) throws IOException, ParseException {
		StringBuilder responseText = new StringBuilder();
		try {
			FileUtils fileUtils = new FileUtils();
			String validate = fileUtils.getFileContents("noc-type-json-validator.json");
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(validate);

			JSONObject jsonValidator = (JSONObject) jsonObject.get(requestData.getApplicationType());
			JSONObject jsonRequested = requestData.getDataPayload();

			Set<String> keyValidateList = jsonValidator.keySet();
			Set<String> keyRequestedList = jsonRequested.keySet();

			if (keyValidateList.equals(keyRequestedList)) {

				for (String key : keyValidateList) {
					JSONObject actualValidate = (JSONObject) jsonValidator.get(key);
					String isMandatory = actualValidate.get("mandatory").toString();
					String isType = actualValidate.get("type").toString();
					String isRegExpression = actualValidate.get("validateRegularExp").toString();

					String dataReq = jsonRequested.get(key).toString();

					if (isMandatory.equals("true") && dataReq.equals("")) {
						responseText.append(key + " : [Mandatory field]");
						responseText.append(",");
					} else {
						Pattern validatePattern = Pattern.compile(isRegExpression);
						if (!validatePattern.matcher(dataReq).matches()) {
							responseText.append(key + ":[Invalid data]");
							responseText.append(",");
						}
					}
				}
				if (!responseText.toString().equals("")) {
					responseText = new StringBuilder(
							"Error at => " + responseText.substring(0, responseText.length() - 1));
				}
			} else {
				responseText = new StringBuilder("Supplied Invalid Colunms");
			}
		} catch (Exception e) {
			responseText.append("Unable to Process request => ");
			responseText.append("Exceptions => " + e.getMessage());
		}
		return responseText.toString();
	}*/
	
	private String validateJsonData(RequestData requestData) throws IOException, ParseException {
		StringBuilder responseText = new StringBuilder();
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonValidator1 = (JSONObject) jsonParser.parse(jsonObject.toJSONString()); 
			JSONObject jsonValidator = (JSONObject) jsonValidator1.get(requestData.getApplicationType());
			JSONObject jsonRequested = (JSONObject) jsonParser.parse(requestData.getDataPayload().toString());

			if (jsonObject == null || jsonValidator == null || jsonRequested == null) {
				return "Unable to load the JSON file or requested data.";
			}

			Set<String> keyValidateList = jsonValidator.keySet();
			Set<String> keyRequestedList = jsonRequested.keySet();

			if (keyValidateList.equals(keyRequestedList)) {

				for (String key : keyValidateList) {
					JSONObject actualValidate = (JSONObject) jsonValidator.get(key);
					String isMandatory = actualValidate.get("mandatory").toString();
					String isType = actualValidate.get("type").toString();
					String isRegExpression = actualValidate.get("validateRegularExp").toString();
					String dataReq = jsonRequested.get(key).toString();

					if (isMandatory.equals("true") && dataReq.equals("")) {
						responseText.append(key + " : [Mandatory field]");
						responseText.append(",");
					} else {
						Pattern validatePattern = Pattern.compile(isRegExpression);
						if (!validatePattern.matcher(dataReq).matches()) {
							responseText.append(key + ":[Invalid data]");
							responseText.append(",");
						}
					}
				}
				if (!responseText.toString().equals("")) {
					responseText = new StringBuilder(
							"Error at =>  " + responseText.substring(0, responseText.length() - 1));
				}
			} else {
				responseText = new StringBuilder("Supplied Invalid Colunms");
			}
		} catch (Exception e) {
			responseText.append("Unable to Process request => ");
			responseText.append("Exceptions => " + e.getMessage());
		}
		return responseText.toString();
	}
}
