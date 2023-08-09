package com.lukeonuke.admintoolsconnect.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LogModel {
    private String uuid;
    private String value;
}