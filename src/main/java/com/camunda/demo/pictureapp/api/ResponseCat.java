package com.camunda.demo.pictureapp.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCat {
    
    String id;
    String url;
    int width;
    int height;

    @Override
    public String toString() {
        return "ResponseCat [id=" + id + ", url=" + url + "]";
    }
    
}
