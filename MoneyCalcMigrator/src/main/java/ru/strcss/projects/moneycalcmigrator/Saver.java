package ru.strcss.projects.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
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
     * @param sections - sections required to save in DB
     * @return
     * @throws IOException
     */
    List<SpendingSection> saveSections(Set<String> sections) throws IOException {

        AjaxRs<Settings> settingsResponse = service.getSettings(config.getLogin()).execute().body();

        checkPersonRegistration(settingsResponse);

        Settings existingSettings = getSettings(settingsResponse);

        int offset = 0;
        List<SpendingSection> spendingSections = new ArrayList<>();

        for (String section : sections) {
            if (existingSettings.getSections().stream().anyMatch(spendingSection -> spendingSection.getName().equals(section)))
                break;
            offset++;
            // FIXME: 19.02.2018 Section ID must be created under the hood
            spendingSections.add(generateSpendingSection(existingSettings, offset, section));
        }

        existingSettings.getSections().addAll(spendingSections);
        AjaxRs<Settings> saveSettingResponse = service.saveSettings(existingSettings).execute().body();

        if (saveSettingResponse == null || saveSettingResponse.getStatus() != Status.SUCCESS)
            throw new RuntimeException("Error saving new Sections!");
        log.info("Added following sections: {}. Additional Sections found in files: {}", spendingSections, sections);

        return saveSettingResponse.getPayload().getSections();
    }

    /**
     * Check if requested Person exists. Registering Person if required
     *
     * @param settingsResponse - response object with Status of execution
     * @throws IOException
     */
    private void checkPersonRegistration(AjaxRs<Settings> settingsResponse) throws IOException {
        if (settingsResponse.getStatus() == Status.ERROR && settingsResponse.getMessage().contains("does not exist")) {
            Access access = Access.builder()
                    .email(config.getEmail())
                    .login(config.getLogin())
                    .password("password")
                    .build();

            Identifications identifications = Identifications.builder()
                    .login(config.getLogin())
                    .name(config.getName())
                    .build();
            AjaxRs<Person> registerResponse = service.registerPerson(new Credentials(access, identifications)).execute().body();
            if (registerResponse.getStatus() != Status.SUCCESS)
                throw new RuntimeException("Registration has failed", new RuntimeException(registerResponse.getMessage()));
        }
    }

    /**
     * Return actual Settings object. If previous request was failed then request once again
     *
     * @param settingsResponse - response object with Settings value, if it was successful
     * @return Settings
     * @throws IOException
     */
    private Settings getSettings(AjaxRs<Settings> settingsResponse) throws IOException {
        Settings existingSettings;
        if (settingsResponse.getStatus().equals(Status.SUCCESS)) {
            existingSettings = settingsResponse.getPayload();
        } else {
            existingSettings = service.getSettings(config.getLogin()).execute().body().getPayload();
        }
        return existingSettings;
    }

    /**
     * Generate SpendingSection with required fields
     *
     * @param existingSettings - used to calculate initial ID position
     * @param offset           - offset for ID calculation
     * @param section          - section name
     * @return SpendingSection object
     */
    private SpendingSection generateSpendingSection(Settings existingSettings, int offset, String section) {
        return SpendingSection.builder()
                .budget(5000)
                .isAdded(true)
                .name(section)
                .id(existingSettings.getSections().size() + offset)
                .build();
    }

    public Status saveTransactions(List<Transaction> transactionsToAdd, String login) {
        boolean rollback = false;

        List<Transaction> addedTransactions = new ArrayList<>();

        for (Transaction transaction : transactionsToAdd) {
            try {
                AjaxRs<Transaction> request = service.addTransaction(new TransactionAddContainer(login, transaction)).execute().body();
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

            addedTransactions.forEach(t -> {
                        try {
                            service.deleteTransaction(new TransactionDeleteContainer(login, t.get_id())).execute().body();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
        return rollback ? Status.ERROR : Status.SUCCESS;
    }
}
