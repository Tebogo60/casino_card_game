package com.cassinocards.cassino_api.model.user;

import com.cassinocards.cassino_api.model.user.dto.CreateUnverifiedUserDTO;
import com.cassinocards.cassino_api.repository.user.UnverifiedUserRepository;
import com.cassinocards.cassino_api.shared.EmailService;
import com.cassinocards.cassino_api.service.user.UnverifiedUserService;
import com.cassinocards.cassino_api.shared.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
        verify(emailService).sendVerificationEmail(eq(dto.email()), anyString());
    }
}
