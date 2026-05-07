package com.cassinocards.cassino_api.repository.user;

import com.cassinocards.cassino_api.model.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
