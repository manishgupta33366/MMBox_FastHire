package com.nga.xtendhr.fastHire.docusign;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;
import com.sun.jersey.core.util.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendEnvelope extends ExampleBase {

//    private static final String DOC_2_DOCX = "World_Wide_Corp_Battle_Plan_Trafalgar.docx";
    private static final String DOC_3_PDF = "World_Wide_Corp_lorem.pdf";
    private static final String ENVELOPE_1_DOCUMENT_1 = "<!DOCTYPE html>" +
            "<html>" +
            "    <head>" +
            "      <meta charset=\"UTF-8\">" +
            "    </head>" +
            "    <body style=\"font-family:sans-serif;margin-left:2em;\">" +
            "    <h1 style=\"font-family: 'Trebuchet MS', Helvetica, sans-serif;" +
            "         color: darkblue;margin-bottom: 0;\">World Wide Corp</h1>" +
            "    <h2 style=\"font-family: 'Trebuchet MS', Helvetica, sans-serif;" +
            "         margin-top: 0px;margin-bottom: 3.5em;font-size: 1em;" +
            "         color: darkblue;\">Order Processing Division</h2>" +
            "  <h4>Ordered by " + DSConfig.SIGNER_NAME1 + "</h4>" +
            "    <p style=\"margin-top:0em; margin-bottom:0em;\">Email: " + DSConfig.SIGNER_EMAIL1 + "</p>" +
            "    <p style=\"margin-top:0em; margin-bottom:0em;\">Copy to: " + DSConfig.CC_NAME + ", " + DSConfig.SIGNER_EMAIL1 + "</p>" +
            "    <p style=\"margin-top:3em;\">" +
            "  Candy bonbon pastry jujubes lollipop wafer biscuit biscuit. Topping brownie sesame snaps" +
            " sweet roll pie. Croissant danish biscuit soufflé caramels jujubes jelly. Dragée danish caramels lemon" +
            " drops dragée. Gummi bears cupcake biscuit tiramisu sugar plum pastry." +
            " Dragée gummies applicake pudding liquorice. Donut jujubes oat cake jelly-o. Dessert bear claw chocolate" +
            " cake gummies lollipop sugar plum ice cream gummies cheesecake." +
            "    </p>" +
            "    <!-- Note the anchor tag for the signature field is in white. -->" +
            "    <h3 style=\"margin-top:3em;\">Agreed: <span style=\"color:white;\">**signature_1**/</span></h3>" +
            "    </body>" +
            "</html>";

    public SendEnvelope(ApiClient apiClient) throws IOException {
        super(apiClient);
    }

    /**
     * method show the usage of
     * @return
     * @throws ApiException
     * @throws IOException
     */
    public EnvelopeSummary sendEnvelope(String base64, String signer_Email1, String signer_Name1) throws ApiException, IOException {

        this.checkToken();

        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document sent from Fast Hire Application");

        Document doc1 = new Document();
        doc1.setDocumentBase64(base64);
        doc1.setName("Contract");
        doc1.setFileExtension("pdf");
        doc1.setDocumentId("1");

//        Document doc2 = new Document();
//        doc2.setDocumentBase64(new String(Base64.encode(DSHelper.readContent(DOC_2_DOCX))));
//        doc2.setName("Battle Plan");
//        doc2.setFileExtension("docx");
//        doc2.setDocumentId("2");

        // The order in the docs array determines the order in the envelope
        envelopeDefinition.setDocuments(Arrays.asList(doc1));  // doc2, doc3
        // create a signer recipient to sign the document, identified by name and email
        // We're setting the parameters via the object creation
        Signer signer1 = new Signer();
        signer1.setEmail(signer_Email1);
        signer1.setName(signer_Name1);
        signer1.setRecipientId("1");
        signer1.setRoutingOrder("1");
                
        /*Signer signer2 = new Signer();
        signer2.setEmail(DSConfig.SIGNER_EMAIL2);
        signer2.setName(DSConfig.SIGNER_NAME2);
        signer2.setRecipientId("2");
        signer2.setRoutingOrder("1");*/
        // routingOrder (lower means earlier) determines the order of deliveries
        // to the recipients. Parallel routing order is supported by using the
        // same integer as the order for two or more recipients.

        // create a cc recipient to receive a copy of the documents, identified by name and email
        // We're setting the parameters via setters
        /*CarbonCopy cc1 = new CarbonCopy();
        cc1.setEmail(DSConfig.CC_EMAIL);
        cc1.setName(DSConfig.CC_NAME);
        cc1.setRoutingOrder("2");
        cc1.setRecipientId("2");*/
        // Create signHere fields (also known as tabs) on the documents,
        // We're using anchor (autoPlace) positioning
        //
        // The DocuSign platform seaches throughout your envelope's
        // documents for matching anchor strings. So the
        // sign_here_2 tab will be used in both document 2 and 3 since they
        // use the same anchor string for their "signer 1" tabs.
               
        java.util.List<SignHere> signHereTabs1 = new ArrayList<SignHere>();
        /*java.util.List<SignHere> signHereTabs2 = new ArrayList<SignHere>();*/
        SignHere signHereApp = new SignHere();
        signHereApp.setDocumentId("1");
        signHereApp.setRecipientId("1");
        signHereApp.setPageNumber("8");
        signHereApp.setTabLabel("SignHereTab");
        signHereApp.setXPosition("80");
        signHereApp.setYPosition("420");
        signHereTabs1.add(signHereApp);

        /*SignHere signHereCoapp = new SignHere();
        signHereCoapp.setDocumentId("1");
        signHereCoapp.setRecipientId("2");
        signHereCoapp.setPageNumber("8");
        signHereCoapp.setTabLabel("SignHereTab");
        signHereCoapp.setXPosition("80");
        signHereCoapp.setYPosition("420");
        signHereTabs2.add(signHereCoapp);*/

        Tabs tabs1 = new Tabs();
        tabs1.setSignHereTabs(signHereTabs1);        
        signer1.setTabs(tabs1);
        
        /*Tabs tabs2 = new Tabs();
        tabs2.setSignHereTabs(signHereTabs2);
        signer2.setTabs(tabs2);*/
        
        Recipients recipients = new Recipients();
        List<Signer> signers = new ArrayList<Signer>();
        signers.add(signer1);
        /*signers.add(signer2);*/
        recipients.setSigners(signers);
        
        
        // recipients.setCarbonCopies(Arrays.asList(cc1));
        envelopeDefinition.setRecipients(recipients);
        // Request that the envelope be sent by setting |status| to "sent".
        // To request that the envelope be created as a draft, set to "created"
        envelopeDefinition.setStatus("sent");

        EnvelopesApi envelopeApi = new EnvelopesApi(this.apiClient);
        EnvelopeSummary results = envelopeApi.createEnvelope(this.getAccountId(), envelopeDefinition);

        return results;
    }
}
