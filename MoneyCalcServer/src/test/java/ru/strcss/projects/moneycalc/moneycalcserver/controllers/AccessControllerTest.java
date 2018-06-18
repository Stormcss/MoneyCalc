package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.AccessDBConnection;

import java.util.Collections;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateAccess;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;

public class AccessControllerTest {
    private AccessDBConnection accessDBConnection = mock(AccessDBConnection.class);
    private AccessController accessController;

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = "successfulScenario")
    public void prepare_successfulScenario() {
        when(accessDBConnection.getAccess(anyString()))
                .thenReturn(generateAccess());

        accessController = new AccessController(accessDBConnection);
    }

    @BeforeGroups(groups = "failedScenario")
    public void prepare_failedScenario() {
        when(accessDBConnection.getAccess(anyString()))
                .thenReturn(null);

        accessController = new AccessController(accessDBConnection);
    }

    @Test(groups = "successfulScenario")
    public void testGetAccess() throws Exception {
        ResponseEntity<MoneyCalcRs<Access>> accessRs = accessController.getAccess();

        assertEquals(accessRs.getBody().getServerStatus(), Status.SUCCESS, accessRs.getBody().getMessage());
    }

    @Test(groups = "successfulScenario", expectedExceptions = UnsupportedOperationException.class)
    public void testSaveAccess() throws Exception {
        accessController.saveAccess(new IdentificationsUpdateContainer(generateIdentifications()));
    }

    @Test(groups = "failedScenario", dependsOnGroups = "successfulScenario")
    public void testGetAccess_failedScenario() throws Exception {
        ResponseEntity<MoneyCalcRs<Access>> accessRs = accessController.getAccess();

        assertEquals(accessRs.getBody().getServerStatus(), Status.ERROR, accessRs.getBody().getMessage());
    }

}