package com.nga.xtendhr.fastHire.controller;

import java.net.URI;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nga.xtendhr.fastHire.connections.HttpConnectionPOST;
import com.nga.xtendhr.fastHire.utilities.CommonFunctions;
import com.nga.xtendhr.fastHire.utilities.ConstantManager;
import com.nga.xtendhr.fastHire.utilities.URLManager;

@RestController
@RequestMapping(value = ConstantManager.genAPI)
public class NationalIdController {
	
	private static final Logger logger = LoggerFactory.getLogger(NationalIdController.class);
	private static final String configName = "sfconfigname";
	public static final String destinationName = "prehiremgrSFTest";
	
	@PostMapping(value = ConstantManager.attachment, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> uploadAttachment(@RequestBody Map<String, String> payload, HttpServletRequest requestForSession){
		
		HttpSession session = requestForSession.getSession(false);

		String userID = (String) session.getAttribute("userID");
		URLManager genURL = new URLManager(getClass().getSimpleName(), configName);
		String urlToCall = genURL.formURLToCall();
		logger.info(ConstantManager.lineSeparator + ConstantManager.urlLog + urlToCall + ConstantManager.lineSeparator);

		// Get details from server
		URI uri = CommonFunctions.convertToURI(urlToCall);
		
		HttpConnectionPOST httpConnectionPOST = new HttpConnectionPOST(uri, URLManager.dConfiguration,
				replaceKeysForAttachment(userID, payload.get("fileName"), payload.get("fileContent")), NationalIdController.class);
		
		String result = httpConnectionPOST.connectToServer();		
		logger.debug("Upload New Attachment: " + result);
		
		JSONObject resultObject = new JSONObject(result);
		JSONArray resultObjectArray = resultObject.getJSONArray("d");
		logger.debug("Upload New Attachment resultObjectArray: " + resultObjectArray);
		JSONObject attachmentJSONObj = new JSONObject();
		/* Find out the JSON Array Length */
		if(resultObjectArray.length() > 0 ){
			attachmentJSONObj.put("key", resultObjectArray.getJSONObject(0).getString("key"));
		}
		else{
			attachmentJSONObj.put("key", "");
		}		
		logger.debug("Get New Attachment : " + attachmentJSONObj);
		return ResponseEntity.status(HttpStatus.OK).body(attachmentJSONObj.toString());	
	}
	
	@PostMapping(value = ConstantManager.perNationalId, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String createNationalID(@RequestBody Map<String, String> payload, HttpServletRequest requestForSession){
		
		HttpSession session = requestForSession.getSession(false);

		String userID = (String) session.getAttribute("userID");
		URLManager genURL = new URLManager(getClass().getSimpleName(), configName);
		String urlToCall = genURL.formURLToCall();
		logger.info(ConstantManager.lineSeparator + ConstantManager.urlLog + urlToCall + ConstantManager.lineSeparator);

		// Get details from server
		URI uri = CommonFunctions.convertToURI(urlToCall);
		
		HttpConnectionPOST httpConnectionPOST = new HttpConnectionPOST(uri, URLManager.dConfiguration,
				replaceKeysForNationalID(userID, payload.get("country"), payload.get("cardType"), payload.get("nationalId"), payload.get("attachmentId")), NationalIdController.class);
		
		String result = httpConnectionPOST.connectToServer();
		logger.debug("Get NationalID response : " + result);
		return result;	
	}
	
	@SuppressWarnings("unchecked")
	private String replaceKeysForAttachment(String userID, String fileName, String fileContent) {
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject jsonObj = new org.json.simple.JSONObject();
		jsonObj.put("uri", "Attachment");
		obj.put("__metadata", jsonObj);
		obj.put("module", "HRIS");
		obj.put("ownerIdType", "USERSSYS_ID");
		obj.put("documentType", null);
		obj.put("documentEntityType", "PERSON");
		obj.put("documentCategory", "NATIONAL_ID");
		obj.put("userId", userID);
		obj.put("ownerId", userID);
		obj.put("fileName", fileName);
		obj.put("fileContent", fileContent);
		logger.error("Attachment POST in NationIdController:" + obj.toJSONString());
		return obj.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	private String replaceKeysForNationalID(String userID, String country, String cardType, String nationalId, String attachmentId) {
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject jsonObj = new org.json.simple.JSONObject();
		jsonObj.put("uri", "PerNationalId");
		obj.put("__metadata", jsonObj);
		obj.put("isPrimary", true);
		obj.put("country", country);
		obj.put("cardType", cardType);
		obj.put("nationalId", nationalId);
		obj.put("attachmentId", attachmentId);
		obj.put("personIdExternal", userID);
		logger.error("NationalID POST in NationIdController:" + obj.toJSONString());
		return obj.toJSONString();
	}
	
}
