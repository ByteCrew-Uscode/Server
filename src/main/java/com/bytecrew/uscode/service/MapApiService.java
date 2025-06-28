package com.bytecrew.uscode.service;

import com.bytecrew.uscode.dto.Coordinates;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MapApiService {//webflux 로?


    @Value("${kakao.map.coordinates.url}")
    private String kakaoCoordinatesUrl;

    @Value("${kakao.client.id}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();


    public Coordinates getCoordinates(String address){ //코드 별로
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK "+kakaoApiKey);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("query", address);


        HttpEntity<String> entity = new HttpEntity<>(headers);
        JsonNode response = restTemplate.exchange(
                kakaoCoordinatesUrl,
                HttpMethod.GET,
                entity,
                JsonNode.class,
                uriVariables
        ).getBody();


        double latitude = response.get("documents").get(0).get("y").asDouble();
        double longitude = response.get("documents").get(0).get("x").asDouble();

        return new Coordinates(latitude, longitude);
    }
    private HttpHeaders setHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Integer extractTotalTime(JsonNode response) {
        return response.get("features").get(0).get("properties").get("totalTime").asInt() / 60;
    }


}

