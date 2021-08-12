package com.example.books;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class QueryUtils {

    // Tag for the Log messages.
    private static final String LOG_TAG = QueryUtils.class.getName();

    // Returns new url object from given String url.
    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG,"Error with creating URl",e);
        }
        return url;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public static List<Books> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Books> books = extractBooks(jsonResponse);

        // Return the {@link Event}
        return books;
    }

    public static List<Books> extractBooks(String bookJson){
        List<Books> books = new ArrayList<>();
        try {
            JSONObject baseJsonObject = new JSONObject(bookJson);
            JSONArray booksArray = baseJsonObject.getJSONArray("items");
            for (int i=0;i<booksArray.length();i++){
                JSONObject currentBooks = booksArray.getJSONObject(i);
                JSONObject properties = currentBooks.getJSONObject("volumeInfo");
                String bookName = properties.getString("title");

                JSONObject imageObject= properties.getJSONObject("imageLinks");
                String image = imageObject.getString("smallThumbnail");

                String desc = properties.getString("description");

                String url = properties.getString("previewLink");

                StringBuilder authors = new StringBuilder();

                // Check whether the JSON results contain information on authors of the book
                if (properties.has("authors")) {
                    // JSON does have author information
                    // Extract the array that holds the data
                    JSONArray jsonAuthors = properties.getJSONArray("authors");
                    // Find and store the number of authors present in the authors array
                    int numberOfAuthors = jsonAuthors.length();
                    // Set max number of authors that can be displayed effectively without
                    // over-populating the view
                    int maxAuthors = 3;

					/* Sometimes author information within the author JSON array
					 is a single string item with concatenated authors separated by
					 a semicolon or a comma and this does not display itself properly on the
					 screen because there are too many authors along with the separators */

                    // Initialize variables
                    String cAuthors = "";
                    String[] allAuthors =  null;

                    // Length of the first item from the array is used to deterministically
                    // come to the conclusion that the authors are concatenated together
                    // as a single string
                    int numberOfLetters = jsonAuthors.get(0).toString().length();
                    // Conservatively set 40 as the max length for an author's name
                    if (numberOfLetters > 40) {
                        // Authors are concatenated
                        // Extract concatenated authors and remove beginning and trailing characters
                        // as a result of toString() artifact
                        cAuthors = jsonAuthors.toString().substring(2, numberOfLetters - 1);
                        // Split on semi-colons or commas
                        allAuthors = cAuthors.split("[;,]");
                        // Traverse the array and get up to max authors
                        for (int j = 0; j < allAuthors.length && j < maxAuthors; j++) {
                            authors.append(allAuthors[j].trim()).append("\n");
                        }

                    } else {
                        // Authors are not concatenated within the array as a single string item
                        // Traverse the json array and add authors to the newly initialized array
                        for (int j = 0; j < numberOfAuthors && j < maxAuthors; j++) {
                            authors.append(jsonAuthors.getString(j)).append("\n");
                        }
                    }

                }


                Books book = new Books(bookName,authors.toString(),image,desc,url);

                books.add(book);
            }


        }catch (Exception e){
            Log.e("QueryUtils","Problem parsing Book JSON result",e);
        }

        return books;
    }
}
