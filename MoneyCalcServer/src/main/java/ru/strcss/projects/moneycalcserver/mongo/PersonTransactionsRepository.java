package ru.strcss.projects.moneycalcserver.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.strcss.projects.moneycalc.enitities.PersonTransactions;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateFromString;


@Repository
public interface PersonTransactionsRepository extends MongoRepository<PersonTransactions, String> {

    // FIXME: 03.02.2018 Possible performance issues here
    List<PersonTransactions> findByLogin(String login);

    // FIXME: 05.02.2018 Possible performance issues here
//    PersonTransactions deleteByLoginAnd_id(String login, String id);

//    default Transaction deleteTransactionByLoginAnd_id(String login, String id){
//
//    }


    default List<Transaction> findTransactionsBetween(String login, LocalDate dateFrom, LocalDate dateTo){
        List<PersonTransactions> personTransactions = findByLogin(login);
        return personTransactions.stream()
                .map(PersonTransactions::getTransactions)
                .flatMap(Collection::stream)
                .filter(t -> isBetween(t, dateFrom, dateTo))
                .collect(Collectors.toList());
    }

    default List<Transaction> findTransactionsBetweenFilteredWithSection(String login, LocalDate dateFrom, LocalDate dateTo, List<Integer> sections){
        List<PersonTransactions> personTransactions = findByLogin(login);
        // TODO: 09.02.2018 Filter transactions using DB!
        return personTransactions.stream()
                .map(PersonTransactions::getTransactions)
                .flatMap(Collection::stream)
                .filter(transaction -> isBetween(transaction, dateFrom, dateTo))
                .filter(transaction -> sections.stream().anyMatch(id -> id.equals(transaction.getSectionID())))
                .collect(Collectors.toList());
    }

    default boolean isBetween(Transaction transaction, LocalDate dateFrom, LocalDate dateTo){
        LocalDate date = formatDateFromString(transaction.getDate());
        return (date.isAfter(dateFrom) || date.isEqual(dateFrom)) && (date.isBefore(dateTo) || date.isEqual(dateTo));
    }
}
