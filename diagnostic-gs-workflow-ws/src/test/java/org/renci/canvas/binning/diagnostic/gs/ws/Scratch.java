package org.renci.canvas.binning.diagnostic.gs.ws;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Test;

public class Scratch {

    @Test
    public void scratch() {

        Client client = ClientBuilder.newBuilder().newClient();
        WebTarget target = client.target("http://rest.genenames.org/fetch/symbol/A12M1");
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        Response response = builder.get();
        String result = response.readEntity(String.class);
        //response.getEntityTag().toString();

        // String restServiceURL = "http://rest.genenames.org/fetch/symbol/A12M1";
        // WebClient client = WebClient.create(restServiceURL).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
        //
        // Response response = client.get();
        // Object o = response.getEntity();
        System.out.println("");
    }
}
