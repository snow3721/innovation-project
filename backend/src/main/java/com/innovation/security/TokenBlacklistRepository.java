package com.innovation.security;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends MongoRepository<TokenBlacklistEntry, String> {

    boolean existsByJti(String jti);
}
