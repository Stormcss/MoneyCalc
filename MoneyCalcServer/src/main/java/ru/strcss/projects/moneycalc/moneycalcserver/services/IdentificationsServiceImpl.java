package ru.strcss.projects.moneycalc.moneycalcserver.services;

import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.IdentificationsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.IdentificationsService;

@Service
public class IdentificationsServiceImpl implements IdentificationsService {

    private IdentificationsMapper identificationsMapper;

    public IdentificationsServiceImpl(IdentificationsMapper identificationsMapper) {
        this.identificationsMapper = identificationsMapper;
    }

    @Override
    public Identifications getIdentifications(String login) {
        return identificationsMapper.getIdentifications(login);
    }

    @Override
    public boolean updateIdentifications(String login, Identifications identifications) {
        return identificationsMapper.updateIdentifications(login, identifications) > 0;
    }
}
