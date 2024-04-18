package com.spring.url.shortner.url_shortner.contoller;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.url.shortner.url_shortner.constants.config;
import com.spring.url.shortner.url_shortner.services.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@CrossOrigin(origins = config.FRONT_END_URL) // Replace with your frontend app's origin
public class GenerateURL {
    
    @Autowired
    UrlService urlService;
     @Autowired
    private ObjectMapper objectMapper;
  
    @PostMapping("/gen")
    @ResponseBody
    public String generate(@RequestBody String longUrl, HttpServletRequest request) throws JsonMappingException, JsonProcessingException{
         JsonNode jsonNode = objectMapper.readTree(longUrl);
        String cleanedUrl = jsonNode.get("cleanedUrl").asText();
        
        return urlService.saveURL(cleanedUrl,request);
    }   


    @GetMapping("/{shortUrl}")
    @ResponseBody
    public String redirectToOriginalUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getLongURL(shortUrl);
      
        if (originalUrl == null) {
            System.out.println("ERROR");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else {
        System.out.println(originalUrl);
            return originalUrl;
        }
    }
}
