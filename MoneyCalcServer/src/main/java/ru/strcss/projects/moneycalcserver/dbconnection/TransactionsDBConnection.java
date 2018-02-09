package ru.strcss.projects.moneycalcserver.dbconnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcserver.mongo.PersonTransactionsRepository;

import java.time.LocalDate;
import java.util.List;

import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.formatDateFromString;

@Component
public class TransactionsDBConnection {

    private PersonTransactionsRepository personTransactionsRepository;

    @Autowired
    public TransactionsDBConnection(PersonTransactionsRepository personTransactionsRepository) {
        this.personTransactionsRepository = personTransactionsRepository;
    }

    public List<Transaction> getTransactions(TransactionsSearchContainer container){

        String login = container.getLogin().replace("\"", "");

        LocalDate rangeFrom = formatDateFromString(container.getRangeFrom());
        LocalDate rangeTo = formatDateFromString(container.getRangeTo());

        return personTransactionsRepository.findTransactionsBetween(login, rangeFrom, rangeTo);
    }
}
