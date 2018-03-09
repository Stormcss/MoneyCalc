package ru.strcss.projects.moneycalc.integration;

import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.integration.utils.Pair;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.savePersonGetLoginAndToken;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.sendRequest;

public class SecurityIT extends AbstractIT {
    @Test
    public void correctLoginRequesting() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String login = loginAndToken.getLeft();
        String token = loginAndToken.getRight();

        AjaxRs<Access> getAccessRs = sendRequest(service.getAccess(token), Status.SUCCESS).body();

        assertEquals(getAccessRs.getPayload().getLogin(), login, "Login is not the same!");
    }
}
