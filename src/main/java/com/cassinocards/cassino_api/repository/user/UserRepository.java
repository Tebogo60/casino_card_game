package com.cassinocards.cassino_api.repository.user;

import com.cassinocards.cassino_api.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
