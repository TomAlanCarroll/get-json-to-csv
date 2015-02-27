package com.example;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.URLEncoder;

/**
 * A simple REST API client
 */
public class RestApiClient {
    /**
     * The base URL to execute HTTP requests against
     */
    private String baseUrl;

    /**
     * (optional): The parameter ID to provide for HTTP GET requests
     */
    private String parameterId;

    public RestApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public RestApiClient(String baseUrl, String parameterId) {
        this(baseUrl);
        this.parameterId = parameterId;
    }

    private RestApiClient () {} // Make the default constructor unavailable

    /**
     * Gets the {@code JsonNode} for a HTTP GET request at the given base URL
     * @param queryString The query String (which will be encoded)
     * @return The {@code JsonNode} for a HTTP GET request at the given base URL
     * @throws UnirestException If there are any problems while executing the HTTP GET request
     */
    public JsonNode getJson(String queryString) throws UnirestException {
        String builtGetUrl = buildGetUrl(queryString);

        HttpResponse<JsonNode> jsonResponse = Unirest.get(builtGetUrl).asJson();

        return jsonResponse.getBody();
    }

    /**
     * Builds a GET URL String
     * @param queryString The query string to be encoded and added to the URL
     * @return The baseUrl + the queryString separated either with "/" or "?parameterId=" depending on whether
     * parameterId has been set
     */
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
