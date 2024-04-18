package com.spring.url.shortner.url_shortner.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.url.shortner.url_shortner.cache.UrlCache;
import com.spring.url.shortner.url_shortner.constants.config;
import com.spring.url.shortner.url_shortner.model.Url;
import com.spring.url.shortner.url_shortner.repository.UrlRepo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UrlService {
  // private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 5;
  
    String shortUrl = "";
    String completeShortUrl ="";
    @Autowired
    UrlRepo urlRepo;
     
    @Autowired
    private UrlCache urlCache;
 
    public String saveURL(String longUrl,HttpServletRequest request){
        System.out.println("Service "+longUrl);
       if (urlCache.contains(longUrl)) {
       System.out.println("Cache: "+ urlCache.get(longUrl));
            return urlCache.get(longUrl);
        }
         String baseDomain = config.FRONT_END_URL;
    // Remove protocol (http or https) from the longUrl
        if (longUrl.startsWith("http://")) {
            longUrl = longUrl.substring(7);
        } else if (longUrl.startsWith("https://")) {
            longUrl = longUrl.substring(8);
        }

        // Remove 'www' from the longUrl if present
        if (longUrl.startsWith("www.")) {
            longUrl = longUrl.substring(4);
        }

        Optional<Url> existingMapping = urlRepo.findByLongUrl(longUrl);


        if (existingMapping.isPresent()) {
         shortUrl = existingMapping.get().getShortUrl();
        System.out.println("DB...");
         completeShortUrl = baseDomain + "/" + shortUrl;
        urlCache.put(longUrl, shortUrl);
        return shortUrl;
    }
       
        
        

        try {
            // Combine long URL and timestamp
            String combinedString = longUrl + Instant.now().toEpochMilli();

            // Generate SHA-256 hash object
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combinedString.getBytes(StandardCharsets.UTF_8));

            // Convert the hash bytes to a hexadecimal string
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashBuilder.append(String.format("%02x", b));
            }
            String hash = hashBuilder.toString();

            // Create the short code from the hash
            StringBuilder codeBuilder = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < CODE_LENGTH; i++) {
                int randomIndex = random.nextInt(hash.length());
                char randomChar = hash.charAt(randomIndex);
                codeBuilder.append(randomChar);
            }
            shortUrl = codeBuilder.toString();
            completeShortUrl = baseDomain + "/" + shortUrl;
            // Store the URL and its associated short code in the database
            Url url = new Url();
            url.setlongUrl(longUrl);
            url.setShortUrl(completeShortUrl);
            urlRepo.save(url);
          //  urlCache.put(longUrl, completeShortUrl);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
     System.out.println("Short: "+completeShortUrl);
        return completeShortUrl;
    }

public String getLongURL(String shortUrl) {
  
    Optional<Url> longUrl = urlRepo.findByShortUrl(config.FRONT_END_URL+"/"+shortUrl);
   
    if (longUrl.isPresent()) {
         
        return longUrl.get().getlongUrl();
    } else {
      
        return null; // Return null or handle the case when short URL is not found
    }
}

  
}

    