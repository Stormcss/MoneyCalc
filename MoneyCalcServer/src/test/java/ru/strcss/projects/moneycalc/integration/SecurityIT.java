package ru.strcss.projects.moneycalc.integration;

import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.integration.utils.Pair;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetLoginAndToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;

public class SecurityIT extends AbstractIT {
    @Test
    public void correctLoginRequesting() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String login = loginAndToken.getLeft();
        String token = loginAndToken.getRight();

        Access getAccessRs = sendRequest(service.getAccess(token)).body();

        assertEquals(getAccessRs.getLogin(), login, "Login is not the same!");
    }
}
