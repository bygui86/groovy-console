package com.rabbit.samples.groovyconsole

import groovy.json.JsonSlurper
import org.apache.groovy.json.internal.LazyMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class GroovyConsoleApplication implements CommandLineRunner {

	private static String ROOT_URL = "http://localhost:9000"

	private static Logger LOG = LoggerFactory.getLogger(GroovyConsoleApplication.class)

	static void main(String[] args) {
		SpringApplication.run(GroovyConsoleApplication, args)
	}

	@Override
	void run(String... args) {

		def jsonSlurper = new JsonSlurper()

		LazyMap statusJson = getResponse jsonSlurper, '/api/system/status', false
		LOG.debug "$statusJson"
		LOG.info "Status: ${statusJson.status}"

		LazyMap healthJson = getResponse jsonSlurper, '/api/system/health', true
		LOG.debug "$healthJson"
		LOG.info "Health: ${healthJson.health}"

		LazyMap qualityGateJson = getResponse jsonSlurper, '/api/qualitygates/project_status?projectKey=ch.interdiscount.microservices:sap-adapter-service', true
		LOG.debug "$qualityGateJson"
		LOG.info "Quality gate: ${qualityGateJson.projectStatus.status}"
	}

	private LazyMap getResponse(final JsonSlurper jsonSlurper, final String url, final boolean auth) {

		URL apiUrl = (ROOT_URL + url).toURL()
		if (auth) {
			jsonSlurper.parse(
					apiUrl,
					buildConnectionParamatersMap(
//							getUserPwAuthString()
							getTokenAuthString()
					)
			) as LazyMap

		} else{
			jsonSlurper.parse(apiUrl) as LazyMap
		}
	}

	private String getUserPwAuthString() {
		'admin:secret'.getBytes().encodeBase64().toString()
	}

	private String getTokenAuthString() {
		'01913c925c1d366febd66701b7cd553e12972d94:'.getBytes().encodeBase64().toString()
	}

	private LinkedHashMap<String, LinkedHashMap<String, GString>> buildConnectionParamatersMap(String authString) {
		def propParams = ['Authorization': "Basic $authString"]
		['requestProperties': propParams]
	}

}
