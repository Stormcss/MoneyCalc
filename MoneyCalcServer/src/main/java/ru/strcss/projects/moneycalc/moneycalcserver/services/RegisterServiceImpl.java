package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.entities.Person;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.RegistryMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SpendingSectionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.RegisterService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateRegisteringSettings;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateRegisteringSpendingSection;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    private RegistryMapper registryMapper;
    private SpendingSectionsMapper sectionsMapper;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterServiceImpl(RegistryMapper registryMapper, SpendingSectionsMapper sectionsMapper,
                               BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.registryMapper = registryMapper;
        this.sectionsMapper = sectionsMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Person registerUser(Credentials credentials) {

        credentials.getAccess().setPassword(bCryptPasswordEncoder.encode(credentials.getAccess().getPassword()));

        String login = credentials.getAccess().getLogin();

        log.info("Registering new User with Login: '{}' and name: '{}'", login, credentials.getIdentifications().getName());

        SpendingSection section1 = generateRegisteringSpendingSection("Еда", 5000L, 0);
        SpendingSection section2 = generateRegisteringSpendingSection("Свое", 5000L, 1);

        Person person = registryMapper.registerIds();

        registryMapper.registerUser(credentials, generateRegisteringSettings(), person);
        sectionsMapper.addSpendingSection(person.getId(), section1);
        sectionsMapper.addSpendingSection(person.getId(), section2);

        return person;
    }

    @Override
    public boolean isUserExistsByLogin(String login) {
        return registryMapper.isUserExistsByLogin(login);
    }

    @Override
    public boolean isUserExistsByEmail(String email) {
        return registryMapper.isUserExistsByEmail(email);
    }
}
