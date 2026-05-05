package com.cassinocards.cassino_api.service.user;

import com.cassinocards.cassino_api.repository.user.UserRepository;
import com.cassinocards.cassino_api.shared.exception.UserFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UserFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserFoundException(email));
    }
}