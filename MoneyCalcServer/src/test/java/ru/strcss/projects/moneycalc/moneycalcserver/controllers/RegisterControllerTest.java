package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.RegistrationDBConnection;

import java.util.Collections;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class RegisterControllerTest {

    private RegistrationDBConnection registrationDBConnection = mock(RegistrationDBConnection.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    private MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private RegisterController registerController;

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = {"registerSuccessfulScenario", "registerIncorrectContainers"})
    public void prepareSuccessfulScenarioAndIncorrectContainers() {
        when(registrationDBConnection.isPersonExistsByEmail(anyString()))
                .thenReturn(false);
        when(registrationDBConnection.isPersonExistsByLogin(anyString()))
                .thenReturn(false);
        when(bCryptPasswordEncoder.encode(anyString()))
                .thenReturn(UUID());
        doNothing().when(mongoTemplate).save(anyObject());

        registerController = new RegisterController(registrationDBConnection, bCryptPasswordEncoder, mongoTemplate);
    }

    @Test(groups = "registerSuccessfulScenario")
    public void testRegisterPerson() {
        AjaxRs<Person> registerRs = registerController.registerPerson(generateCredentials());
        assertEquals(registerRs.getStatus(), Status.SUCCESS, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_emptyContainer() {
        AjaxRs<Person> registerRs = registerController.registerPerson(null);
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_emptyAccessAndIdentifications() {
        AjaxRs<Person> registerRs = registerController.registerPerson(new Credentials(null, null));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_emptyAccess() {
        AjaxRs<Person> registerRs = registerController.registerPerson(new Credentials(null, generateIdentifications()));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_AccessWithNulls() {
        AjaxRs<Person> registerRs = registerController.registerPerson(new Credentials(Access.builder().build(), generateIdentifications()));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_Access_LoginEmpty() {
        Access access = Access.builder().email("mail@mail.ru").password("123445").build();
        AjaxRs<Person> registerRs = registerController.registerPerson(new Credentials(access, generateIdentifications()));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_Access_PasswordEmpty() {
        Access access = Access.builder().login("login").email("mail@mail.ru").build();
        AjaxRs<Person> registerRs = registerController.registerPerson(new Credentials(access, generateIdentifications()));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_Access_EmailEmpty() {
        Access access = Access.builder().login("login").password("12345").build();
        AjaxRs<Person> registerRs = registerController.registerPerson(new Credentials(access, generateIdentifications()));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_IdentificationWithNulls() {
        AjaxRs<Person> registerRs = registerController.registerPerson(new Credentials(generateAccess(), Identifications.builder().build()));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerIncorrectContainers")
    public void testRegisterPerson_incorrectEmail() {
        Access access = generateAccess();
        access.setEmail("123");

        AjaxRs<Person> registerRs = registerController.registerPerson(new Credentials(access, generateIdentifications()));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());

        access.setEmail("123@mail");
        registerRs = registerController.registerPerson(new Credentials(access, generateIdentifications()));
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerFail", dependsOnGroups = {"registerSuccessfulScenario", "registerIncorrectContainers"})
    public void testRegisterPerson_ExistingLogin() {
        System.out.println("testRegisterPerson_ExistingLogin");
        when(registrationDBConnection.isPersonExistsByLogin(anyString())).thenReturn(true);
        registerController = new RegisterController(registrationDBConnection, bCryptPasswordEncoder, mongoTemplate);

        AjaxRs<Person> registerRs = registerController.registerPerson(generateCredentials());
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }

    @Test(groups = "registerFail", dependsOnGroups = {"registerSuccessfulScenario", "registerIncorrectContainers"})
    public void testRegisterPerson_ExistingEmail() {
        System.out.println("testRegisterPerson_ExistingEmail");
        when(registrationDBConnection.isPersonExistsByEmail(anyString())).thenReturn(true);
        registerController = new RegisterController(registrationDBConnection, bCryptPasswordEncoder, mongoTemplate);

        AjaxRs<Person> registerRs = registerController.registerPerson(generateCredentials());
        assertEquals(registerRs.getStatus(), Status.ERROR, registerRs.getMessage());
    }
}