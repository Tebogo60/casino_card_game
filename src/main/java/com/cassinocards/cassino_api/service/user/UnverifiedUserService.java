package com.cassinocards.cassino_api.service.user;

import com.cassinocards.cassino_api.model.user.UnverifiedUser;
import com.cassinocards.cassino_api.repository.user.UnverifiedUserRepository;
import com.cassinocards.cassino_api.model.user.dto.CreateUnverifiedUserDTO;
import com.cassinocards.cassino_api.shared.exception.EmailAlreadyExistsException;
import com.cassinocards.cassino_api.shared.EmailService;
import com.cassinocards.cassino_api.shared.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnverifiedUserService {

    private final UnverifiedUserRepository repository;
    private final EmailService emailService;

    public void register(CreateUnverifiedUserDTO dto) {
        if (repository.existsAllByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        UUID token = UUID.randomUUID();

        UnverifiedUser user = UnverifiedUser.builder()
                .email(dto.email())
                .verificationToken(token)
                .build();

        repository.save(user);
        emailService.sendVerificationEmail(dto.email(), token);
    }

    public UnverifiedUser find(UUID token) {
        return repository.findUnverifiedUserByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException(token));
    }
}
