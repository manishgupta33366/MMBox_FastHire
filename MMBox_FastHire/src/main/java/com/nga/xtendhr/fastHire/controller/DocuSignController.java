package com.nga.xtendhr.fastHire.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.Envelope;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.EnvelopesInformation;
import com.nga.xtendhr.fastHire.docusign.ListEnvelopes;
import com.nga.xtendhr.fastHire.docusign.SendEnvelope;
import com.nga.xtendhr.fastHire.model.SFConstants;
import com.nga.xtendhr.fastHire.service.SFConstantsService;
import com.nga.xtendhr.fastHire.utilities.ConstantManager;

@RestController
@RequestMapping(value = ConstantManager.genAPI)
public class DocuSignController {

	private static final Logger logger = LoggerFactory.getLogger(DocuSignController.class);
	private static final ApiClient apiClient = new ApiClient();
	@Autowired
	SFConstantsService sfConstantsService;
	
	@PostMapping(value = "/GenerateDocSign")
	public ResponseEntity<?> generateDocumentSigning(HttpServletRequest request, @RequestBody Map<String, String> payload) throws ApiException, IOException {
		logger.debug("GenerateDocSign base64 : " +  payload.get("base64"));
		SFConstants docuSignEmail = sfConstantsService.findById("docuSignEmail");
		logger.debug("GenerateDocSign docuSignEmail : " +  docuSignEmail);
		SFConstants docuSignName = sfConstantsService.findById("docuSignName");
		logger.debug("GenerateDocSign docuSignName : " + docuSignName);
		System.setProperty("https.protocols","TLSv1.2");
		EnvelopeSummary result = new SendEnvelope(apiClient).sendEnvelope(payload.get("base64"), docuSignEmail.getValue(), docuSignName.getValue());
		logger.debug("GenerateDocSign EnvelopeSummary Status &  : " +  result.getStatus() + " " + result.getEnvelopeId());
        EnvelopesInformation envelopesList = new ListEnvelopes(apiClient).list();
        List<Envelope> envelopes = envelopesList.getEnvelopes();       
        logger.debug("GenerateDocSign result : " +  result);
        logger.debug("GenerateDocSign envelope : " + envelopes.get(0));
		return ResponseEntity.status(HttpStatus.OK).body("Document is sent for Signing successfully.");		
	}
	
}
