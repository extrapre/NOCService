package org.egov.noc.repository.querybuilder;

import org.egov.noc.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilder {

	@Autowired
	private ApplicationProperties applicationProperties;

	public static final String SELECT_PETS_QUERY ="select ED.* from egpm_noc_application_detail ED inner join egpm_noc_application EA on ED.application_uuid=EA.application_uuid WHERE EA.application_type=? AND EA.application_status=? AND ED.is_active=TRUE";
	public static final String SELECT_APPLICATION_QUERY = "select * from egpm_noc_application_detail ED inner join egpm_noc_application EA on ED.application_uuid=EA.application_uuid WHERE  ED.application_uuid=? and ED.is_active=true and EA.application_status=?";
	
	
	//add
	public static final String INSERT_NOC_DETAILS_QUERY = "INSERT into public.egpm_noc_application_detail "
			+ "(application_detail_uuid, application_uuid, application_detail, is_active, created_by, created_time, last_modified_by, last_modified_time)"
			+ "values(?,?,to_json(?::json),?,?,?,?,?)";

	public static String getPetsQuery() {

		StringBuilder petsQuery = new StringBuilder(SELECT_PETS_QUERY);
		return petsQuery.toString();
	}

	public static String getApplicationQuery() {
		StringBuilder petsQuery = new StringBuilder(SELECT_APPLICATION_QUERY);
		return petsQuery.toString();
	}

}
