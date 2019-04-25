package ru.strcss.projects.moneycalc.integration.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.integration.testapi.MoneyCalcClient;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Status;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.testutils.Generator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateCredentials;

@Slf4j
public class IntegrationTestUtils {
    private final static String messageRegex = "\"message\":\"(.*?)\"";
    private final static Pattern messageGetterPattern = Pattern.compile(messageRegex);

    public static <T> Response<T> sendRequest(Call<T> call) {
        return sendRequest(call, null);
    }

    public static <T> Response<T> sendRequest(Call<T> call, Status expectedStatus) {
        Response<T> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException("Can not send Request!", e);
        }

        assertNotNull(response, "Response is null!");

        if (response.body() == null /*&& expectedStatus != null && expectedStatus.equals(Status.ERROR)*/) {
            String errorBodyMessage = getErrorBodyMessage(response);
            log.debug("{} - {}", errorBodyMessage, response.code());
            if (expectedStatus != null && expectedStatus.equals(Status.SUCCESS)) // TODO: 04.04.2019 remove comparing with Status
                assertEquals(response.code(), 200, "Response http code is incorrect!");
        } else {
            assertNotNull(response.body(), "Response body is null!");
//            if (expectedStatus != null && type.isInstance(MoneyCalcRs.class) )
//                assertEquals(response.body().getServerStatus(), expectedStatus, response.body().getMessage());
            log.debug("Received - {} with HTTP status {}", response.body(), response.code());
        }
        return response;
    }

