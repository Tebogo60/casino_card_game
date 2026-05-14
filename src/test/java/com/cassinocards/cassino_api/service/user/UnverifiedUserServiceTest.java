package com.cassinocards.cassino_api.service.user;

import com.cassinocards.cassino_api.model.user.UnverifiedUser;
import com.cassinocards.cassino_api.dto.user.request.CreateUnverifiedUserDTO;
import com.cassinocards.cassino_api.repository.user.UnverifiedUserRepository;
import com.cassinocards.cassino_api.shared.EmailService;
import com.cassinocards.cassino_api.shared.exception.EmailAlreadyExistsException;
import com.cassinocards.cassino_api.shared.exception.InvalidTokenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnverifiedUserServiceTest {

    @Mock
    private UnverifiedUserRepository repository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UnverifiedUserService service;

    private CreateUnverifiedUserDTO createDTO(String email) {
        return new CreateUnverifiedUserDTO(email);
    }

    @Test
    void register_savesUserTest() {
        CreateUnverifiedUserDTO dto = createDTO("user@testuser.co");
        when(repository.existsAllByEmail(dto.email())).thenReturn(false);

        service.register(dto);
        verify(repository).save(any(UnverifiedUser.class));
    }

    @Test
    void register_throwException_whenEmailExistsTest() {
        CreateUnverifiedUserDTO dto = createDTO("user@testuser.co");
        when(repository.existsAllByEmail(dto.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> service.register(dto));

        verify(repository, never()).save(any());
    }

    @Test
    void register_savesAndSendEmailTest() {
        CreateUnverifiedUserDTO dto = createDTO("user2@testuser.co");
        when(repository.existsAllByEmail(dto.email())).thenReturn(false);

        service.register(dto);
        verify(repository).save(any(UnverifiedUser.class));
        verify(emailService).sendVerificationEmail(eq(dto.email()), any(UUID.class));
    }

    @Test
    void find_savedUnverifiedUser() {
        UUID token = UUID.randomUUID();

        UnverifiedUser user = UnverifiedUser.builder()
                .email("test@example.com")
                .verificationToken(token)
                .build();

        when(repository.findUnverifiedUserByVerificationToken(token))
                .thenReturn(Optional.of(user));

        UnverifiedUser result = service.find(token);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void find_throwsException_whenTokenNotFound() {
        UUID token = UUID.randomUUID();

        when(repository.findUnverifiedUserByVerificationToken(token))
                .thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> service.find(token));
    }
}
