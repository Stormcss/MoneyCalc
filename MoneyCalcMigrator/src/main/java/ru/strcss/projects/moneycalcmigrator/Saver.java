package ru.strcss.projects.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.enitities.*;
import ru.strcss.projects.moneycalcmigrator.api.MigrationAPI;
import ru.strcss.projects.moneycalcmigrator.dto.ConfigContainer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
class Saver {

    private MigrationAPI service;
    private ConfigContainer config;

    @PostConstruct
    public void init() {
        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getMoneyCalcServerHost() + ":" + config.getMoneyCalcServerPort())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MigrationAPI.class);
    }

    @Autowired
    public Saver(ConfigContainer config) {
        this.config = config;
    }

    /**
     * Save Sections with provided names in DB
     *
     * @param sectionsNames - sections required to save in DB
     * @return
     * @throws IOException
     */
    List<SpendingSection> saveSections(Set<String> sectionsNames) throws IOException {

        AjaxRs<List<SpendingSection>> settingsResponse = service.getSpendingSections(token).execute().body();

        String token = checkPersonRegistration(settingsResponse);


        List<SpendingSection> existingSectionsList = getSectionsList(settingsResponse);

        List<SpendingSection> spendingSectionsToAdd = new ArrayList<>();

        for (String sectionName : sectionsNames) {
            if (existingSectionsList.stream().anyMatch(spendingSection -> spendingSection.getName().equals(sectionName))) {
                break;
            }
            spendingSectionsToAdd.add(generateSpendingSection(sectionName));
        }

        for (SpendingSection spendingSection : spendingSectionsToAdd){
            AjaxRs<List<SpendingSection>>  addResponse = service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection)).execute().body();
            if (addResponse == null || addResponse.getStatus() != Status.SUCCESS)
                throw new RuntimeException("Error saving new Section!");
        }

//        existingSettings.getSections().addAll(spendingSectionsToAdd);
//        AjaxRs<Settings> saveSettingResponse = service.saveSettings(existingSettings).execute().body();


        log.info("Added following sections: {}. Additional Sections found in files: {}", spendingSectionsToAdd, sectionsNames);

        return spendingSectionsToAdd;
    }

    /**
     *
     * Check if requested Person exists. Registering Person if required
     *
     * @param settingsResponse - response object with Status of execution
     * @return Token
     * @throws IOException
     */
    private String checkPersonRegistration(AjaxRs<List<SpendingSection>> settingsResponse) throws IOException {
        if (settingsResponse.getStatus() == Status.ERROR && settingsResponse.getMessage().contains("does not exist")) {
            Access access = Access.builder()
                    .email(config.getEmail())
                    .login(config.getLogin())
                    .password(config.getPassword())
                    .build();

            Identifications identifications = Identifications.builder()
                    .name(config.getName())
                    .build();
            AjaxRs<Person> registerResponse = service.registerPerson(new Credentials(access, identifications)).execute().body();
            if (registerResponse.getStatus() != Status.SUCCESS)
                throw new RuntimeException("Registration has failed", new RuntimeException(registerResponse.getMessage()));
            return service.login(access).execute().headers().get("Authorization");
        }
    }

    /**
     * Return actual Settings object. If previous request was failed then request once again
     *
     * @param settingsResponse - response object with Settings value, if it was successful
     * @return Settings
     * @throws IOException
     */
    private List<SpendingSection> getSectionsList(AjaxRs<List<SpendingSection>> settingsResponse) throws IOException {
        if (settingsResponse.getStatus().equals(Status.SUCCESS)) {
            return settingsResponse.getPayload();
        } else {
            return service.getSpendingSections(token).execute().body().getPayload();
        }
    }

    /**
     * Generate SpendingSection with required fields
     *
     * @param sectionName          - sectionName name
     * @return SpendingSection object
     */
    private SpendingSection generateSpendingSection(String sectionName) {
        return SpendingSection.builder()
                .budget(5000)
                .isAdded(true)
                .name(sectionName)
                .build();
    }

    public Status saveTransactions(List<Transaction> transactionsToAdd, String login) {
        boolean rollback = false;

        List<Transaction> addedTransactions = new ArrayList<>();

        for (Transaction transaction : transactionsToAdd) {
            try {
                AjaxRs<Transaction> request = service.addTransaction(token, new TransactionAddContainer(transaction)).execute().body();
                if (request == null || request.getStatus() != Status.SUCCESS || request.getPayload() == null) {
                    rollback = true;
                    break;
                } else {
                    addedTransactions.add(request.getPayload());
                }
            } catch (IOException e) {
                rollback = true;
                e.printStackTrace();
                break;
            }
        }

        if (rollback) {
            log.error("Error has occurred while saving Transactions. Performing RollBack ... ");

            addedTransactions.forEach(transaction -> {
                        try {
                            service.deleteTransaction(token, new TransactionDeleteContainer(transaction.get_id())).execute().body();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
        return rollback ? Status.ERROR : Status.SUCCESS;
    }
}
