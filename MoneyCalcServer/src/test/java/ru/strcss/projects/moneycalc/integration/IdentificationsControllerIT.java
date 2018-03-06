package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.integration.utils.Pair;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.integration.utils.Generator.generateIdentifications;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.*;

@Slf4j
public class IdentificationsControllerIT extends AbstractIT {

    @Test
    public void saveIdentifications() {
        Pair<Credentials, String> credentialsAndToken = savePersonGetCredentialsAndToken(service);
        Credentials credentials = credentialsAndToken.getLeft();
        String token = credentialsAndToken.getRight();

        //Updating default Identifications
        AjaxRs<Identifications> responseSaveIdentifications =
                sendRequest(service.saveIdentifications(token, new IdentificationsUpdateContainer(credentials.getIdentifications())), Status.SUCCESS).body();
        assertNotNull(responseSaveIdentifications.getPayload(), "Payload is null!");
        assertNotNull(responseSaveIdentifications.getPayload().getName(), "Identifications object is empty!");

        //Requesting updated Identifications
        AjaxRs<Identifications> responseGetUpdated = sendRequest(service.getIdentifications(token), Status.SUCCESS).body();
//        assertEquals(responseGetUpdated.getPayload().getLogin(), login, "returned Identifications object has wrong login!");
        assertEquals(responseGetUpdated.getPayload().getName(), credentials.getIdentifications().getName(),
                "returned Identifications object has wrong name!");
    }

    @Test
    public void saveIdentificationsIncorrectName() {
        String token = savePersonGetToken(service);
        Identifications identificationsIncorrect = generateIdentifications();
        identificationsIncorrect.setName(null);
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(identificationsIncorrect);

        AjaxRs<Identifications> response = sendRequest(service.saveIdentifications(token, updateContainer)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Identifications object with incorrect Name is saved!");
    }

    @Test
    public void getIdentifications() {
        Pair<Credentials, String> credentialsAndToken = savePersonGetCredentialsAndToken(service);
        Credentials credentials = credentialsAndToken.getLeft();
        String token = credentialsAndToken.getRight();

        AjaxRs<Identifications> response = sendRequest(service.getIdentifications(token), Status.SUCCESS).body();

        assertEquals(response.getPayload().getName(), credentials.getIdentifications().getName(), "returned Identifications object has wrong name!");
    }

}