package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.PersonService;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.UserMapper;

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

//    @Override
//    public Integer getSettingsIdByPersonId(Integer userId) {
//        return personDao.getSettingsIdByPersonId(userId);
//    }
//
//    @Override
//    public Integer getAccessIdByPersonId(Integer userId) {
//        return personDao.getAccessIdByPersonId(userId);
//    }
//
//    @Override
//    public Integer getIdentificationsIdByPersonId(Integer userId) {
//        return personDao.getIdentificationsIdByPersonId(userId);
//    }
}
