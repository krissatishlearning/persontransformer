package com.example.persontransformer.repository;

import com.example.persontransformer.domain.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonMongoRepository extends MongoRepository<Person, String> {

    Optional<Person> findByExternalId(String externalId);
}
