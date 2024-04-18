package com.spring.url.shortner.url_shortner.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "url_data")
public class Url {
    

    public Url() {
        super();
    }


public Url(int id, String longUrl, String shortUrl) {
        this.id = id;
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
    }

@Override
    public String toString() {
        return "Url [id=" + id + ", longUrl=" + longUrl + ", shortUrl=" + shortUrl + "]";
    }



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String longUrl;
    private String shortUrl;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getlongUrl() {
        return longUrl;
    }



    public void setlongUrl(String longUrl) {
        this.longUrl = longUrl;
    }



    public String getShortUrl() {
        return shortUrl;
    }



    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
     
    

}
