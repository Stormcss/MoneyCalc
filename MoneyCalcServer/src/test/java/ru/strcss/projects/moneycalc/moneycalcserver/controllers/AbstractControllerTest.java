package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeSuite;

/**
 * Created by Stormcss
 * Date: 29.11.2018
 */
public abstract class AbstractControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected MockMvc mockMvc;

    String USER_LOGIN = "User";

    @BeforeSuite
    void init() throws Exception {
        super.springTestContextPrepareTestInstance();
    }
}
