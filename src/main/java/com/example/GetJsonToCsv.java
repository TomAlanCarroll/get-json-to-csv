package com.example;

import com.mashape.unirest.http.JsonNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class GetJsonToCsv {
    private static final String BASE_URL = "http://api.goeuro.com/api/v2/position/suggest/en";
    private static final String OUTPUT_FILENAME = "out.csv";
    private static final String[] CSV_COLUMNS = {"_id", "name", "type", "latitude", "longitude"};

    /**
     * Queries GoEuro's REST API for location JSON and saves the results to a CSV.
     * @param args The arguments to the search:
     *             [0] => The query string that will be encoding and passed to the search
     *  (optional) [1] => The filename of the output file
     */
    public static void main(String[] args) {
        FileWriter fileWriter = null;
        CSVPrinter csvPrinter = null;
        CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator(System.lineSeparator());
        JSONArray apiData = null;
        JSONObject location = null;

        if (args.length > 0) {
            String queryString = args[0], specifiedOutputFilename = null;

            // Use the second argument to specify the output filename
            if (args.length > 1) {
                specifiedOutputFilename = args[1];
            }

            // Request the data from the REST API
            RestApiClient restApiClient = new RestApiClient(BASE_URL);

            System.out.println("Requesting GoEuro for location data...");

            // Get the JSON from the API
            try {
                JsonNode json = restApiClient.getJson(queryString);

                if (json.isArray()) {
                    apiData = json.getArray();
                } else {
                    throw new Exception("Returned JSON was not an array!");
                }
            } catch (Exception exception) {
                printBlockingError("Unable to obtain location data from the GoEuro API", exception);
                return;
            }

            // Write the necessary JSON fields to the CSV
            try {
                fileWriter = new FileWriter((specifiedOutputFilename != null) ? specifiedOutputFilename : OUTPUT_FILENAME);
                csvPrinter = new CSVPrinter(fileWriter, csvFormat);
                csvPrinter.printRecord(CSV_COLUMNS);

                // Write a row to out.csv
                for (int i = 0; i < apiData.length(); i++) {
                    try {
                        location = apiData.getJSONObject(i);
                    } catch (Exception exception) {
                        printWarning("Non-JSON Object encountered in response at index " + i + ".", exception);
                        continue; // Skip this row
                    }

                    List row = new ArrayList<String>();

                    // Add general data to the row
                    for (int j = 0; j < CSV_COLUMNS.length - 2; j++) {
                        try {
                            if (location.has(CSV_COLUMNS[j])) {
                                row.add(String.valueOf(location.get(CSV_COLUMNS[j])));
                            } else {
                                row.add("");
                            }
                        } catch (Exception exception) {
                            printWarning("Unable to get '" + CSV_COLUMNS[j] + "' from JSON Object response at index " + i + ".", exception);
                        }
                    }

                    // Add latitude/longitude to the row
                    try {
                        if (location.has("geo_position")) {
                            JSONObject geoPosition = location.getJSONObject("geo_position");
                            if (geoPosition.has("latitude")) {
                                row.add(String.valueOf(geoPosition.get("latitude")));
                            } else {
                                row.add("");
                            }
                            if (geoPosition.has("longitude")) {
                                row.add(String.valueOf(geoPosition.get("longitude")));
                            } else {
                                row.add("");
                            }
                        }
                    } catch (Exception exception) {
                        printWarning("Unable to get latitude/longitude from JSON Object response at index " + i + ".", exception);
                    }

                    // Add the row to the csv printer only if the row has the right number of columns
                    if (row.size() == CSV_COLUMNS.length) {
                        csvPrinter.printRecord(row);
                    } else {
                        printWarning("Unable to get data from JSON Object response at index " + i + ". " +
                                "The row was not added to the output file.");
                    }
                }
            } catch (Exception exception) { // Blocking error; Stop everything!
                printBlockingError("Unable to write GoEuro response to CSV file", exception);
                return;
            } finally {
                try {
                    fileWriter.close();
                    csvPrinter.close();
                } catch (Exception exception) {
                    printBlockingError("Unable to close CSV output file", exception);
                    return;
                }
            }

            System.out.println("--------------- SUCCESS ---------------");
        }
    }

    /**
     * Prints a total failure error message
     * @param message The message to output
     * @param exception The exception from which the message will be shown
     */
    private static void printBlockingError(String message, Exception exception) {
        System.out.println("Error: " + message + ". Exception message:");
        System.out.println(exception.getMessage());
        System.out.println("--------------- FAILURE ---------------");
    }

    /**
     * Prints a warning message
     * @param message The message to output
     * @param exception The exception from which the message will be shown
     */
    private static void printWarning(String message, Exception exception) {
        System.out.println("Warning: " + message + ". Exception message:");
        System.out.println(exception.getMessage());
    }

    /**
     * Prints a warning message
     * @param message The message to output
     */
    private static void printWarning(String message) {
        System.out.println("Warning: " + message + ".");
    }
}
