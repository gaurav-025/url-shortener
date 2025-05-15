package com.example.urlshortener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class baseUrlController {
    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/base-url")
    public Map<String,String> getBaseUrl() {
        return Map.of("baseUrl",baseUrl);
    }
}