//    public static <T> Response<MoneyCalcRs<T>> sendRequest(Call<MoneyCalcRs<T>> call, Status expectedStatus) {
//        Response<MoneyCalcRs<T>> response;
//        try {
//            response = call.execute();
//        } catch (IOException e) {
//            throw new RuntimeException("Can not send Request!", e);
//        }
//
//        assertNotNull(response, "Response is null!");
//
//        if (response.body() == null /*&& expectedStatus != null && expectedStatus.equals(Status.ERROR)*/) {
//            String errorBodyMessage = getErrorBodyMessage(response);
//            log.debug("{} - {}", errorBodyMessage, response.code());
//            if (expectedStatus != null && expectedStatus.equals(Status.SUCCESS))
//                // TODO: 30.05.2018 add storing http code in Status object
//                assertEquals(response.code(), 200, errorBodyMessage);
////                assertEquals(response.code(), 200, "Response code is not 200!");
//        } else {
//            assertNotNull(response.body(), "Response body is null!");
//            if (expectedStatus != null)
//                assertEquals(response.body().getServerStatus(), expectedStatus, response.body().getMessage());
//            log.debug("{} - {}", response.body().getMessage(), response.body().getServerStatus().name());
//        }
//        return response;
//    }

    public static String getErrorBodyMessage(Response response) {
        try {
            ResponseBody responseBody = response.errorBody();
            if (responseBody == null)
                return null;
            String errorJSON = responseBody.string();
            final Matcher messageMatcher = messageGetterPattern.matcher(errorJSON);

            if (messageMatcher.find()) {
                return messageMatcher.group(1);
            } else {
                return errorJSON;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            return response.message();
        }
    }

    /**
     * Save Person with random login and return Token
     *
     * @param service - Retrofit configured services
     * @return token for created and logged in person
     */
    public static String savePersonGetToken(MoneyCalcClient service) {
        Credentials credentials = generateCredentials(Generator.UUID());
        sendRequest(service.registerPerson(credentials), Status.SUCCESS);

        return getToken(service, credentials.getAccess());
    }

    /**
     * Save Person with random login and return login with token
     *
     * @param service - Retrofit configured services
     * @return login and token for created and logged in person
     */
    public static Pair<String, String> savePersonGetLoginAndToken(MoneyCalcClient service) {
        String login = Generator.UUID();
        Credentials credentials = generateCredentials(login);
        sendRequest(service.registerPerson(credentials), Status.SUCCESS).body();

        return new Pair<>(login, getToken(service, credentials.getAccess()));
    }

    /**
     * Save Person with random login and return container with Entity IDs
     *
     * @param service - Retrofit configured services
     */
    public static Pair<Person, String> savePersonGetIdsAndToken(MoneyCalcClient service) {
        Credentials credentials = generateCredentials(Generator.UUID());
        Person registerRs = sendRequest(service.registerPerson(credentials), Status.SUCCESS).body().getPayload();

        Person person = new Person(registerRs.getId(), registerRs.getAccessId(), registerRs.getIdentificationsId(), registerRs.getSettingsId());

        return new Pair<>(person, getToken(service, credentials.getAccess()));
    }

    /**
     * Save Person with random login and return credentials with token
     *
     * @param service - Retrofit configured services
     * @return Credentials and token for created and logged in person
     */
    public static Pair<Credentials, String> savePersonGetCredentialsAndToken(MoneyCalcClient service) {
        Credentials credentials = generateCredentials(Generator.UUID());
        sendRequest(service.registerPerson(credentials), Status.SUCCESS).body();

        return new Pair<>(credentials, getToken(service, credentials.getAccess()));
    }

    public static String getToken(MoneyCalcClient service, Access access) {
        Headers headers;
        try {
            headers = service.login(access).execute().headers();
        } catch (IOException e) {
            throw new RuntimeException("Can not get Token!", e);
        }
        return headers.get("Authorization");
    }

    /**
     * Add SpendingSection and return its Id
     *
     * @param spendingSection - added SpendingSection
     * @return added Spending Section Id
     */
    public static Integer addSpendingSectionGetSectionId(MoneyCalcClient service, String token, SpendingSection spendingSection) {
        SpendingSectionsSearchRs addSectionRs = sendRequest(service.addSpendingSection(token, spendingSection), Status.SUCCESS).body();

        // FIXME: 25.04.2019 wrap in Optional
        return addSectionRs.getItems().stream().filter(section -> section.getName().equals(spendingSection.getName()))
                .findAny()
                .map(SpendingSection::getSectionId)
                .orElseThrow(() -> new RuntimeException("Spending Section is not found"));
    }

    /**
     * Add SpendingSection and return Rs from the server
     *
     * @param spendingSection - added SpendingSection
     * @return income Rs object
     */
    public static SpendingSectionsSearchRs addSpendingSectionGetRs(MoneyCalcClient service, String token, SpendingSection spendingSection) {
        return sendRequest(service.addSpendingSection(token, spendingSection), Status.SUCCESS).body();
    }

    /**
     * Delete SpendingSection and return Rs from the server
     *
     * @param token - token after login
     * @param id    - deleted Transaction id
     * @return income Rs object
     */
    public static SpendingSectionsSearchRs deleteSpendingSectionByIdGetRs(MoneyCalcClient service, String token, Integer id) {
        return sendRequest(service.deleteSpendingSection(token, id), Status.SUCCESS).body();
    }

    /**
     * Add Transaction and return Rs from the server
     *
     * @param token       - token after login
     * @param transaction - added Transaction object
     * @return income Rs object
     */
    public static Transaction addTransaction(MoneyCalcClient service, String token, Transaction transaction) {
        return sendRequest(service.addTransaction(token, transaction), Status.SUCCESS).body();
    }

    /**
     * Get Transactions with applied filter
     */
    public static TransactionsSearchRs getTransactions(MoneyCalcClient service, String token, TransactionsSearchFilter
            searchContainer) {
        return sendRequest(service.getTransactions(token, searchContainer), Status.SUCCESS).body();
    }

    /**
     * Get Transactions without filter
     */
    public static TransactionsSearchRs getTransactions(MoneyCalcClient service, String token) {
        return sendRequest(service.getTransactions(token), Status.SUCCESS).body();
    }

    public static TransactionsSearchRs getTransactions(MoneyCalcClient service, String token, LocalDate dateFrom,
                                                       LocalDate dateTo, List<Integer> sectionIds) {
        TransactionsSearchFilter container = new TransactionsSearchFilter();
        container.setDateFrom(dateFrom);
        container.setDateTo(dateTo);
        container.setRequiredSections(sectionIds);
        return sendRequest(service.getTransactions(token, container), Status.SUCCESS).body();
    }
}
