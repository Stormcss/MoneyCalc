package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.UserMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.PersonService;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    private UserMapper userMapper;

    @Override
    public Long getUserIdByLogin(String login) {
        return userMapper.getUserIdByLogin(login);
    }
}
