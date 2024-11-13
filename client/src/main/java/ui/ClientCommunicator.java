package ui;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ClientCommunicator {

    private final String serverUrl;

    public ClientCommunicator(String url) {
        serverUrl = url;
    }

    <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws Exception {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            writeBody(request, http, authToken);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
    }


    private static void writeBody(Object request, HttpURLConnection http, String authToken) throws IOException {
        if (authToken != null) {
            http.setRequestProperty("authorization", authToken);
        }
        if (request != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new IOException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}