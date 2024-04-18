package com.spring.url.shortner.url_shortner.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.spring.url.shortner.url_shortner.model.Url;


@Repository
public interface UrlRepo extends CrudRepository<Url,Integer> {
    Optional<Url> findByLongUrl(String longString);
    Optional<Url> findByShortUrl(String shortUrl);
}
