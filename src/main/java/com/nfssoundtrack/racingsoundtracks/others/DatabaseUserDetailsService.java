package com.nfssoundtrack.racingsoundtracks.others;

import com.nfssoundtrack.racingsoundtracks.dbmodel.User;
import com.nfssoundtrack.racingsoundtracks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * no idea how this works, better to check on the web this implements service
 */
@Component
public class DatabaseUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userAccount = userRepository.findByLogin(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("User with username [" + username + "] not found in the system");
        }
        return new MyUserDetails(userAccount);
    }
}
