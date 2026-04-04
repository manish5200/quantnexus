package com.quantnexus.security;

import com.quantnexus.domain.User;
import com.quantnexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Security: Loading JPA user context for [{}]", email);

        return userRepository.findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(()-> {
                    log.warn("Security Block: Unknown email attempt [{}]", email);
                    return new UsernameNotFoundException("User not found.");
                });

/*
//Without lambda expression :-

        // 1. Fetch the user from the database.
        Optional<User> userBox = userRepository.findByEmail(email);

        // 2. Check if the user is actually inside the box
        if(userBox.isPresent()) {
            // 3. Extract the raw database entity (JPA User)
            User user = userBox.get();
            // 4. Wrap it in our Enterprise Security context and return it
            SecurityUser securityUser = new SecurityUser(user);
            return securityUser;
        }else{
            // 5. If the box is empty, log the attempt and throw the exception
            log.warn("Security Block: Unknown email attempt [{}]", email);
            throw new UsernameNotFoundException("User not found in the system.");
        }
*/
    }
}
