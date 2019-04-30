package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.integration.utils.Pair;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetCredentialsAndToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;

@Slf4j
public class IdentificationsControllerIT extends AbstractIT {

    @Test
    public void saveIdentifications() {
        Pair<Credentials, String> credentialsAndToken = savePersonGetCredentialsAndToken(service);
        Credentials credentials = credentialsAndToken.getLeft();
        String token = credentialsAndToken.getRight();

        //Updating default Identifications
        Identifications saveIdentificationsRs =
                sendRequest(service.saveIdentifications(token, credentials.getIdentifications())).body();
        assertNotNull(saveIdentificationsRs.getName(), "Identifications object is empty!");

        //Requesting updated Identifications
        Identifications getUpdatedRs = sendRequest(service.getIdentifications(token)).body();
        assertEquals(getUpdatedRs.getName(), credentials.getIdentifications().getName(),
                "returned Identifications object has wrong name!");
    }

    @Test
    public void saveIdentificationsIncorrectName() {
        String token = savePersonGetToken(service);
        Identifications incorrectIdentifications = generateIdentifications();
        incorrectIdentifications.setName(null);

        sendRequest(service.saveIdentifications(token, incorrectIdentifications), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getIdentifications() {
        Pair<Credentials, String> credentialsAndToken = savePersonGetCredentialsAndToken(service);
        Credentials credentials = credentialsAndToken.getLeft();
        String token = credentialsAndToken.getRight();

        Identifications getIdentificationsRs = sendRequest(service.getIdentifications(token)).body();

        assertEquals(getIdentificationsRs.getName(), credentials.getIdentifications().getName(),
                "returned Identifications object has wrong name!");
    }

}