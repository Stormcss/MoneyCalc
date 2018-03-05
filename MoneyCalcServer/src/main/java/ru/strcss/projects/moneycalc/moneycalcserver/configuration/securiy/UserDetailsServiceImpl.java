package ru.strcss.projects.moneycalc.moneycalcserver.configuration.securiy;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.RegistrationDBConnection;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private RegistrationDBConnection registrationDBConnection;

    public UserDetailsServiceImpl(RegistrationDBConnection registrationDBConnection) {
        this.registrationDBConnection = registrationDBConnection;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Access personAccess = registrationDBConnection.getAccessByLogin(username);
        if (personAccess == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(personAccess.getLogin(), personAccess.getPassword(), emptyList());
    }
}
