package com.example.urlshortener;
//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<UrlEntity,String> {
    Optional<UrlEntity> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Optional<UrlEntity> findByUrl(String url);

    void deleteByShortCode(String shortCode);
}
