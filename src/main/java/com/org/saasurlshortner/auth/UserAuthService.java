package com.org.saasurlshortner.auth;

import com.org.saasurlshortner.exceptions.ResourceNotFoundException;
import com.org.saasurlshortner.model.UserModel;
import com.org.saasurlshortner.repository.UserModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService implements UserDetailsService {

    private final UserModelRepository userModelRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserModel> userModelOptional = userModelRepository.findByEmail(email);
        if(userModelOptional.isEmpty()) throw new ResourceNotFoundException("User with email " + email + " not found");
        UserModel user = userModelOptional.get();
        return new AuthUserDetails(user);
    }
}
