package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.entities.Person;
import ru.strcss.projects.moneycalc.testutils.Generator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateCredentials;

@Slf4j
public class RegisterControllerIT extends AbstractIT {

    @Test
    public void registerCorrectPerson() {
        MoneyCalcRs<Person> registerRs = sendRequest(service.registerPerson(generateCredentials())).body();

        assertEquals(registerRs.getServerStatus(), Status.SUCCESS, registerRs.getMessage());
    }

    @Test
    public void registerExistingLoginPerson() {
        String login = Generator.UUID();

        MoneyCalcRs<Person> registerNewRs = sendRequest(service.registerPerson(generateCredentials(login))).body();
        assertEquals(registerNewRs.getServerStatus(), Status.SUCCESS, "New Person was not saved!");

        Response<MoneyCalcRs<Person>> saveExistingRs = sendRequest(service.registerPerson(generateCredentials(login)));
        assertFalse(saveExistingRs.isSuccessful(), "Existing Person was saved!");
    }

    @Test
    public void registerExistingEmailPerson() {
        Credentials credentials = generateCredentials();

        MoneyCalcRs<Person> saveNewRs = sendRequest(service.registerPerson(credentials)).body();
        assertEquals(saveNewRs.getServerStatus(), Status.SUCCESS, "New Person was not saved!");

        //Creating new Credentials with the same Email
        credentials.getAccess().setLogin(Generator.UUID());

        Response<MoneyCalcRs<Person>> saveExistingRs = sendRequest(service.registerPerson(credentials));
        assertFalse(saveExistingRs.isSuccessful(), "Person with existing email was saved!");
    }

    @Test
    public void registerEmptyPassword() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setPassword(null);

        Response<MoneyCalcRs<Person>> saveRs = sendRequest(service.registerPerson(credentials));

        assertFalse(saveRs.isSuccessful(), "Person with empty password was saved!");
    }

    @Test
    public void registerEmptyEmail() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail(null);

        Response<MoneyCalcRs<Person>> saveRs = sendRequest(service.registerPerson(credentials));

        assertFalse(saveRs.isSuccessful(), "Person with empty password was saved!");
    }

    @Test
    public void registerIncorrectEmail() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail("123");

        Response<MoneyCalcRs<Person>> saveRs = sendRequest(service.registerPerson(credentials));

        assertFalse(saveRs.isSuccessful(), "Person with incorrect Email was saved!");
    }

    @Test
    public void registerEmptyLogin() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setLogin(null);

        Response<MoneyCalcRs<Person>> registerRs = sendRequest(service.registerPerson(credentials));

        assertFalse(registerRs.isSuccessful(), "Person with empty login was saved!");
    }

    @Test
    public void registerEmptyAll() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setPassword(null);
        credentials.getAccess().setLogin(null);
        credentials.getAccess().setEmail(null);
        credentials.getIdentifications().setName(null);

        Response<MoneyCalcRs<Person>> saveRs = sendRequest(service.registerPerson(credentials));

        assertFalse(saveRs.isSuccessful(), "Response is not failed!");
    }
}