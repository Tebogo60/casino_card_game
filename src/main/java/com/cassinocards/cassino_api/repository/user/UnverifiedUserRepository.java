package com.cassinocards.cassino_api.repository.user;

import com.cassinocards.cassino_api.model.user.UnverifiedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UnverifiedUserRepository extends JpaRepository<UnverifiedUser, Long> {

    Optional<UnverifiedUser> findUnverifiedUserByEmail(String email);
    Optional<UnverifiedUser> findUnverifiedUserByVerificationToken(UUID token);

    Boolean existsAllByEmail(String email);
    Boolean existsByVerificationToken(UUID token);
}
