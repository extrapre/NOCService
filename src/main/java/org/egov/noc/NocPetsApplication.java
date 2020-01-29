package org.egov.noc;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.egov.tracer.model.CustomException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class NocPetsApplication {

	@Value("${egov.validation.json.path}")
	private String configValidationPaths;

	@Autowired
	public static ResourceLoader resourceLoader;

	public NocPetsApplication(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(NocPetsApplication.class, args);
	}

	@PostConstruct
	@Bean(name="validatorJSON")
	public JSONObject loadValidationSourceConfigs() {
		Map<String, String> errorMap = new HashMap<>();
		JSONObject jsonObject = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		log.info("====================== EGOV NOC SERVICE ======================");
		log.info("LOADING CONFIGS VALIDATION : " + configValidationPaths);
		try {
			log.info("Attempting to load config: " + configValidationPaths);

			if (configValidationPaths.startsWith("https://") || configValidationPaths.startsWith("http://")) {
				log.info("Reading....: " + configValidationPaths);

				URL jsonFile = new URL(configValidationPaths);
				jsonObject = mapper.readValue(new InputStreamReader(jsonFile.openStream()), JSONObject.class);

				log.info("Parsed: " + configValidationPaths);

			} else if (configValidationPaths.startsWith("file://") || configValidationPaths.startsWith("classpath:")) {
				log.info("Reading....: " + configValidationPaths);

				Resource resource = resourceLoader.getResource(configValidationPaths);
				File file = resource.getFile();
				jsonObject = mapper.readValue(file, JSONObject.class);

				log.info("Parsed to object: " + configValidationPaths);
			}
		} catch (Exception e) {
			log.error("Exception while fetching service map for: " + configValidationPaths, e);
			errorMap.put("FAILED_TO_FETCH_FILE", configValidationPaths);
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
		else
			log.info("====================== VALIDATION CONFIGS LOADED SUCCESSFULLY! ====================== ");

		return jsonObject;
	}
}
