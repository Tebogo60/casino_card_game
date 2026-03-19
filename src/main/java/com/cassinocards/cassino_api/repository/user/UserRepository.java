package com.cassinocards.cassino_api.repository.user;

import com.cassinocards.cassino_api.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);
    Boolean existsAllByUsername(String username);

    Optional<User> findUserByEmail(String email);
    Boolean existsAllByEmail(String email);
}
