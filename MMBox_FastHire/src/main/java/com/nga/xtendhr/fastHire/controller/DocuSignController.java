package com.nga.xtendhr.fastHire.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.Envelope;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.EnvelopesInformation;
import com.nga.xtendhr.fastHire.SF.DestinationClient;
import com.nga.xtendhr.fastHire.docusign.ListEnvelopes;
import com.nga.xtendhr.fastHire.docusign.SendEnvelope;
import com.nga.xtendhr.fastHire.service.SFConstantsService;
import com.nga.xtendhr.fastHire.utilities.ConstantManager;

@RestController
@RequestMapping(value = ConstantManager.genAPI)
public class DocuSignController {

	private static final Logger logger = LoggerFactory.getLogger(DocuSignController.class);
	private static final ApiClient apiClient = new ApiClient();
	public static final String destinationName = "prehiremgrSFTest";
	private String loggedInUser = null;

	@Autowired
	SFConstantsService sfConstantsService;

	@PostMapping(value = "/GenerateDocSign")
	public ResponseEntity<?> generateDocumentSigning(HttpServletRequest httpRequest,
			@RequestBody Map<String, String> payload, @RequestParam(value = "userId", required = true) String userId)
			throws ApiException, IOException, NamingException, URISyntaxException {
		loggedInUser = httpRequest.getUserPrincipal().getName();
		logger.debug("GenerateDocSign base64 : " + payload.get("base64"));

		/*
		 * SFConstants docuSignEmail = sfConstantsService.findById("docuSignEmail");
		 * logger.debug("GenerateDocSign docuSignEmail : " + docuSignEmail); SFConstants
		 * docuSignName = sfConstantsService.findById("docuSignName");
		 * logger.debug("GenerateDocSign docuSignName : " + docuSignName);
		 */

		DestinationClient destClient = new DestinationClient();
		destClient.setDestName(destinationName);
		destClient.setHeaderProvider();
		destClient.setConfiguration();
		destClient.setDestConfiguration();
		destClient.setHeaders(destClient.getDestProperty("Authentication"));

		/* Get the User Details of the logged In User */
		HttpResponse userResponse = destClient.callDestinationGET("/User",
				"?$format=json&$filter=userId eq '" + userId + "'&$select=defaultFullName,email");
		String userResponseJsonString = EntityUtils.toString(userResponse.getEntity(), "UTF-8");
		JSONObject userResponseObject = (JSONObject) JSONValue.parse(userResponseJsonString);
		userResponseObject = (JSONObject) userResponseObject.get("d");
		JSONArray userJSONArray = (JSONArray) userResponseObject.get("results");
		logger.debug("GenerateDocSign userJSONArray : " + userJSONArray);
		JSONObject userObject = (JSONObject) userJSONArray.get(0);
		String userEmail = userObject.get("email").toString();
		String userFullName = userObject.get("defaultFullName").toString();
		logger.debug("GenerateDocSign userObject : " + userObject);

		System.setProperty("https.protocols", "TLSv1.2");
		EnvelopeSummary result = new SendEnvelope(apiClient).sendEnvelope(payload.get("base64"), userEmail,
				userFullName);
		logger.debug(
				"GenerateDocSign EnvelopeSummary Status &  : " + result.getStatus() + " " + result.getEnvelopeId());
		EnvelopesInformation envelopesList = new ListEnvelopes(apiClient).list();
		List<Envelope> envelopes = envelopesList.getEnvelopes();
		logger.debug("GenerateDocSign result : " + result);
		logger.debug("GenerateDocSign envelope : " + envelopes.get(0));
		return ResponseEntity.status(HttpStatus.OK).body("Document is sent for Signing successfully.");
	}

}
