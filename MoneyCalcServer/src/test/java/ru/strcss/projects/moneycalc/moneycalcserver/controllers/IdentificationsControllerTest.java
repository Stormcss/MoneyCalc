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
import ru.strcss.projects.moneycalc.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.IdentificationsService;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.PersonService;

import java.util.Collections;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;

public class IdentificationsControllerTest {

    private IdentificationsService identificationsService = mock(IdentificationsService.class);
    private PersonService personService = mock(PersonService.class);
    private IdentificationsController identificationsController;

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = {"IdentificationsSuccessfulScenario", "IdentificationsIncorrectContainers"})
    public void prepare_successfulScenario_incorrectContainers() {
        when(identificationsService.getIdentificationsById(anyInt()))
                .thenReturn(generateIdentifications());
        when(identificationsService.updateIdentifications(any(Identifications.class)))
                .thenReturn(generateIdentifications());
        when(personService.getPersonIdByLogin(anyString()))
                .thenReturn(1);
        when(personService.getIdentificationsIdByPersonId(anyInt()))
                .thenReturn(1);
        identificationsController = new IdentificationsController(identificationsService, personService);
    }

    @BeforeGroups(groups = "IdentificationsFailedScenario")
    public void prepare_failedScenario() {
        when(identificationsService.getIdentificationsById(anyInt()))
                .thenReturn(null);
        when(identificationsService.updateIdentifications(any(Identifications.class)))
                .thenReturn(null);
        when(personService.getPersonIdByLogin(anyString()))
                .thenReturn(1);
        when(personService.getIdentificationsIdByPersonId(anyInt()))
                .thenReturn(1);
        identificationsController = new IdentificationsController(identificationsService, personService);
    }

    @Test(groups = "IdentificationsSuccessfulScenario")
    public void testSaveIdentifications() {
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(generateIdentifications());
        ResponseEntity<MoneyCalcRs<Identifications>> saveIdentificationsRs = identificationsController.updateIdentifications(updateContainer);

        assertEquals(saveIdentificationsRs.getBody().getServerStatus(), Status.SUCCESS, saveIdentificationsRs.getBody().getMessage());
        assertEquals(updateContainer.getIdentifications(), saveIdentificationsRs.getBody().getPayload(), "Identifications are not equal!");
    }

    @Test(groups = "IdentificationsSuccessfulScenario")
    public void testGetIdentifications() {
        ResponseEntity<MoneyCalcRs<Identifications>> getIdentificationsRs = identificationsController.getIdentifications();

        assertEquals(getIdentificationsRs.getBody().getServerStatus(), Status.SUCCESS, getIdentificationsRs.getBody().getMessage());
        assertNotNull(getIdentificationsRs.getBody().getPayload(), "Identifications is null!");
    }

    @Test(groups = "IdentificationsIncorrectContainers")
    public void testSaveIdentifications_emptyIdentifications() {
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(null);
        ResponseEntity<MoneyCalcRs<Identifications>> saveIdentificationsRs = identificationsController.updateIdentifications(updateContainer);

        assertEquals(saveIdentificationsRs.getBody().getServerStatus(), Status.ERROR, saveIdentificationsRs.getBody().getMessage());
    }

    @Test(groups = "IdentificationsIncorrectContainers")
    public void testSaveIdentifications_Identifications_emptyFields() {
        Identifications identifications = Identifications.builder().build();
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(identifications);

        ResponseEntity<MoneyCalcRs<Identifications>> saveIdentificationsRs = identificationsController.updateIdentifications(updateContainer);

        assertEquals(saveIdentificationsRs.getBody().getServerStatus(), Status.ERROR, saveIdentificationsRs.getBody().getMessage());
    }

    @Test(groups = "IdentificationsFailedScenario", dependsOnGroups = {"IdentificationsSuccessfulScenario", "IdentificationsIncorrectContainers"})
    public void testSaveIdentifications_failed() {
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(generateIdentifications());
        ResponseEntity<MoneyCalcRs<Identifications>> saveIdentificationsRs = identificationsController.updateIdentifications(updateContainer);

        assertEquals(saveIdentificationsRs.getBody().getServerStatus(), Status.ERROR, saveIdentificationsRs.getBody().getMessage());
    }

    @Test(groups = "IdentificationsFailedScenario", dependsOnGroups = {"IdentificationsSuccessfulScenario", "IdentificationsIncorrectContainers"})
    public void testGetIdentifications_failed() {
        ResponseEntity<MoneyCalcRs<Identifications>> getIdentificationsRs = identificationsController.getIdentifications();

        assertEquals(getIdentificationsRs.getBody().getServerStatus(), Status.ERROR, getIdentificationsRs.getBody().getMessage());
    }
}