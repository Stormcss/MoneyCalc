package ru.strcss.projects.moneycalcmigrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import ru.strcss.projects.moneycalc.entities.*;
import ru.strcss.projects.moneycalcmigrator.api.MigrationAPI;
import ru.strcss.projects.moneycalcmigrator.api.ServerConnectorI;
import ru.strcss.projects.moneycalcmigrator.properties.MigrationProperties;
import ru.strcss.projects.moneycalcmigrator.utils.LocalDateAdapter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
class ServerConnector implements ServerConnectorI {

    private MigrationAPI service;
    private MigrationProperties properties;

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getMoneyCalcServerHost() + ":" + properties.getMoneyCalcServerPort())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(MigrationAPI.class);
    }

    public ServerConnector(MigrationProperties properties) {
        this.properties = properties;
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

    /**
     * Register person with provided Access object and return token Authorization header
     *
     * @return token Authorization header
     */
    private String registerPerson(Access access) {
        Identifications identifications = Identifications.builder()
                .name(properties.getName())
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
                MoneyCalcRs<Transaction> response = service.addTransaction(token, new TransactionAddContainer(transaction)).execute().body();
                if (response == null || response.getServerStatus() != Status.SUCCESS || response.getPayload() == null) {
                    rollback = true;
                    break;
                } else {
                    addedTransactions.add(response.getPayload());
                }
            } catch (Exception e) {
                rollback = true;
                e.printStackTrace();
                break;
            }
        }

        if (rollback) {
            log.error("Error has occurred while saving Transactions. Performing RollBack ... ");

            addedTransactions.forEach(transaction -> {
                        try {
                            service.deleteTransaction(token, new TransactionDeleteContainer(transaction.getId())).execute().body();
                        } catch (IOException e) {
                            log.error("Rollback for transaction id \"{}\" has failed", transaction.getId());
                            e.printStackTrace();
                        }
                    }
            );
        }
        return rollback ? Status.ERROR : Status.SUCCESS;
    }
}
