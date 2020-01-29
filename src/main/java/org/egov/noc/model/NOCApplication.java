package org.egov.noc.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Collection of audit related fields used by most models
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NOCApplication {

	private String applicationUuid;
	private String tenantId;
	private String nocNumber;
	private String appliedBy;
	private String appliedDate;
	private String applicationType;
	private String applicationStatus;
	private Boolean isActive;
	private String createdBy;
	private String lastModifiedBy;
	private Long createdTime;
	private Long lastModifiedTime;
	private String applicantName;
	private String houseNo;
	private String sector;
}
