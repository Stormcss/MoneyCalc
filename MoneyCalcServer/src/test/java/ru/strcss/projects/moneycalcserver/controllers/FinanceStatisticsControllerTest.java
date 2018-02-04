package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalc.dto.*;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcserver.controllers.testapi.MoneyCalcClient;
import ru.strcss.projects.moneycalcserver.controllers.utils.Generator;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.personGenerator;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class FinanceStatisticsControllerTest extends AbstractTestNGSpringContextTests {

    @LocalServerPort
    public int SpringBootPort;

    private MoneyCalcClient service;

    @BeforeClass
    public void init(){
        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:" + SpringBootPort + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MoneyCalcClient.class);
    }

//    @Test
//    public void getTransactions() throws IOException {
//        Person person = personGenerator();
//
//        //Registering Person
//        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
//        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());
//
//        //Saving Transactions
//        Response<AjaxRs<List<Transaction>>> responseCreatePerson = sendRequest(service.addTransaction(Generator.generateTransaction()));
//
//        //Requesting Transactions
//        Response<AjaxRs<List<Transaction>>> responseGetTransactions = sendRequest(service.getTransactions(person.getAccess().getLogin()));
//
//        List<Transaction> personTransactions = person.getFinance().getFinanceStatistics().getTransactions();
//        List<Transaction> requestedTransactions = responseGetTransactions.body().getPayload();
//
//        assertEquals(responseGetTransactions.body().getStatus(), Status.SUCCESS, responseGetTransactions.body().getMessage());
//        assertEquals(requestedTransactions.size(), personTransactions.size(),
//                "Transactions list size in generated Person is not equal to returned list of Transactions");
//
//        if (personTransactions.size() > 0) {
//            for (int i = 0; i < personTransactions.size(); i++) {
//                int randomIndex = ThreadLocalRandom.current().nextInt(personTransactions.size() - 1);
//                assertEquals(requestedTransactions.get(randomIndex).getSum(), personTransactions.get(randomIndex).getSum(),
//                        "Transactions in generated Person and returned are not equal!");
//            }
//        }
//    }

    /**
     * 1) Saving new Transaction to empty list
     * 2)
     *
     * @throws IOException
     */

    @Test
    public void saveNewTransaction() throws IOException {
        String login = UUID();

        Person person = personGenerator(login);

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());


        Transaction transaction = Generator.generateTransaction();

//        Adding new Transaction
        Response<AjaxRs<Transaction>> responseAddTransaction = sendRequest(service.addTransaction(new TransactionContainer(transaction, login)));
        assertEquals(responseAddTransaction.body().getStatus(), Status.SUCCESS, responseAddTransaction.body().getMessage());

        //Checking that it is added
        TransactionsSearchContainer container = new TransactionsSearchContainer(login, formatDateToString(generateDateMinus(ChronoUnit.DAYS, 1)),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)));
        Response<AjaxRs<List<Transaction>>> responseGetTransactions = sendRequest(service.getTransactions(container));

        assertEquals(responseGetTransactions.body().getStatus(), Status.SUCCESS, responseGetTransactions.body().getMessage());
        assertEquals(responseGetTransactions.body().getPayload().size(), 1, "Size of returned Transactions list is not 1!");

    }

//    @AfterClass
//    public static void shutdownDB() throws InterruptedException {
//        if (mongo != null) mongo.close();
//        if (mongoProcess != null) mongoProcess.stop();
//    }

//    @Test
//    public void addTransaction() throws IOException {
//        String login = UUID();
//        Person person = personGenerator(login, 1, 1);
//
//        //Registering Person
//        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
//        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());
//
//        Transaction transaction = Generator.generateTransaction();
//
//        //Adding transaction
//        Response<AjaxRs<Transaction>> responseAddTransaction = sendRequest(service.addTransaction(transaction));
//        assertEquals(responseAddTransaction.body().getStatus(), Status.SUCCESS, responseAddTransaction.body().getMessage());
//
//        //Checking that transaction is added
//        HashMap<String, String> requestMap = new HashMap<>();
//
//        requestMap.put("login", login);
//        requestMap.put("rangeFrom", formatDateToString(now()));
//        requestMap.put("rangeTo", formatDateToString(formatDateFromString(generateDatePlus(ChronoUnit.DAYS, 2))));
//
//        Response<AjaxRs<List<Transaction>>> responseGetTransactions = sendRequest(service.getTransactions(requestMap));
//
//        assertEquals(responseGetTransactions.body().getPayload().get(responseGetTransactions.body().getPayload().size() - 1).get_id(), transaction.get_id(), "Transactions are not equal!");
//    }

//    @Test
//    public void updateTransaction() throws IOException {
//        Person person = personGenerator();
//
//        //Registering Person
//        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
//        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());
//
//        Transaction transaction = Generator.generateTransaction();
//
//        // Adding transaction
//        Response<AjaxRs<List<Transaction>>> responseAddTransaction = sendRequest(service.addTransaction(transaction));
//        assertEquals(responseAddTransaction.body().getStatus(), Status.SUCCESS, responseAddTransaction.body().getMessage());
//
//        Transaction newTransaction = Transaction.builder()
//                .currency(transaction.getCurrency())
//                .description(transaction.getDescription())
//                .sum(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))
//                .build();
//
//        //Updating transaction
//        Response<AjaxRs<List<Transaction>>> responseUpdateTransaction = sendRequest(service.updateTransaction(newTransaction));
//        assertEquals(responseUpdateTransaction.body().getStatus(), Status.SUCCESS, responseUpdateTransaction.body().getMessage());
//        assertNotEquals(responseUpdateTransaction.body().getPayload().get(responseUpdateTransaction.body().getPayload().size() - 1).getSum(), transaction.getSum(), "Transaction wasn't updated!");
//
//        // TODO: 26.01.2018 Add checks for Summary Statistics!!!
//    }

    // TODO: 26.01.2018 Add TEST for deleteTransaction

}