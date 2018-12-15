package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.AccessMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.AccessService;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class AccessServiceImpl implements AccessService, UserDetailsService {

    private AccessMapper accessMapper;

    @Override
    public Access getAccess(String login) {
        return accessMapper.getAccess(login);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Access personAccess = accessMapper.getAccess(username);
        if (personAccess == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(personAccess.getLogin(), personAccess.getPassword(), emptyList());
    }
}
