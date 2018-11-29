package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.entities.Identifications;
import ru.strcss.projects.moneycalc.integration.utils.Pair;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.*;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;

@Slf4j
public class IdentificationsControllerIT extends AbstractIT {

    @Test
    public void saveIdentifications() {
        Pair<Credentials, String> credentialsAndToken = savePersonGetCredentialsAndToken(service);
        Credentials credentials = credentialsAndToken.getLeft();
        String token = credentialsAndToken.getRight();

        //Updating default Identifications
        MoneyCalcRs<Identifications> saveIdentificationsRs =
                sendRequest(service.saveIdentifications(token, credentials.getIdentifications()), Status.SUCCESS).body();
        assertNotNull(saveIdentificationsRs.getPayload().getName(), "Identifications object is empty!");

        //Requesting updated Identifications
        MoneyCalcRs<Identifications> getUpdatedRs = sendRequest(service.getIdentifications(token), Status.SUCCESS).body();
        assertEquals(getUpdatedRs.getPayload().getName(), credentials.getIdentifications().getName(),
                "returned Identifications object has wrong name!");
    }

    @Test
    public void saveIdentificationsIncorrectName() {
        String token = savePersonGetToken(service);
        Identifications incorrectIdentifications = generateIdentifications();
        incorrectIdentifications.setName(null);

        Response<MoneyCalcRs<Identifications>> saveIdentificationsRs = sendRequest(service.saveIdentifications(token, incorrectIdentifications));

        assertFalse(saveIdentificationsRs.isSuccessful(), "Identifications object with incorrect Name is saved!");
    }

    @Test
    public void getIdentifications() {
        Pair<Credentials, String> credentialsAndToken = savePersonGetCredentialsAndToken(service);
        Credentials credentials = credentialsAndToken.getLeft();
        String token = credentialsAndToken.getRight();

        MoneyCalcRs<Identifications> getIdentificationsRs = sendRequest(service.getIdentifications(token), Status.SUCCESS).body();

        assertEquals(getIdentificationsRs.getPayload().getName(), credentials.getIdentifications().getName(),
                "returned Identifications object has wrong name!");
    }

}