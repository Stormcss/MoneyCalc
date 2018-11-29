package ru.strcss.projects.moneycalc.moneycalcserver.services;

import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.UserMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.PersonService;

@Service
public class PersonServiceImpl implements PersonService {

    private UserMapper userMapper;

    public PersonServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Long getUserIdByLogin(String login) {
        return userMapper.getUserIdByLogin(login);
    }
}
