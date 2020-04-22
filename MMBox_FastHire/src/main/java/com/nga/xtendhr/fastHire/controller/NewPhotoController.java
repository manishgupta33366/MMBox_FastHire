package com.nga.xtendhr.fastHire.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nga.xtendhr.fastHire.SF.DestinationClient;
import com.nga.xtendhr.fastHire.connections.HttpConnectionPOST;
import com.nga.xtendhr.fastHire.utilities.CommonFunctions;
import com.nga.xtendhr.fastHire.utilities.ConstantManager;
import com.nga.xtendhr.fastHire.utilities.URLManager;

@RestController
@RequestMapping(value = ConstantManager.genAPI)
public class NewPhotoController {
	
	private static final Logger logger = LoggerFactory.getLogger(NewPhotoController.class);
	private static final String configName = "sfconfigname";
	public static final String destinationName = "prehiremgrSFTest";
	
	@PostMapping(value = ConstantManager.newPhoto, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String uploadNewPhoto(@RequestBody Map<String, String> payload, HttpServletRequest requestForSession){
		
		HttpSession session = requestForSession.getSession(false);

		String userID = (String) session.getAttribute("userID");
		URLManager genURL = new URLManager(getClass().getSimpleName(), configName);
		String urlToCall = genURL.formURLToCall();
		logger.info(ConstantManager.lineSeparator + ConstantManager.urlLog + urlToCall + ConstantManager.lineSeparator);

		// Get details from server
		URI uri = CommonFunctions.convertToURI(urlToCall);
		
		HttpConnectionPOST httpConnectionPOST = new HttpConnectionPOST(uri, URLManager.dConfiguration,
				replaceKeys(userID, payload.get("base64")), NewPhotoController.class);

		String result = httpConnectionPOST.connectToServer();
		logger.debug("Upload New Photo: " + result);
		return result;
	}
	
	@GetMapping(value = ConstantManager.newPhoto, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> getNewPhoto(@RequestParam(value="userId",required = true) String userId, HttpServletRequest requestForSession) throws NamingException, ClientProtocolException, IOException, URISyntaxException{
		
		HttpSession session = requestForSession.getSession(false);
		DestinationClient destClient = new DestinationClient();
		destClient.setDestName(destinationName);
		destClient.setHeaderProvider();
		destClient.setConfiguration();
		destClient.setDestConfiguration();
		destClient.setHeaders(destClient.getDestProperty("Authentication"));
		HttpResponse response = destClient.callDestinationGET("/Photo","?$format=json&$filter=userId eq '"
				+ userId + "' and photoType eq " + "7" + "&$select=photo");
		String result = EntityUtils.toString(response.getEntity(), "UTF-8");
		
		JSONObject resultObject = new JSONObject(result);
		JSONArray resultObjectArray = resultObject.getJSONObject("d").getJSONArray("results");
		
		JSONObject photoJSONObj = new JSONObject();
		/* Find out the JSON Array Length, photo is optional field */
		if(resultObjectArray.length() > 0 ){
			photoJSONObj.put("photo", resultObjectArray.getJSONObject(0).getString("photo"));
		}
		else{
			photoJSONObj.put("photo", "");
		}		
		logger.debug("Get New Photo: " + photoJSONObj);
		return ResponseEntity.status(HttpStatus.OK).body(photoJSONObj.toString());
	}
	
	@SuppressWarnings("unchecked")
	private String replaceKeys(String userID, String photo) {
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject jsonObj = new org.json.simple.JSONObject();
		jsonObj.put("uri", "Photo");
		jsonObj.put("type", "SFOData.Photo");		
		obj.put("__metadata", jsonObj);
		obj.put("photoType", "1");
		obj.put("userId", userID);
		obj.put("photoName", null);
		obj.put("photo", photo);		
		logger.error("NewPhotoPost:" + obj.toJSONString());
		return obj.toJSONString();
	}

}
