package org.egov.noc.repository;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.model.NOCApplication;
import org.egov.noc.model.NOCApplicationDetail;
import org.egov.noc.model.NOCDetailsRequestData;
import org.egov.noc.model.NOCRequestData;
import org.egov.noc.model.RequestData;
import org.egov.noc.producer.Producer;
import org.egov.noc.repository.querybuilder.QueryBuilder;
import org.egov.noc.repository.rowmapper.NocRowMapper;
import org.egov.noc.service.IDGenUtil;
import org.egov.noc.wf.model.ProcessInstance;
import org.egov.noc.wf.model.ProcessInstanceRequest;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class NocRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NocRowMapper nocRowMapper;
	
	@Autowired
	private IDGenUtil idgen;

	@Autowired
	private Producer producer;

	@Value("${persister.save.transition.noc.topic}")
	private String saveNOCTopic;
	
	@Value("${persister.save.transition.noc.details.topic}")
	private String saveNOCDetailsTopic;
	
	public List<NOCApplicationDetail> findPets(RequestData requestInfo) {

		List<Object> preparedStatementValues = new ArrayList<>();
		String queryStr =QueryBuilder.getPetsQuery();
		if(requestInfo.getApplicationType().isEmpty())
		{	
			String qallstring=queryStr.replace("WHERE EA.application_type=?","");
			log.debug("query:::" + qallstring + "  preparedStatementValues::" + preparedStatementValues);

		return jdbcTemplate.query(qallstring, new Object[]{requestInfo.getApplicationStatus()},nocRowMapper);
		}else {
			return jdbcTemplate.query(queryStr,new Object[]{requestInfo.getApplicationType(),requestInfo.getApplicationStatus()}, nocRowMapper);
		}
	}

	public List<NOCApplicationDetail> findPet(String applicationuuid,String status) {

		List<Object> preparedStatementValues = new ArrayList<>();
		String queryStr =QueryBuilder.getApplicationQuery();
		log.debug("query:::" + queryStr + "  preparedStatementValues::" + preparedStatementValues);
 
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("application_uuid", applicationuuid);
		
	    return jdbcTemplate.query(queryStr, new Object[]{applicationuuid,status},nocRowMapper );
	
	}		
//add
	//@Transactional
	public String saveNocTableJson(RequestData requestData) {

		RequestInfo requestInfo = requestData.getRequestInfo();


		String NOCID = idgen.generateApplicationId(requestData.getTenantId());
		String applicationId = null;
		
		if (NOCID!=null) {
			JSONObject dataPayLoad = requestData.getDataPayload();
			Long time = System.currentTimeMillis();
			applicationId = UUID.randomUUID().toString();
			NOCApplication app = new NOCApplication();
			app.setApplicationUuid(applicationId);
			app.setTenantId(requestData.getTenantId());
			app.setNocNumber(NOCID);
			app.setApplicantName(dataPayLoad.get("applicantName").toString());
			app.setHouseNo(dataPayLoad.get("houseNo").toString());
			app.setSector(dataPayLoad.get("sector").toString());
			app.setAppliedDate(new Date().toLocaleString());
			app.setApplicationType(requestData.getApplicationType());
			app.setApplicationStatus("APPLIED");
			app.setIsActive(true);
			app.setCreatedBy(requestInfo.getUserInfo().getUuid());
			app.setCreatedTime(time);
			app.setLastModifiedBy(requestInfo.getUserInfo().getUuid());
			app.setLastModifiedTime(time);
			List<NOCApplication> applist = Arrays.asList(app);
			NOCRequestData data = new NOCRequestData();
			data.setRequestInfo(requestInfo);
			data.setNocApplication(applist);
			producer.push(saveNOCTopic, data);
			//workflowIntegration(NOCID, requestData);
			return applicationId;
		}
		else
			return null;
	}
	

	private void workflowIntegration(String applicationId, RequestData requestData) {
		
		ProcessInstanceRequest workflowRequest=new ProcessInstanceRequest();
		workflowRequest.setRequestInfo(requestData.getRequestInfo());
		ProcessInstance processInstances=new ProcessInstance();
		processInstances.setTenantId(requestData.getTenantId());
		processInstances.setAction("INITIATE");
		processInstances.setBusinessId(applicationId);
		processInstances.setModuleName(requestData.getApplicationType());
		processInstances.setBusinessService(requestData.getApplicationType());
		List<ProcessInstance> processList=Arrays.asList(processInstances);
		workflowRequest.setProcessInstances(processList);
		idgen.createWorkflowRequest(workflowRequest);
	}

	public void saveNOCDetails(RequestData requestData, String applicationId) {

		RequestInfo requestInfo = requestData.getRequestInfo();
		System.out.println("savePet requestInfo:" + applicationId);
		log.debug("savePet requestInfo:" + applicationId);
		log.debug("savePet requestData : " + requestData.getDataPayload());
		Long time = System.currentTimeMillis();
		String applicationDetailsId = UUID.randomUUID().toString();
		
		NOCApplicationDetail nocappdetails=new NOCApplicationDetail();
		nocappdetails.setApplicationDetailUuid(applicationDetailsId);
		nocappdetails.setApplicationUuid(applicationId);
		nocappdetails.setApplicationDetail(requestData.getDataPayload().toJSONString());
		nocappdetails.setIsActive(true);
		nocappdetails.setCreatedBy(requestInfo.getUserInfo().getUuid());
		nocappdetails.setCreatedTime(time);
		nocappdetails.setLastModifiedBy(requestInfo.getUserInfo().getUuid());
		nocappdetails.setLastModifiedTime(time);
		List<NOCApplicationDetail> applist = Arrays.asList(nocappdetails);
		NOCDetailsRequestData data = new NOCDetailsRequestData();
		data.setRequestInfo(requestInfo);
		data.setNocApplicationDetails(applist);

		producer.push(saveNOCDetailsTopic, data);
		
		
		
		
		
/*		
		jdbcTemplate.batchUpdate(QueryBuilder.INSERT_NOC_DETAILS_QUERY, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {

				ps.setString(1, applicationDetailsId);
				ps.setString(2, applicationId);
				ps.setString(3, requestData.getDataPayload().toJSONString());
				ps.setBoolean(4, true);
				ps.setString(5, requestInfo.getUserInfo().getUuid());
				ps.setLong(6, time);
				ps.setString(7, requestInfo.getUserInfo().getUuid());
				ps.setLong(8, time);
			}

			@Override
			public int getBatchSize() {
				return 1;
			}
		});*/
	}

}
