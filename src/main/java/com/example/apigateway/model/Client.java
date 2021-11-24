package com.example.apigateway.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Client {
    private int clientId;
    private String organizationCode;
}
