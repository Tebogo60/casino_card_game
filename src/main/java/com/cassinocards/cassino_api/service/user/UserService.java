package com.cassinocards.cassino_api.service.user;

import com.cassinocards.cassino_api.model.user.AuthProvider;
import com.cassinocards.cassino_api.model.user.User;
import com.cassinocards.cassino_api.model.user.UserRole;
import com.cassinocards.cassino_api.model.user.dto.CreateUserDTO;
import com.cassinocards.cassino_api.repository.user.UnverifiedUserRepository;
import com.cassinocards.cassino_api.repository.user.UserRepository;
import com.cassinocards.cassino_api.shared.EmailService;
import com.cassinocards.cassino_api.shared.exception.UserFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

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
}
