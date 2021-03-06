package ru.strcss.projects.moneycalc.moneycalcmigrator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcmigrator.api.MigrationAPI;
import ru.strcss.projects.moneycalc.moneycalcmigrator.api.ServerConnectorI;
import ru.strcss.projects.moneycalc.moneycalcmigrator.model.exceptions.MigratorException;
import ru.strcss.projects.moneycalc.moneycalcmigrator.properties.MigrationProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
class ServerConnector implements ServerConnectorI {

    private MigrationAPI service;
    private MigrationProperties properties;

    @Override
    public String login(Access access) {
        String token;
        try {
            final Call<Void> login = service.login(access);
            final Response<Void> execute = login.execute();
            token = execute.headers().get("Authorization");
            if (token == null) {
                token = registerPerson(access);
            }
        } catch (IOException e) {
            throw new MigratorException("Getting Token has failed!", e);
        }
        return token;
    }

    @Override
    public SpendingSectionsSearchRs saveSpendingSection(String token, SpendingSection spendingSection) {
        try {
            return service.addSpendingSection(token, spendingSection).execute().body();
        } catch (IOException e) {
            throw new MigratorException("Saving SpendingSection has failed", e);
        }
    }

    /**
     * Register person with provided Access object and return token Authorization header
     *
     * @return token Authorization header
     */
    private String registerPerson(Access access) {
        Identifications identifications = new Identifications(null, properties.getName());
        try {
            Response<Person> registerResponse = service.registerPerson(new Credentials(access, identifications)).execute();
            if (!registerResponse.isSuccessful())
                throw new MigratorException("Registration has failed", new RuntimeException(registerResponse.message()));
            return service.login(access).execute().headers().get("Authorization");
        } catch (IOException e) {
            throw new MigratorException("Registration has failed!", e);
        }
    }

    @Override
    public SpendingSectionsSearchRs getSectionsList(String token) {
        try {
            return service.getSpendingSections(token).execute().body();
        } catch (IOException e) {
            throw new MigratorException("Getting SpendingSection list has failed!", e);
        }
    }


    @Override
    public boolean saveTransactions(String token, List<Transaction> transactionsToAdd, String login) {
        boolean rollback = false;

        List<Transaction> addedTransactions = new ArrayList<>();

        for (Transaction transaction : transactionsToAdd) {
            try {
                Response<Transaction> response = service.addTransaction(token, transaction).execute();
                if (!response.isSuccessful() || response.body() == null) {
                    rollback = true;
                    break;
                } else {
                    addedTransactions.add(response.body());
                }
            } catch (Exception e) {
                rollback = true;
                log.error("Failed adding transactions", e);
                break;
            }
        }

        if (rollback) {
            log.error("Error has occurred while saving Transactions. Performing RollBack ... ");

            addedTransactions.forEach(transaction -> {
                        try {
                            service.deleteTransaction(token, transaction.getId()).execute().body();
                        } catch (IOException e) {
                            log.error("Rollback for transaction id '{}' has failed. ", transaction.getId(), e);
                        }
                    }
            );
        }
        log.info("Added {} transactions...", addedTransactions.size());
        return !rollback;
    }
}
