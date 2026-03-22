package com.cassinocards.cassino_api.service.user;

import com.cassinocards.cassino_api.model.user.AuthProvider;
import com.cassinocards.cassino_api.model.user.PasswordResetToken;
import com.cassinocards.cassino_api.model.user.User;
import com.cassinocards.cassino_api.model.user.UserRole;
import com.cassinocards.cassino_api.model.user.dto.CreateUserDTO;
import com.cassinocards.cassino_api.model.user.dto.ForgotPasswordDTO;
import com.cassinocards.cassino_api.repository.user.PasswordResetTokenRepository;
import com.cassinocards.cassino_api.repository.user.UnverifiedUserRepository;
import com.cassinocards.cassino_api.repository.user.UserRepository;
import com.cassinocards.cassino_api.shared.EmailService;
import com.cassinocards.cassino_api.shared.exception.UserFoundException;
import com.cassinocards.cassino_api.shared.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public void save(CreateUserDTO dto) {

        if (this.userRepository.existsByEmail(dto.email())) {
            throw new UserFoundException(dto.email());
        }

        User user = User.builder()
                .username(dto.username())
                .email(dto.email())
                .role(UserRole.PLAYER)
                .authProvider(AuthProvider.LOCAL)
                .password(passwordEncoder.encode(dto.password()))
                .build();

        userRepository.save(user);
        emailService.sendUserCreatedEmail(dto.email());
    }

    public void forgotPassword(ForgotPasswordDTO dto) {
        User user = userRepository.findUserByEmail(dto.email())
                .orElseThrow(() -> new UserNotFoundException(dto.email()));

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(UUID.randomUUID())
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(dto.email(), resetToken.getToken());
    }
}
