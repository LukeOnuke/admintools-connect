package com.lukeonuke.admintoolsconnect.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Setter
public class QueuedCommandModel {
    private int id;
    private ServerModel server;
    private String value;
    private LocalDateTime createdAt;
}
