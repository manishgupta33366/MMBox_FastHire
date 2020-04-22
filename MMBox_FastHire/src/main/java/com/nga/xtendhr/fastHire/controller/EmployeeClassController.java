package com.nga.xtendhr.fastHire.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nga.xtendhr.fastHire.SF.DestinationClient;
import com.nga.xtendhr.fastHire.connections.HttpConnectionGET;
import com.nga.xtendhr.fastHire.utilities.CommonFunctions;
import com.nga.xtendhr.fastHire.utilities.ConstantManager;
import com.nga.xtendhr.fastHire.utilities.URLManager;

@RestController
@RequestMapping(value = ConstantManager.genAPI)
public class EmployeeClassController {

	private static final String configName = "sfconfigname";
	public static final String destinationName = "prehiremgrSFTest";
	private static final Logger logger = LoggerFactory.getLogger(EmployeeClassController.class);
	
	@GetMapping(value = ConstantManager.employeeClass, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getEmployeeClass(@RequestParam(value="status",required = true) String status){
		Map<String,String> queryParams = new HashMap<>();
		queryParams.put("status", status);
		URLManager genURL = new URLManager(queryParams,getClass().getSimpleName(), configName);
		String urlToCall = genURL.formURLToCall();
		logger.info(ConstantManager.lineSeparator + ConstantManager.urlLog + urlToCall + ConstantManager.lineSeparator);

		// Get details from server
		URI uri = CommonFunctions.convertToURI(urlToCall);
		HttpConnectionGET httpConnectionGET = new HttpConnectionGET(uri, URLManager.dConfiguration,
				EmployeeClassController.class);
		String result = httpConnectionGET.connectToServer();
		return result;
	}
	
	@GetMapping(value = ConstantManager.employeeClassPickListOption, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> getEmployeePickListOption(@RequestParam(value="id",required = true) String id) throws NamingException, ClientProtocolException, IOException, URISyntaxException{
		
		DestinationClient destClient = new DestinationClient();
		destClient.setDestName(destinationName);
		destClient.setHeaderProvider();
		destClient.setConfiguration();
		destClient.setDestConfiguration();
		destClient.setHeaders(destClient.getDestProperty("Authentication"));
		HttpResponse response = destClient.callDestinationGET("/PicklistOption", "?$format=json&$filter=picklist/picklistId eq '"
				+ "EMPLOYEECLASS" + "' and status eq '" + "ACTIVE" + "' and id eq '" + id + "'&$select=externalCode");
		String result = EntityUtils.toString(response.getEntity(), "UTF-8");
		
		JSONObject resultObject = new JSONObject(result);
		JSONArray resultObjectArray = resultObject.getJSONObject("d").getJSONArray("results");
		String externalCode = resultObjectArray.getJSONObject(0).getString("externalCode");	
				
		response = destClient.callDestinationGET("/PicklistOption","?$format=json&$filter=picklist/picklistId eq '"
				+ "EmploymentType" + "' and status eq '" + "ACTIVE" + "' and parentPicklistOption/externalCode eq '"+ externalCode +"'&$expand=picklist,picklistLabels,parentPicklistOption&$select=id,picklistLabels/label,picklistLabels/locale");		result = EntityUtils.toString(response.getEntity(), "UTF-8");	
		
		resultObject = new JSONObject(result);			
		resultObjectArray = resultObject.getJSONObject("d").getJSONArray("results");
				
		JSONArray pickListOptionArray = new JSONArray();
		for(int i=0;i<resultObjectArray.length();i++){
			JSONObject pickListOptionObject = resultObjectArray.getJSONObject(i);
			JSONObject optionObject = new JSONObject();
			optionObject.put("id", pickListOptionObject.getString("id"));
			
			JSONArray optionArray = new JSONArray();
			JSONArray resultOptionArray = pickListOptionObject.getJSONObject("picklistLabels").getJSONArray("results");
			logger.debug("resultOptionArray in getEmployeePickListOption:  " + resultOptionArray);
			for(int j=0;j<resultOptionArray.length();j++){
				JSONObject resultOptionObject = resultOptionArray.getJSONObject(j);
				JSONObject tempObject = new JSONObject();
				tempObject.put("locale", resultOptionObject.getString("locale"));
				tempObject.put("label", resultOptionObject.getString("label"));
				optionArray.put(tempObject);
			}			
			optionObject.put("picklistLabels", optionArray);
			pickListOptionArray.put(optionObject);			
		}		
		logger.debug("pickListOptionArray in getEmployeePickListOption:  " + pickListOptionArray);
		return ResponseEntity.status(HttpStatus.OK).body(pickListOptionArray.toString());
	}
	
}
