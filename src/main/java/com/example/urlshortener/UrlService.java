package com.example.urlshortener;

//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;

@Service
public class UrlService {
    private static final String BASE62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlEntity createShortUrl(String originalUrl) {
        // Validate the URL before creating
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid or unreachable URL: " + originalUrl);
        }

        Optional<UrlEntity> existingEntity = urlRepository.findByUrl(originalUrl);
        if (existingEntity.isPresent()) {
//            existingEntity.get().setAccessCount(existingEntity.get().getAccessCount() + 1);
            urlRepository.save(existingEntity.get());
            return existingEntity.get();
        }

        String shortCode;
        do {
            shortCode = base62EncodeFromHex(generateUniqueId(originalUrl));
            if (shortCode.length() < 7) {
                // Pad the short code if it's too short
                shortCode = String.format("%-7s", shortCode).replace(' ', '0');
            }
        } while (urlRepository.existsByShortCode(shortCode));

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setUrl(originalUrl);
        urlEntity.setShortCode(shortCode.substring(0, 7));
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setUpdatedAt(LocalDateTime.now());
        return urlRepository.save(urlEntity);
    }

    public Optional<UrlEntity> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    public String getFullUrl(UrlEntity urlEntity) {
        return baseUrl + "/shorten/" + urlEntity.getShortCode();
    }

    public void updateUrl(UrlEntity urlEntity) {
        urlEntity.setUpdatedAt(LocalDateTime.now());
        urlRepository.save(urlEntity);
    }

    public UrlEntity save(UrlEntity urlEntity) {
        return urlRepository.save(urlEntity);
    }

    @Transactional
    public void deleteUrl(String shortCode) {
        urlRepository.deleteByShortCode(shortCode);
    }

    private String generateUniqueId(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().substring(0, Math.min(8, hexString.length()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

//    private String base62Encode(long num) {
//        StringBuilder encoded = new StringBuilder();
//        while (num > 0) {
//            encoded.insert(0, BASE62_CHARACTERS.charAt((int) (num % 62)));
//            num /= 62;
//        }
//        return encoded.toString();
//    }

    private String base62EncodeFromHex(String hex) {
        // Convert hex string to BigInteger to handle large hashes
        java.math.BigInteger number = new java.math.BigInteger(hex, 16);
        StringBuilder encoded = new StringBuilder();
        while (number.compareTo(java.math.BigInteger.ZERO) > 0) {
            int index = number.mod(java.math.BigInteger.valueOf(62)).intValue();
            encoded.insert(0, BASE62_CHARACTERS.charAt(index));
            number = number.divide(java.math.BigInteger.valueOf(62));
        }
        return encoded.toString();
    }

    private boolean isValidUrl(String urlString) {
        try {
            // Validate the URL format
            new URL(urlString).toURI(); // Ensures the URL format is valid
            return true;
        } catch (Exception e) {
            // Invalid URL format
            return false;
        }
    }

//    private boolean isValidUrl(String urlString) {
//        try {
//            // Validate the URL format
//            URL url = new URL(urlString);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(5000); // Set timeout for connection
//            connection.setReadTimeout(5000); // Set timeout for reading
//            int responseCode = connection.getResponseCode();
//            // Check if the URL is reachable (response code 200 indicates success)
//            return responseCode == 200;
//        } catch (MalformedURLException e) {
//            // Invalid URL format
//            return false;
//        } catch (IOException e) {
//            // URL is not reachable
//            return false;
//        }
//    }
}
