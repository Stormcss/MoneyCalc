package ru.strcss.projects.moneycalc.moneycalcserver.configuration;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationBeans {

    private final JettyProperties jettyProperties;

    @Autowired
    public ConfigurationBeans(JettyProperties jettyProperties) {
        this.jettyProperties = jettyProperties;
    }

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory() {
        final JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();

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
