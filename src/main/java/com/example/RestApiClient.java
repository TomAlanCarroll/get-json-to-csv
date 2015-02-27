package com.example;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.URLEncoder;

public class RestApiClient {
    private String baseUrl;
    private String parameterId;

    public RestApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public RestApiClient(String baseUrl, String parameterId) {
        this(baseUrl);
        this.parameterId = parameterId;
    }

    private RestApiClient () {}

    public JsonNode getJson(String queryString) throws UnirestException {
        String builtGetUrl = buildGetUrl(queryString);

        HttpResponse<JsonNode> jsonResponse = Unirest.get(builtGetUrl).asJson();

        return jsonResponse.getBody();
    }

    protected String buildGetUrl(String queryString) {
        try {
            if (parameterId == null || parameterId.equals("")) {
                return baseUrl + "/" + URLEncoder.encode(queryString, "UTF-8");
            } else {
                return baseUrl + "?" + parameterId + "=" + URLEncoder.encode(queryString, "UTF-8");
            }
        } catch (Exception exception) {
            System.out.println("Error: Unable to URL encode query string: " + queryString);
            return "";
        }
    }
}
