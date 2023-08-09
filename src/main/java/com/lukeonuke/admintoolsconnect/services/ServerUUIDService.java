package com.lukeonuke.admintoolsconnect.services;

import com.lukeonuke.admintoolsconnect.AdminToolsConnect;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerUUIDService {
    private static ServerUUIDService instance = null;
    private final Path atcTxt = Path.of("atc.txt");
    @Getter
    private String serverUUID = null;

    private ServerUUIDService(){
        loadFromFile();
    }

    public static ServerUUIDService getInstance(){
        if(instance == null) instance = new ServerUUIDService();
        return instance;
    }

    public void setServerUUID(String serverUUID){
        this.serverUUID = serverUUID;
        try {
            Files.writeString(atcTxt, serverUUID, StandardCharsets.UTF_8);
        } catch (IOException e) {
            AdminToolsConnect.atLogger.severe("Could not write to atc.txt : " + e.getMessage());
        }
    }

    private void loadFromFile(){
        if(!atcTxt.toFile().exists()) {
            AdminToolsConnect.atLogger.severe("Admin Tools Connect is not configured!");
        }

        try {
            serverUUID = Files.readString(atcTxt, StandardCharsets.UTF_8);
        } catch (IOException e) {
            AdminToolsConnect.atLogger.severe("Could not read atc.txt : " + e.getMessage());
        }
    }

    public boolean isUUIDConfigured(){
        return serverUUID != null;
    }
}
