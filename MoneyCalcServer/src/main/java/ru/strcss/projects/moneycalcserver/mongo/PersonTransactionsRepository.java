package ru.strcss.projects.moneycalcserver.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.strcss.projects.moneycalc.enitities.PersonTransactions;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.formatDateFromString;

@Repository
public interface PersonTransactionsRepository extends MongoRepository<PersonTransactions, String> {

    // FIXME: 03.02.2018 Possible performance issues here
    List<PersonTransactions> findByLogin(String login);

    default List<Transaction> findTransactionsBetween(String login, LocalDate dateFrom, LocalDate dateTo){
        List<PersonTransactions> personTransactions = findByLogin(login);
        return personTransactions.stream()
                .map(PersonTransactions::getTransactions)
                .flatMap(Collection::stream)
                .filter(t -> isBetween(t, dateFrom, dateTo))
                .collect(Collectors.toList());
    }

    default boolean isBetween(Transaction transaction, LocalDate dateFrom, LocalDate dateTo){
        LocalDate date = formatDateFromString(transaction.getDate());
        return date.isAfter(dateFrom) && date.isBefore(dateTo);
    }

    Transaction deleteByLogin(String login);
}
