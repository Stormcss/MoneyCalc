package ru.strcss.projects.moneycalcserver.controllers;

import org.junit.Assert;
import org.junit.Test;
import ru.strcss.projects.moneycalcserver.enitities.dto.*;

import java.util.UUID;

public class ApiControllerRegisterTest {

    @Test
    public void addPerson() {
        ApiControllerRegister apiControllerRegister = new ApiControllerRegister();

        Person person = Person.builder()
                .access(Access.builder()
                        .login("Vasya")
                        .password("qwerty")
                        .build())
                .ID(UUID.randomUUID().toString().replace("-", ""))
                .personalIdentifications(PersonalIdentifications.builder()
                        .name("Вася")
                        .build())
                .build();

        AjaxRs ajaxRs = apiControllerRegister.addPerson(person);
        Assert.assertTrue(ajaxRs.getStatus().equals(Status.SUCCESS));
    }

    @Test
    public void checkAddedPerson(){

    }
}