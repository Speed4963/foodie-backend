package com.eatproject.backend.restaurant.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject; // org.json 라이브러리 사용 시

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@Service
public class NaverMapService {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    // 위도, 경도를 담을 레코드 (DTO 대용)
    public record Coordinate(Double lat, Double lng) {}

    public Coordinate getCoordinate(String address) {
        try {
            // 주소에 공백이나 특수문자가 있을 수 있으므로 인코딩이 필수입니다.
//            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            // NaverMapService.java 내 URL
            String apiURL = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode?query=" +address;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.set("X-NCP-APIGW-API-KEY", clientSecret);


            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(apiURL, HttpMethod.GET, entity, String.class);

            log.info("네이버 API 응답 전체 내용: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                var addresses = jsonObject.getJSONArray("addresses");

                if (addresses.length() > 0) {
                    JSONObject addrObj = addresses.getJSONObject(0);
                    Double lat = Double.parseDouble(addrObj.getString("y")); // 위도
                    Double lng = Double.parseDouble(addrObj.getString("x")); // 경도
                    return new Coordinate(lat, lng);
                }
            }
        } catch (Exception e) {
            log.error("Naver Geocoding API 호출 중 오류 발생: {}", e.getMessage());
        }
        return null; // 실패 시 null 반환
    }
}