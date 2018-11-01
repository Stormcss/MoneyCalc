package ru.strcss.projects.moneycalc.moneycalcserver.security;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.AccessService;

import static java.util.Collections.emptyList;

@Primary
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private AccessService accessService;

    public UserDetailsServiceImpl(AccessService accessService) {
        this.accessService = accessService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Access personAccess = accessService.getAccessByLogin(username);
        if (personAccess == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(personAccess.getLogin(), personAccess.getPassword(), emptyList());
    }
}
