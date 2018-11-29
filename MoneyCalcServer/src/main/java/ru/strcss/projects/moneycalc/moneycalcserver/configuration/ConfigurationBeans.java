package ru.strcss.projects.moneycalc.moneycalcserver.configuration;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ConfigurationBeans {

    private final JettyProperties jettyProperties;

    @Autowired
    public ConfigurationBeans(JettyProperties jettyProperties) {
        this.jettyProperties = jettyProperties;
    }

    @Bean
    public JettyServletWebServerFactory jettyEmbeddedServletContainerFactory() {
        final JettyServletWebServerFactory factory = new JettyServletWebServerFactory();

        factory.addServerCustomizers((Server server) -> {
            final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
            threadPool.setMinThreads(jettyProperties.getMinThreads());
            threadPool.setMaxThreads(jettyProperties.getMaxThreads());
            threadPool.setIdleTimeout(jettyProperties.getIdleTimeout());
            threadPool.setName(jettyProperties.getThreadNamePrefix());
        });
        return factory;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
