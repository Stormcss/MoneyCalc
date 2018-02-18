package ru.strcss.projects.moneycalcmigrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalcmigrator.api.MigrationAPI;
import ru.strcss.projects.moneycalcmigrator.utils.ConfigContainer;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
class Saver {

    private MigrationAPI service;
    private ConfigContainer config;

    @Autowired
    public Saver(ConfigContainer config) {
        this.config = config;

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getMoneyCalcServerHost() + ":" + config.getMoneyCalcServerPort())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MigrationAPI.class);
    }

    List<SpendingSection> saveSections(Set<String> sections) throws IOException {

        AjaxRs<Settings> settings = service.getSettings(config.getLogin()).execute().body();

//        sections.stream()
//                .map(section -> SpendingSection.builder()
//                        .budget(5000)
//                        .id())


//        settings.getPayload().getSections().addAll(sections);

//        AjaxRs<Settings> settings = service.saveSettings(config.getLogin()).execute().body();




        return settings.getPayload().getSections();

        //        RestTemplate restTemplate = new RestTemplate();
//        String fooResourceUrl
//                = config.getMoneyCalcServerHost() + ":" + config.getMoneyCalcServerPort();
//        ResponseEntity<Settings> response
//                = restTemplate.postForEntity(fooResourceUrl + "/api/settings/getSettings","Stormcss" , Settings.class);

    }
}
