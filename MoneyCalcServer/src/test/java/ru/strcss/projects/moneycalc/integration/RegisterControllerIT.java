package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.testutils.Generator;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateCredentials;

@Slf4j
public class RegisterControllerIT extends AbstractIT {

    @Test
    public void registerCorrectPerson() {
        Person registerRs = sendRequest(service.registerPerson(generateCredentials())).body();

        assertNotNull(registerRs.getId(), "id is null");
        assertNotNull(registerRs.getAccessId(), "accessId is null");
        assertNotNull(registerRs.getIdentificationsId(), "identificationsId is null");
        assertNotNull(registerRs.getSettingsId(), "SettingsId is null");
    }

    @Test
    public void registerExistingLoginPerson() {
        String login = Generator.UUID();

        sendRequest(service.registerPerson(generateCredentials(login)));

        Response<Person> saveExistingRs = sendRequest(service.registerPerson(generateCredentials(login)),
                HttpStatus.BAD_REQUEST);
        assertFalse(saveExistingRs.isSuccessful(), "Existing Person was saved!");
    }

    @Test
    public void registerExistingEmailPerson() {
        Credentials credentials = generateCredentials();

        sendRequest(service.registerPerson(credentials));

        //Creating new Credentials with the same Email
        credentials.getAccess().setLogin(Generator.UUID());

        Response<Person> saveExistingRs = sendRequest(service.registerPerson(credentials), HttpStatus.BAD_REQUEST);
        assertFalse(saveExistingRs.isSuccessful(), "Person with existing email was saved!");
    }

    @Test
    public void registerEmptyPassword() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setPassword(null);

        Response<Person> saveRs = sendRequest(service.registerPerson(credentials), HttpStatus.BAD_REQUEST);

        assertFalse(saveRs.isSuccessful(), "Person with empty password was saved!");
    }

    @Test
    public void registerEmptyEmail() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail(null);

        Response<Person> saveRs = sendRequest(service.registerPerson(credentials), HttpStatus.BAD_REQUEST);

        assertFalse(saveRs.isSuccessful(), "Person with empty password was saved!");
    }

    @Test
    public void registerIncorrectEmail() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail("123");

        Response<Person> saveRs = sendRequest(service.registerPerson(credentials), HttpStatus.BAD_REQUEST);

        assertFalse(saveRs.isSuccessful(), "Person with incorrect Email was saved!");
    }

    @Test
    public void registerEmptyLogin() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setLogin(null);

        Response<Person> registerRs = sendRequest(service.registerPerson(credentials), HttpStatus.BAD_REQUEST);

        assertFalse(registerRs.isSuccessful(), "Person with empty login was saved!");
    }

    @Test
    public void registerEmptyAll() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setPassword(null);
        credentials.getAccess().setLogin(null);
        credentials.getAccess().setEmail(null);
        credentials.getIdentifications().setName(null);

        Response<Person> saveRs = sendRequest(service.registerPerson(credentials), HttpStatus.BAD_REQUEST);

        assertFalse(saveRs.isSuccessful(), "Response is not failed!");
    }
}