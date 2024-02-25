package com.camunda.demo.pictureapp.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDog {
    
    private String message;
    private String status;
    
}
