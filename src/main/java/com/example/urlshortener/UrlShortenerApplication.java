package com.example.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlShortenerApplication {

    public static void main(String[] args) {
//        System.out.print("Project started!");
        SpringApplication.run(UrlShortenerApplication.class, args);
        System.out.print("Project started!");
    }

}
