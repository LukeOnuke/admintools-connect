package com.lukeonuke.admintoolsconnect.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ErrorModel {
    private String name;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
