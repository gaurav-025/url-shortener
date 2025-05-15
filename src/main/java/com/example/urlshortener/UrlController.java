package com.example.urlshortener;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/s")
public class UrlController {
    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlEntity> createShortUrl(@Valid @RequestBody UrlEntity urlEntity) {
        String originalUrl = urlEntity.getUrl();
        logger.info("Received request to create a short URL for: {}", originalUrl);

        // Validate the URL before processing
        if (!isValidUrl(originalUrl)) {
            logger.warn("Invalid URL provided: {}", originalUrl);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            UrlEntity createdUrl = urlService.createShortUrl(originalUrl);
//            createdUrl.setShortCode(urlService.getFullUrl(createdUrl));
            return new ResponseEntity<>(createdUrl, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating short URL for: {}", originalUrl, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/{shortCode}")
//    public ResponseEntity<UrlEntity> getOriginalUrl(@PathVariable String shortCode) {
//        logger.info("Received request to retrieve original URL for short code: {}", shortCode);
//
//        Optional<UrlEntity> urlEntity = urlService.getOriginalUrl(shortCode);
//        return urlEntity.map(ResponseEntity::ok)
//                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
//    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlEntity> updateShortUrl(@PathVariable String shortCode, @RequestBody UrlEntity urlEntity) {
        logger.info("Received request to update short URL for short code: {}", shortCode);

        Optional<UrlEntity> existingUrl = urlService.getOriginalUrl(shortCode);
        if (existingUrl.isPresent()) {
            UrlEntity updatedUrl = existingUrl.get();
            updatedUrl.setUrl(urlEntity.getUrl());
            updatedUrl.setUpdatedAt(LocalDateTime.now());
            urlService.updateUrl(updatedUrl);
            return new ResponseEntity<>(updatedUrl, HttpStatus.OK);
        } else {
            logger.warn("Short code not found for update: {}", shortCode);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        logger.info("Received request to delete short URL for short code: {}", shortCode);

        Optional<UrlEntity> urlEntity = urlService.getOriginalUrl(shortCode);
        if (urlEntity.isPresent()) {
            urlService.deleteUrl(shortCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Short code not found for deletion: {}", shortCode);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlEntity> getStatistics(@PathVariable String shortCode) {
        logger.info("Received request for statistics for short code: {}", shortCode);

        Optional<UrlEntity> urlEntity = urlService.getOriginalUrl(shortCode);
        if (urlEntity.isPresent()) {
            UrlEntity entity = urlEntity.get();
//            entity.setAccessCount(entity.getAccessCount() + 1);
//            urlService.updateUrl(entity);
            return new ResponseEntity<>(entity, HttpStatus.OK);
        } else {
            logger.warn("Short code not found for statistics: {}", shortCode);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Helper method to validate a URL format and check if it is reachable
    private boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI(); // Validate that the URL is well-formed
            return true; // Consider additional checks for reachability if necessary
        } catch (MalformedURLException | java.net.URISyntaxException e) {
            return false;
        }
    }

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable String shortCode, HttpServletResponse response){
        logger.info("Redirecting short code: {}",shortCode);
        Optional<UrlEntity> urlEntity=urlService.getOriginalUrl(shortCode);
        if(urlEntity.isPresent()){
            try{
//                response.sendRedirect(urlEntity.get().getUrl());
                UrlEntity entity=urlEntity.get();
                entity.setAccessCount(entity.getAccessCount()+1);
                urlService.save(entity);
                response.sendRedirect(urlEntity.get().getUrl());
            }
            catch(IOException e){
                logger.error("Error during redirection for short code: {}",shortCode,e);
            }
        }
        else{
            logger.warn("Short code not found for redirection: {}",shortCode);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
