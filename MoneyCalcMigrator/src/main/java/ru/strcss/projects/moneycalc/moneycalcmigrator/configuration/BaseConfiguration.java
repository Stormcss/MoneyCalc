package ru.strcss.projects.moneycalc.moneycalcmigrator.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalc.moneycalcmigrator.api.MigrationAPI;
import ru.strcss.projects.moneycalc.moneycalcmigrator.properties.MigrationProperties;
import ru.strcss.projects.moneycalc.moneycalcmigrator.utils.LocalDateAdapter;

import java.time.LocalDate;

/**
 * Created by Stormcss
 * Date: 01.12.2018
 */
@Configuration
public class BaseConfiguration {

    @Bean
    MigrationAPI migrationAPI(MigrationProperties properties) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getMoneyCalcServerHost() + ":" + properties.getMoneyCalcServerPort())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(MigrationAPI.class);
    }
}
