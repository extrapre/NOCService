package org.egov.noc.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.noc.model.NOCApplicationDetail;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NocRowMapper implements ResultSetExtractor<List<NOCApplicationDetail>> {

	@Override
	public List<NOCApplicationDetail> extractData(ResultSet rs) throws SQLException {

		Map<String, NOCApplicationDetail> NOCApplicationDetailMap = new LinkedHashMap<>();
		while (rs.next()) {

			String petsId = rs.getString("application_uuid");
			NOCApplicationDetail NOCApplicationDetail = NOCApplicationDetailMap.get(petsId);

			if (NOCApplicationDetail == null) {

				NOCApplicationDetail = NOCApplicationDetail.builder().build();
				NOCApplicationDetail.setApplicationDetail(rs.getString("application_detail"));
				NOCApplicationDetail.setApplicationDetailUuid(rs.getString("application_detail_uuid"));
				NOCApplicationDetail.setApplicationUuid(rs.getString("application_uuid"));
				NOCApplicationDetail.setCreatedBy(rs.getString("created_by"));
				NOCApplicationDetail.setIsActive(rs.getBoolean("is_active"));
				NOCApplicationDetail.setLastModifiedBy(rs.getString("last_modified_by"));
				NOCApplicationDetail.setLastModifiedTime(rs.getLong("last_modified_time"));
				NOCApplicationDetailMap.put(NOCApplicationDetail.getApplicationDetailUuid().toString(), NOCApplicationDetail);
			}
		}
		log.debug("converting map to list object ::: " + NOCApplicationDetailMap.values());
		return new ArrayList<>(NOCApplicationDetailMap.values());
	}

}
