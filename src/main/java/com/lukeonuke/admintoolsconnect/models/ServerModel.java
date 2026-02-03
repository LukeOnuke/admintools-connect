package com.lukeonuke.admintoolsconnect.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Setter
public class ServerModel {
    private int id;
    private String uuid;
    private LocalDateTime createdAt;
}
