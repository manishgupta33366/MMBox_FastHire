package com.nga.xtendhr.fastHire.docusign;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.EnvelopesApi.ListStatusChangesOptions;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.EnvelopesInformation;
import org.joda.time.LocalDate;

import java.io.IOException;

/**
 * This class shows get status changes for one or more envelope(s).
 *
 */
public class ListEnvelopes extends ExampleBase{

    public ListEnvelopes(ApiClient apiClient) throws IOException {
        super(apiClient);
    }

    /**
     * get status for one or more envelope(s) in the last 30 days
     * @return - envelopes information
     * @throws ApiException
     */
    public EnvelopesInformation list() throws ApiException, IOException {

        this.checkToken();
        EnvelopesApi envelopeApi = new EnvelopesApi(this.apiClient);

        ListStatusChangesOptions options = envelopeApi.new ListStatusChangesOptions();
        LocalDate date = LocalDate.now().minusDays(30);
        options.setFromDate(date.toString("yyyy/MM/dd"));

        return envelopeApi.listStatusChanges(this.getAccountId(), options);

    }
}
