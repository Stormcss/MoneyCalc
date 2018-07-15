package ru.strcss.projects.moneycalcmigrator.properties;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.strcss.projects.moneycalcmigrator.dto.MigrationType;

@Configuration
@PropertySource(value = {"classpath:migration.properties"})
@ConfigurationProperties("moneyCalcMigrator")
@Getter
@Setter
public class MigrationProperties {

    @NotNull
    private MigrationType migrationType;

    @NotNull
    private String dataPath;

    @NotNull
    private String moneyCalcServerHost;
    @NotNull
    private String moneyCalcServerPort;

    @NonNull
    private String login;
    @NonNull
    private String password;
    @NonNull
    private String name;
    @NonNull
    private String email;

}
