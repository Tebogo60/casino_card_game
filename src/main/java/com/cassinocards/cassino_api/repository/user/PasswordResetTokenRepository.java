package com.cassinocards.cassino_api.repository.user;

import com.cassinocards.cassino_api.model.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
}
