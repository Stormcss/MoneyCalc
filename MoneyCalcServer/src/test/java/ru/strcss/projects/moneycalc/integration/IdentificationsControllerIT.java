package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.integration.utils.Generator.UUID;
import static ru.strcss.projects.moneycalc.integration.utils.Generator.generateIdentifications;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.*;

@Slf4j
public class IdentificationsControllerIT extends AbstractControllerIT {

    @Test
    public void saveIdentifications() {
        Credentials credentials = savePersonGetCredentials(service);
        String login = credentials.getAccess().getLogin();

        //Updating default Identifications
        AjaxRs<Identifications> responseSaveIdentifications =
                sendRequest(service.saveIdentifications(new IdentificationsUpdateContainer(login, credentials.getIdentifications())), Status.SUCCESS).body();
        assertNotNull(responseSaveIdentifications.getPayload(), "Payload is null!");
        assertNotNull(responseSaveIdentifications.getPayload().getName(), "Identifications object is empty!");

        //Requesting updated Identifications
        AjaxRs<Identifications> responseGetUpdated = sendRequest(service.getIdentifications(new LoginGetContainer(login)), Status.SUCCESS).body();
        assertEquals(responseGetUpdated.getPayload().getLogin(), login, "returned Identifications object has wrong login!");
        assertEquals(responseGetUpdated.getPayload().getName(), credentials.getIdentifications().getName(),
                "returned Identifications object has wrong name!");
    }

    @Test
    public void saveIdentificationsIncorrectLogin() {
        Identifications identificationsIncorrect = generateIdentifications(UUID());
        identificationsIncorrect.setLogin(null);
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(UUID(), identificationsIncorrect);

        AjaxRs<Identifications> response = sendRequest(service.saveIdentifications(updateContainer)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Identifications object with incorrect Login is saved!");
    }

    @Test
    public void saveIdentificationsIncorrectName() {
        Identifications identificationsIncorrect = generateIdentifications(UUID());
        identificationsIncorrect.setName(null);
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(UUID(), identificationsIncorrect);

        AjaxRs<Identifications> response = sendRequest(service.saveIdentifications(updateContainer)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Identifications object with incorrect Name is saved!");
    }

    @Test
    public void getIdentifications() {
        String login = savePersonGetLogin(service);

        AjaxRs<Identifications> response = sendRequest(service.getIdentifications(new LoginGetContainer(login)), Status.SUCCESS).body();

        assertEquals(response.getPayload().getLogin(), login, "returned Identifications object has wrong login!");
    }

}