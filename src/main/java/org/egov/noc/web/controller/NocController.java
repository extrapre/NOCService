package org.egov.noc.web.controller;

import org.egov.noc.model.Errors;
import org.egov.noc.model.RequestData;
import org.egov.noc.service.NocService;
import org.egov.noc.util.UserUtil;
import org.egov.noc.web.contract.ReponseData;
import org.egov.noc.web.contract.factory.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("noc")
public class NocController {

	@Autowired
	private NocService nocService;
	
	@Autowired
	private UserUtil userUtil;

	@Autowired
	private ResponseFactory responseFactory;

	@PostMapping("_get")
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> get(@RequestBody RequestData request, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, request.getRequestInfo()),
					HttpStatus.BAD_REQUEST);
		}
		if (!request.getDataPayload().isEmpty() && request.getDataPayload() != null
				&& request.getDataPayload().get("applicationUuid").toString() != null
				&& !request.getDataPayload().get("applicationUuid").toString().isEmpty()) {
			return new ResponseEntity<>(nocService.searchApplicaion(request), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(nocService.searchNoc(request), HttpStatus.OK);
		}

	}

	/////// add
	@PostMapping("_createJson")
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> createNoc(@RequestBody RequestData requestData, BindingResult bindingResult) {

		// validate user
		Errors res = null;
		Errors response = userUtil.validateUser(requestData);

		if (response.getError().getMessage().equals("success")) {
			log.debug("create petsRequest:" + requestData.getDataPayload());

			if (bindingResult.hasErrors()) {
				return new ResponseEntity<>(
						responseFactory.getErrorResponse(bindingResult, requestData.getRequestInfo()),
						HttpStatus.BAD_REQUEST);
			}

			ReponseData responseDataResponse = nocService.sendNocCreateToTableJsonValue(requestData);

			return new ResponseEntity<>(responseDataResponse, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}
}
