package ru.strcss.projects.moneycalc.integration.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.Access;
import ru.strcss.projects.moneycalc.entities.Person;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.entities.Transaction;
import ru.strcss.projects.moneycalc.integration.testapi.MoneyCalcClient;
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

    public static <T> Response<MoneyCalcRs<T>> sendRequest(Call<MoneyCalcRs<T>> call) {
        return sendRequest(call, null);
    }

    public static <T> Response<MoneyCalcRs<T>> sendRequest(Call<MoneyCalcRs<T>> call, Status expectedStatus) {
        Response<MoneyCalcRs<T>> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException("Can not send Request!", e);
        }

        assertNotNull(response, "Response is null!");

        if (response.body() == null /*&& expectedStatus != null && expectedStatus.equals(Status.ERROR)*/) {
            String errorBodyMessage = getErrorBodyMessage(response);
            log.debug("{} - {}", errorBodyMessage, response.code());
            if (expectedStatus != null && expectedStatus.equals(Status.SUCCESS))
                // TODO: 30.05.2018 add storing http code in Status object
                assertEquals(response.code(), 200, errorBodyMessage);
//                assertEquals(response.code(), 200, "Response code is not 200!");
        } else {
            assertNotNull(response.body(), "Response body is null!");
            if (expectedStatus != null)
                assertEquals(response.body().getServerStatus(), expectedStatus, response.body().getMessage());
            log.debug("{} - {}", response.body().getMessage(), response.body().getServerStatus().name());
        }
        return response;
    }

    public static String getErrorBodyMessage(Response response) {
        try {
            String errorJSON = response.errorBody().string();
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
    public static Pair<IdsContainer, String> savePersonGetIdsAndToken(MoneyCalcClient service) {
        Credentials credentials = generateCredentials(Generator.UUID());
        Person registerRs = sendRequest(service.registerPerson(credentials), Status.SUCCESS).body().getPayload();

        IdsContainer idsContainer = IdsContainer.builder()
                .personId(registerRs.getId())
                .accessId(registerRs.getAccessId())
                .settingsId(registerRs.getSettingsId())
                .identificationsId(registerRs.getIdentificationsId())
                .build();

        return new Pair<>(idsContainer, getToken(service, credentials.getAccess()));
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
     * Add SpendingSection and return it's Id
     *
     * @param spendingSection - added SpendingSection
     * @return added Spending Section Id
     */
    public static Integer addSpendingSectionGetSectionId(MoneyCalcClient service, String token, SpendingSection spendingSection) {
        MoneyCalcRs<List<SpendingSection>> addSectionRs =
                sendRequest(service.addSpendingSection(token, spendingSection), Status.SUCCESS).body();

        return addSectionRs.getPayload().stream().filter(section -> section.getName().equals(spendingSection.getName()))
                .findAny()
                .get().getSectionId();
    }

    /**
     * Add SpendingSection and return Rs from the server
     *
     * @param spendingSection - added SpendingSection
     * @return income Rs object
     */
    public static MoneyCalcRs<List<SpendingSection>> addSpendingSectionGetRs(MoneyCalcClient service, String token, SpendingSection spendingSection) {
        return sendRequest(service.addSpendingSection(token, spendingSection), Status.SUCCESS).body();
    }

    /**
     * Delete SpendingSection and return Rs from the server
     *
     * @param token - token after login
     * @param id    - deleted Transaction id
     * @return income Rs object
     */
    public static MoneyCalcRs<List<SpendingSection>> deleteSpendingSectionByIdGetRs(MoneyCalcClient service, String token, Integer id) {
        return sendRequest(service.deleteSpendingSection(token, id), Status.SUCCESS).body();
    }

    /**
     * Add Transaction and return Rs from the server
     *
     * @param token       - token after login
     * @param transaction - added Transaction object
     * @return income Rs object
     */
    public static MoneyCalcRs<Transaction> addTransaction(MoneyCalcClient service, String token, Transaction transaction) {
        return sendRequest(service.addTransaction(token, transaction), Status.SUCCESS).body();
    }

    /**
     * Get Transactions and return Rs from the server
     */
    public static MoneyCalcRs<List<Transaction>> getTransactions(MoneyCalcClient service, String token, TransactionsSearchFilter
            searchContainer) {
        return sendRequest(service.getTransactions(token, searchContainer), Status.SUCCESS).body();
    }

    public static MoneyCalcRs<List<Transaction>> getTransactions(MoneyCalcClient service, String token, LocalDate dateFrom,
                                                                 LocalDate dateTo, List<Integer> sectionIds) {
        TransactionsSearchFilter container = new TransactionsSearchFilter();
        container.setDateFrom(dateFrom);
        container.setDateTo(dateTo);
        container.setRequiredSections(sectionIds);
        return sendRequest(service.getTransactions(token, container), Status.SUCCESS).body();
    }
}
