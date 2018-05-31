package ru.strcss.projects.moneycalc.moneycalcserver.configuration;

import org.eclipse.jetty.server.Server;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ConfigurationBeansTest {
    private JettyProperties jettyProperties = mock(JettyProperties.class);
    private ConfigurationBeans configurationBeans;

    @BeforeMethod
    public void setUp() {
        when(jettyProperties.getIdleTimeout()).thenReturn(100);
        when(jettyProperties.getMinThreads()).thenReturn(1);
        when(jettyProperties.getMaxThreads()).thenReturn(2);
        when(jettyProperties.getThreadNamePrefix()).thenReturn("prefix");

        configurationBeans = new ConfigurationBeans(jettyProperties);
    }

    @Test
    public void testJettyEmbeddedServletContainerFactory() {
        JettyEmbeddedServletContainerFactory factory = configurationBeans.jettyEmbeddedServletContainerFactory();

        Collection<JettyServerCustomizer> serverCustomizers = factory.getServerCustomizers();

        serverCustomizers.forEach(jettyServerCustomizer -> jettyServerCustomizer.customize(new Server(0)));

        assertEquals(serverCustomizers.size(), 1);
    }

    @Test
    public void testBCryptPasswordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = configurationBeans.bCryptPasswordEncoder();

        assertNotNull(bCryptPasswordEncoder);
    }

}