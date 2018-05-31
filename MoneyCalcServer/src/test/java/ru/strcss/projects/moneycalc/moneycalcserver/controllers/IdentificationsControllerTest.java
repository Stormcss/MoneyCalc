package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
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
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.IdentificationsDBConnection;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;

public class IdentificationsControllerTest {

    private IdentificationsDBConnection identificationsDBConnection = mock(IdentificationsDBConnection.class);
    private IdentificationsController identificationsController;

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = {"SuccessfulScenario", "incorrectContainers"})
    public void prepare_successfulScenario_incorrectContainers() {
        when(identificationsDBConnection.getIdentifications(anyString()))
                .thenReturn(generateIdentifications());
        when(identificationsDBConnection.updateIdentifications(anyString(), any(IdentificationsUpdateContainer.class)))
                .thenReturn(new WriteResult(1, false, new Object()));

        identificationsController = new IdentificationsController(identificationsDBConnection);
    }

    @BeforeGroups(groups = "failedScenario")
    public void prepare_failedScenario() {
        when(identificationsDBConnection.getIdentifications(anyString()))
                .thenReturn(null);
        when(identificationsDBConnection.updateIdentifications(anyString(), any(IdentificationsUpdateContainer.class)))
                .thenReturn(new WriteResult(0, false, new Object()));

        identificationsController = new IdentificationsController(identificationsDBConnection);
    }

    @Test(groups = "SuccessfulScenario")
    public void testSaveIdentifications() {
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(generateIdentifications());
        ResponseEntity<MoneyCalcRs<Identifications>> saveIdentificationsRs = identificationsController.saveIdentifications(updateContainer);

        assertEquals(saveIdentificationsRs.getBody().getServerStatus(), Status.SUCCESS, saveIdentificationsRs.getBody().getMessage());
        assertEquals(updateContainer.getIdentifications(), saveIdentificationsRs.getBody().getPayload(), "Identifications are not equal!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetIdentifications() {
        ResponseEntity<MoneyCalcRs<Identifications>> getIdentificationsRs = identificationsController.getIdentifications();

        assertEquals(getIdentificationsRs.getBody().getServerStatus(), Status.SUCCESS, getIdentificationsRs.getBody().getMessage());
        assertNotNull(getIdentificationsRs.getBody().getPayload(), "Identifications is null!");
    }

    @Test(groups = "incorrectContainers")
    public void testSaveIdentifications_emptyIdentifications() {
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(null);
        ResponseEntity<MoneyCalcRs<Identifications>> saveIdentificationsRs = identificationsController.saveIdentifications(updateContainer);

        assertEquals(saveIdentificationsRs.getBody().getServerStatus(), Status.ERROR, saveIdentificationsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testSaveIdentifications_Identifications_emptyFields() {
        Identifications identifications = Identifications.builder().build();
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(identifications);

        ResponseEntity<MoneyCalcRs<Identifications>> saveIdentificationsRs = identificationsController.saveIdentifications(updateContainer);

        assertEquals(saveIdentificationsRs.getBody().getServerStatus(), Status.ERROR, saveIdentificationsRs.getBody().getMessage());
    }

    @Test(groups = "failedScenario", dependsOnGroups = {"SuccessfulScenario", "incorrectContainers"})
    public void testSaveIdentifications_failed() {
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(generateIdentifications());
        ResponseEntity<MoneyCalcRs<Identifications>> saveIdentificationsRs = identificationsController.saveIdentifications(updateContainer);

        assertEquals(saveIdentificationsRs.getBody().getServerStatus(), Status.ERROR, saveIdentificationsRs.getBody().getMessage());
    }

    @Test(groups = "failedScenario", dependsOnGroups = {"SuccessfulScenario", "incorrectContainers"})
    public void testGetIdentifications_failed() {
        ResponseEntity<MoneyCalcRs<Identifications>> getIdentificationsRs = identificationsController.getIdentifications();

        assertEquals(getIdentificationsRs.getBody().getServerStatus(), Status.ERROR, getIdentificationsRs.getBody().getMessage());
    }
}