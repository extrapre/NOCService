package org.egov.noc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdGenModel {
	private Integer count;
	private String idName;
	private String tenantId;
	private String id;
}

