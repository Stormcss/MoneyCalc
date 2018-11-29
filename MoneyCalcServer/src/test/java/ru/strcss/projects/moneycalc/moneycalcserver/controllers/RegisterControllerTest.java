//package ru.strcss.projects.moneycalc.moneycalcserver.controllers;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.BeforeGroups;
//import org.testng.annotations.Test;
//import ru.strcss.projects.moneycalc.dto.Credentials;
//import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
//import ru.strcss.projects.moneycalc.dto.Status;
//import ru.strcss.projects.moneycalc.entities.Access;
//import ru.strcss.projects.moneycalc.entities.Identifications;
//import ru.strcss.projects.moneycalc.entities.Person;
//import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.RegisterService;
//
//import java.util.Collections;
//
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.testng.Assert.assertEquals;
//import static ru.strcss.projects.moneycalc.testutils.Generator.*;
//
//public class RegisterControllerTest {
//
//    private RegisterService registerService = mock(RegisterService.class);
//    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
//    private RegisterController registerController;
//
//    @BeforeClass
//    public void setUp() {
//        User user = new User("login", "password", Collections.emptyList());
//        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//    }
//
//    @BeforeGroups(groups = {"registerSuccessfulScenario", "registerIncorrectContainers"})
//    public void prepareSuccessfulScenarioAndIncorrectContainers() {
//        when(registerService.isPersonExistsByEmail(anyString()))
//                .thenReturn(false);
//        when(registerService.isPersonExistsByLogin(anyString()))
//                .thenReturn(false);
//        when(bCryptPasswordEncoder.encode(anyString()))
//                .thenReturn(UUID());
//
//        registerController = new RegisterController(registerService, bCryptPasswordEncoder);
//    }
//
//    @Test(groups = "registerSuccessfulScenario")
//    public void testRegisterPerson() {
//        ResponseEntity<MoneyCalcRs<Person>> registerRs = registerController.registerPerson(generateCredentials());
//        assertEquals(registerRs.getBody().getServerStatus(), Status.SUCCESS, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_emptyContainer() {
//        ResponseEntity<MoneyCalcRs<Person>> registerRs = registerController.registerPerson(null);
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_emptyAccessAndIdentifications() {
//        ResponseEntity<MoneyCalcRs<Person>> registerRs =
//                registerController.registerPerson(new Credentials(null, null));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_emptyAccess() {
//        ResponseEntity<MoneyCalcRs<Person>> registerRs =
//                registerController.registerPerson(new Credentials(null, generateIdentifications()));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_AccessWithNulls() {
//        ResponseEntity<MoneyCalcRs<Person>> registerRs =
//                registerController.registerPerson(new Credentials(Access.builder().build(), generateIdentifications()));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_Access_LoginEmpty() {
//        Access access = Access.builder().email("mail@mail.ru").password("123445").build();
//        ResponseEntity<MoneyCalcRs<Person>> registerRs =
//                registerController.registerPerson(new Credentials(access, generateIdentifications()));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_Access_PasswordEmpty() {
//        Access access = Access.builder().login("login").email("mail@mail.ru").build();
//        ResponseEntity<MoneyCalcRs<Person>> registerRs =
//                registerController.registerPerson(new Credentials(access, generateIdentifications()));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_Access_EmailEmpty() {
//        Access access = Access.builder().login("login").password("12345").build();
//        ResponseEntity<MoneyCalcRs<Person>> registerRs =
//                registerController.registerPerson(new Credentials(access, generateIdentifications()));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_IdentificationWithNulls() {
//        ResponseEntity<MoneyCalcRs<Person>> registerRs =
//                registerController.registerPerson(new Credentials(generateAccess(), Identifications.builder().build()));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerIncorrectContainers")
//    public void testRegisterPerson_incorrectEmail() {
//        Access access = generateAccess();
//        access.setEmail("123");
//
//        ResponseEntity<MoneyCalcRs<Person>> registerRs =
//                registerController.registerPerson(new Credentials(access, generateIdentifications()));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//
//        access.setEmail("123@mail");
//        registerRs = registerController.registerPerson(new Credentials(access, generateIdentifications()));
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerFail", dependsOnGroups = {"registerSuccessfulScenario", "registerIncorrectContainers"})
//    public void testRegisterPerson_ExistingLogin() {
//        System.out.println("testRegisterPerson_ExistingLogin");
//        when(registerService.isPersonExistsByLogin(anyString())).thenReturn(true);
//        registerController = new RegisterController(registerService, bCryptPasswordEncoder);
//
//        ResponseEntity<MoneyCalcRs<Person>> registerRs = registerController.registerPerson(generateCredentials());
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//
//    @Test(groups = "registerFail", dependsOnGroups = {"registerSuccessfulScenario", "registerIncorrectContainers"})
//    public void testRegisterPerson_ExistingEmail() {
//        System.out.println("testRegisterPerson_ExistingEmail");
//        when(registerService.isPersonExistsByEmail(anyString())).thenReturn(true);
//        registerController = new RegisterController(registerService, bCryptPasswordEncoder);
//
//        ResponseEntity<MoneyCalcRs<Person>> registerRs = registerController.registerPerson(generateCredentials());
//        assertEquals(registerRs.getBody().getServerStatus(), Status.ERROR, registerRs.getBody().getMessage());
//    }
//}