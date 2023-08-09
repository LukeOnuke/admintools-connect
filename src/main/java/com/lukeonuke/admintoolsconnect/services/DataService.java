package com.lukeonuke.admintoolsconnect.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lukeonuke.admintoolsconnect.AdminToolsConnect;
import com.lukeonuke.admintoolsconnect.models.ErrorModel;
import com.lukeonuke.admintoolsconnect.models.LogModel;
import com.lukeonuke.admintoolsconnect.models.QueuedCommandModel;
import lombok.Getter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

@Getter
public class DataService {
    private final ObjectMapper mapper;

    private static DataService instance;

    private final HttpClient client;

    private final String rootURL = "https://connect.admintools.app";

    private final ServerUUIDService serverUUIDService = ServerUUIDService.getInstance();

    private DataService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    private String get(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(rootURL + url))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> rsp = client.send(req, HttpResponse.BodyHandlers.ofString());

        // Generating response based of status codes
        int code = rsp.statusCode();
        if (code == 200) {
            // Response is OK
            return rsp.body();
        }
        if (code == 400) {
            throw new RuntimeException(mapper.readValue(rsp.body(), ErrorModel.class).getMessage());
        }
        throw new RuntimeException("Response code " + code);
    }

    private void post(String url, Object body) throws JsonProcessingException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(rootURL + url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }

    public void publishLog(String log) throws JsonProcessingException {
        if (!serverUUIDService.isUUIDConfigured()) return;
        post("/api/log", LogModel.builder().uuid(serverUUIDService.getServerUUID()).value(log).build());
    }

    public ArrayList<QueuedCommandModel> getCommandQueue() {
        if (!serverUUIDService.isUUIDConfigured()) return null;
        try {
            return mapper.readValue(get("/api/command/" + serverUUIDService.getServerUUID()), new TypeReference<>() {
            });
        } catch (IOException | InterruptedException e) {
            AdminToolsConnect.atLogger.severe("Error " + e.getClass().getSimpleName() + " " + e.getMessage() + " " + e.getCause().getMessage());
            return null;
        }
    }
}
