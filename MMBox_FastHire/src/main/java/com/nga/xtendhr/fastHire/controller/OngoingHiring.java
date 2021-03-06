package com.nga.xtendhr.fastHire.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nga.xtendhr.fastHire.connections.HttpConnectionGET;
import com.nga.xtendhr.fastHire.utilities.CommonFunctions;
import com.nga.xtendhr.fastHire.utilities.ConstantManager;
import com.nga.xtendhr.fastHire.utilities.URLManager;

@RestController
@RequestMapping(value = ConstantManager.genAPI)
public class OngoingHiring {

	private static final String configName = "sfconfigname";
	private static final Logger logger = LoggerFactory.getLogger(OngoingHiring.class);
	
	@GetMapping(value = ConstantManager.ongoingHiring, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getHiringDetails(@RequestParam(value="employeeClass",required = true) String empclass,
			@RequestParam(value="company",required=true) String company, @RequestParam(value="department",required=true) String department,
			@RequestParam(value="emplStatusNav/id",required=true) String statusId){
		
		Map<String,String> queryParams = new HashMap<>();
		queryParams.put("company", company);
		queryParams.put("employeeClass", empclass);
		String dep =  department.replace(" ", "%20");
		queryParams.put("department", dep.replace(",", "%2C"));
		queryParams.put("emplStatusNav/id", statusId);
		URLManager genURL = new URLManager(queryParams, getClass().getSimpleName(), configName);
		String urlToCall = genURL.formURLToCall();
		logger.info(ConstantManager.lineSeparator + ConstantManager.urlLog + urlToCall + ConstantManager.lineSeparator);

		// Get details from server
		URI uri = CommonFunctions.convertToURI(urlToCall);
		HttpConnectionGET httpConnectionGET = new HttpConnectionGET(uri, URLManager.dConfiguration,
				OngoingHiring.class);
		String result = httpConnectionGET.connectToServer();
		return result;
	}

}
