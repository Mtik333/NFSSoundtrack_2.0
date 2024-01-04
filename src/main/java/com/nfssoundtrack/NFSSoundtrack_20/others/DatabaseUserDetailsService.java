package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.User;
import com.nfssoundtrack.NFSSoundtrack_20.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

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
//        return new org.springframework.security.core.userdetails.User(userAccount.getLogin(), userAccount.getPass(),
//                Collections.singleton( new SimpleGrantedAuthority("ADMIN")));
        return new MyUserDetails(userAccount);
    }
}
