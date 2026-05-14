package com.cassinocards.cassino_api.service.user;

import com.cassinocards.cassino_api.model.user.AuthProvider;
import com.cassinocards.cassino_api.model.user.PasswordResetToken;
import com.cassinocards.cassino_api.model.user.User;
import com.cassinocards.cassino_api.model.user.UserRole;
import com.cassinocards.cassino_api.dto.user.request.CreateUserDTO;
import com.cassinocards.cassino_api.dto.user.request.ForgotPasswordDTO;
import com.cassinocards.cassino_api.repository.user.PasswordResetTokenRepository;
import com.cassinocards.cassino_api.repository.user.UserRepository;
import com.cassinocards.cassino_api.shared.EmailService;
import com.cassinocards.cassino_api.shared.exception.UserFoundException;
import com.cassinocards.cassino_api.shared.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private CreateUserDTO createDTO(String username, String email, String password) {
        return new CreateUserDTO(username, email, password);
    }

    @Test
    void save_createsUser_whenEmailNotTaken() {
        CreateUserDTO dto = createDTO("tebogo_j", "test@example.com", "password123");
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("hashed_password");

        userService.save(dto);

        verify(userRepository).save(any(User.class));
        verify(emailService).sendUserCreatedEmail(dto.email());
    }

    @Test
    void save_throwsException_whenEmailAlreadyExists() {
        CreateUserDTO dto = createDTO("tebogo_j", "test@example.com", "password123");
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(UserFoundException.class, () -> userService.save(dto));

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendUserCreatedEmail(any());
    }

    @Test
    void save_hashesPassword_beforeSaving() {
        CreateUserDTO dto = createDTO("tebogo_j", "test@example.com", "password123");
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("hashed_password");

        userService.save(dto);

        verify(passwordEncoder).encode("password123");
    }

    @Test
    void save_setsCorrectRoleAndAuthProvider() {
        CreateUserDTO dto = createDTO("tebogo_j", "test@example.com", "password123");
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("hashed_password");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.save(dto);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(UserRole.PLAYER, savedUser.getRole());
        assertEquals(AuthProvider.LOCAL, savedUser.getAuthProvider());
        assertEquals("tebogo_j", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("hashed_password", savedUser.getPassword());
    }

    @Test
    void save_sendsEmail_afterUserCreated() {
        CreateUserDTO dto = createDTO("tebogo_j", "test@example.com", "password123");
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("hashed_password");

        userService.save(dto);

        verify(emailService).sendUserCreatedEmail("test@example.com");
    }

    @Test
    void forgotPassword_savesTokenAndSendsEmail() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO("john@example.com");

        User user = User.builder()
                .email("john@example.com")
                .username("john_doe")
                .build();

        when(userRepository.findUserByEmail(dto.email()))
                .thenReturn(Optional.of(user));

        userService.forgotPassword(dto);

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());

        PasswordResetToken saved = tokenCaptor.getValue();
        assertNotNull(saved.getToken());
        assertEquals(user, saved.getUser());
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now()));

        verify(emailService).sendPasswordResetEmail(
                eq(dto.email()), any(UUID.class));
    }

    @Test
    void forgotPassword_throwsException_whenEmailNotFound() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO("unknown@example.com");

        when(userRepository.findUserByEmail(dto.email()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.forgotPassword(dto));

        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }
}