package com.cassinocards.cassino_api.repository.user;

import com.cassinocards.cassino_api.model.user.UnverifiedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnverifiedUserRepository extends JpaRepository<UnverifiedUser, Long> {

    Optional<UnverifiedUser> findUnverifiedUserByEmail(String email);
    Boolean existsAllByEmail(String email);
}
