package com.example;

import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetJsonToCsv {
    private static final String BASE_URL = "http://api.goeuro.com/api/v2/position/suggest/en";

    public static void main(String[] args) {
        if (args.length > 0) {
            String queryString = args[0], specifiedBaseUrl = null;

            if (args.length > 1) {
                specifiedBaseUrl = args[1];
            }

            RestApiClient restApiClient = new RestApiClient(
                    (specifiedBaseUrl != null && !specifiedBaseUrl.equals("")) ? specifiedBaseUrl : BASE_URL);

            System.out.println("Requesting GoEuro for location data...");

            try {
                JsonNode json = restApiClient.getJson(queryString);

                if (json.isArray()) {
                    JSONArray jsonArray = json.getArray();

                    System.out.println(jsonArray);
                    // TODO: Write to a CSV
                } else {
                    throw new Exception("Returned JSON was not an array!");
                }
            } catch (Exception exception) {
                System.out.println("Error: Unable to obtain location data from the GoEuro API. Exception message:");
                System.out.println(exception.getMessage());
                System.out.println("--------------- FAILURE ---------------");
                return;
            }

            System.out.println("--------------- SUCCESS ---------------");
        }
    }
}
