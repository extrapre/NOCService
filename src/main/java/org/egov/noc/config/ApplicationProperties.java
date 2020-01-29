package org.egov.noc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import lombok.Getter;
import lombok.ToString;

@Configuration
@Getter
@PropertySource(value = { "classpath:application.properties" })
@ToString
public class ApplicationProperties {

	@Autowired
	private Environment environment;

	

}