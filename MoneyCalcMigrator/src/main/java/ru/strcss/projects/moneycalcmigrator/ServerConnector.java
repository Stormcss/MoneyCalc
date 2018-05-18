package ru.strcss.projects.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.enitities.*;
import ru.strcss.projects.moneycalcmigrator.api.MigrationAPI;
import ru.strcss.projects.moneycalcmigrator.api.ServerConnectorI;
import ru.strcss.projects.moneycalcmigrator.dto.ConfigContainer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
class ServerConnector implements ServerConnectorI {

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

    public ServerConnector(ConfigContainer config) {
        this.config = config;
    }

    @Override
    public String login(Access access) {
        String token;
        try {
            token = service.login(access).execute().headers().get("Authorization");

            if (token == null) {
                token = registerPerson(access);
            }

        } catch (IOException e) {
            throw new RuntimeException("Getting Token has failed!", e);
        }

        return token;
    }

    @Override
    public List<SpendingSection> saveSpendingSection(String token, SpendingSection spendingSection) {
        try {
            return service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection)).execute().body().getPayload();
        } catch (IOException e) {
            throw new RuntimeException("Saving SpendingSection has failed", e);
        }
    }

//    /**
//     * Save Sections with provided names in DB
//     *
//     * @param sectionsNames - sections required to save in DB
//     * @return
//     * @throws IOException
//     */
//    public List<SpendingSection> saveSections(String token, Set<String> sectionsNames) {
//
//        AjaxRs<List<SpendingSection>> settingsResponse = service.getSpendingSections(token).execute().body();
//
//        String token = checkPersonRegistration(settingsResponse);
//
//
//        List<SpendingSection> existingSectionsList = getSectionsList(settingsResponse);
//
//        List<SpendingSection> spendingSectionsToAdd = new ArrayList<>();
//
//        for (String sectionName : sectionsNames) {
//            if (existingSectionsList.stream().anyMatch(spendingSection -> spendingSection.getName().equals(sectionName))) {
//                break;
//            }
//            spendingSectionsToAdd.add(generateSpendingSection(sectionName));
//        }
//
//        for (SpendingSection spendingSection : spendingSectionsToAdd) {
//            AjaxRs<List<SpendingSection>> addResponse = service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection)).execute().body();
//            if (addResponse == null || addResponse.getStatus() != Status.SUCCESS)
//                throw new RuntimeException("Error saving new Section!");
//        }
//
////        existingSettings.getSections().addAll(spendingSectionsToAdd);
////        AjaxRs<Settings> saveSettingResponse = service.saveSettings(existingSettings).execute().body();
//
//
//        log.info("Added following sections: {}. Additional Sections found in files: {}", spendingSectionsToAdd, sectionsNames);
//
//        return spendingSectionsToAdd;
//    }


    private String registerPerson(Access access) {

        Identifications identifications = Identifications.builder()
                .name(config.getName())
                .build();
        try {
            MoneyCalcRs<Person> registerResponse = service.registerPerson(new Credentials(access, identifications)).execute().body();
            if (registerResponse.getServerStatus() != Status.SUCCESS)
                throw new RuntimeException("Registration has failed", new RuntimeException(registerResponse.getMessage()));
            return service.login(access).execute().headers().get("Authorization");
        } catch (IOException e) {
            throw new RuntimeException("Registration has failed!", e);
        }
    }

    //    private String checkPersonRegistration(AjaxRs<List<SpendingSection>> settingsResponse) throws IOException {
//        if (settingsResponse.getStatus() == Status.ERROR && settingsResponse.getMessage().contains("does not exist")) {
//            Access access = Access.builder()
//                    .email(config.getEmail())
//                    .login(config.getLogin())
//                    .password(config.getPassword())
//                    .build();
//
//            Identifications identifications = Identifications.builder()
//                    .name(config.getName())
//                    .build();
//            AjaxRs<Person> registerResponse = service.registerPerson(new Credentials(access, identifications)).execute().body();
//            if (registerResponse.getStatus() != Status.SUCCESS)
//                throw new RuntimeException("Registration has failed", new RuntimeException(registerResponse.getMessage()));
//            return service.login(access).execute().headers().get("Authorization");
//        }
//    }
    @Override
    public List<SpendingSection> getSectionsList(String token) {
        try {
            return service.getSpendingSections(token).execute().body().getPayload();
        } catch (IOException e) {
            throw new RuntimeException("Getting SpendingSection list has failed!", e);
        }
    }


    @Override
    public Status saveTransactions(String token, List<Transaction> transactionsToAdd, String login) {
        boolean rollback = false;

        List<Transaction> addedTransactions = new ArrayList<>();

        for (Transaction transaction : transactionsToAdd) {
            try {
                MoneyCalcRs<Transaction> request = service.addTransaction(token, new TransactionAddContainer(transaction)).execute().body();
                if (request == null || request.getServerStatus() != Status.SUCCESS || request.getPayload() == null) {
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

//    private static String getToken(MigrationAPI service, Access access) {
//        try {
//            Headers headers = service.login(access).execute().headers();
//            return headers.get("Authorization");
//        } catch (IOException e) {
//            throw new RuntimeException("Can not get Token!", e);
//        }
//
//    }
}
