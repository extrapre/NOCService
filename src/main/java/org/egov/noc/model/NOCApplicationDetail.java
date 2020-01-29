package org.egov.noc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NOCApplicationDetail {
	private String applicationDetailUuid;
	private String applicationUuid;
	private String applicationDetail;
	private String createdBy;
	private Long createdTime;
	private String lastModifiedBy;
	private Long lastModifiedTime;
	private Boolean isActive;
}
