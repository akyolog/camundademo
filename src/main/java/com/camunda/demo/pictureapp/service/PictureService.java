package com.camunda.demo.pictureapp.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.camunda.demo.pictureapp.api.ResponseCat;
import com.camunda.demo.pictureapp.api.ResponseDog;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PictureService {

  @Value("${BASE_URI_CAT:https://api.thecatapi.com/v1}")
  String baseCatURI;

  @Value("${BASE_URI_DOG:https://dog.ceo/api}")
  String baseDogURI;
  

  @Bean
  RestClient restCatClient(){
    return RestClient.builder()
            .requestFactory(new HttpComponentsClientHttpRequestFactory())
            .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter()))
            .baseUrl(baseCatURI)
            .build();
  }

  @Bean
  RestClient restDogClient(){
    return RestClient.builder()
            .requestFactory(new HttpComponentsClientHttpRequestFactory())
            .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter()))
            .baseUrl(baseDogURI)
            .build();
  }


  public String selectAnimal(String animalType) throws MalformedURLException, IOException {
     
    //File image = null;
    String image = null;
    if(animalType == null || animalType.equalsIgnoreCase("cat")){
      ResponseEntity<List> responseEntity = restCatClient().get().uri("/images/search")
                                                  .accept(MediaType.APPLICATION_JSON)
                                                  .retrieve()
                                                  .toEntity(List.class);
          System.out.println(responseEntity.toString());
          if(responseEntity.getStatusCode().value() == HttpStatus.OK.value()){
            if(responseEntity.hasBody() && responseEntity.getBody() != null){
              ObjectMapper objectMapper = new ObjectMapper();
              ResponseCat cat = objectMapper.convertValue(responseEntity.getBody().get(0), ResponseCat.class) ;
              BufferedImage bufferedImage = ImageIO.read(new URL(cat.getUrl()));
              ByteArrayOutputStream bos = new ByteArrayOutputStream();
              //image = new File("cat_"+ cat.getId() +".jpg");
              ImageIO.write(bufferedImage, "jpg", bos);
              image = Base64.getEncoder().encodeToString(bos.toByteArray());
            }
          }
    } else if(animalType.equalsIgnoreCase("dog")){
      ResponseEntity<ResponseDog> responseEntity = restDogClient().get().uri("/breeds/image/random")
                                                  .accept(MediaType.APPLICATION_JSON)
                                                  .retrieve()
                                                  .toEntity(ResponseDog.class);
          System.out.println(responseEntity.toString());
          if(responseEntity.getStatusCode().value() == HttpStatus.OK.value()){
            if(responseEntity.hasBody() && responseEntity.getBody() != null){
              ObjectMapper objectMapper = new ObjectMapper();
              ResponseDog cat = objectMapper.convertValue(responseEntity.getBody(), ResponseDog.class) ;
              ByteArrayOutputStream bos = new ByteArrayOutputStream();
              BufferedImage bufferedImage = ImageIO.read(new URL(cat.getMessage()));
              //image = new File("dog_12345"+".jpg");
              ImageIO.write(bufferedImage, "jpg", bos);
              image = Base64.getEncoder().encodeToString(bos.toByteArray());
            }
          }
    }
    
    return image;
  }
}
