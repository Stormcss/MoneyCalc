package ru.strcss.projects.moneycalcserver.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.strcss.projects.moneycalc.enitities.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

//    List<Transaction> findByLoginAndDateBetween(String login, String dateFrom, String dateTo);

//    Transaction deleteByLoginAnd_id(String login, String id);
}
