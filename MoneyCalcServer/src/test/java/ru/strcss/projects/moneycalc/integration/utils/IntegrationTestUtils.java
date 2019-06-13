package ru.strcss.projects.moneycalc.integration.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.integration.testapi.MoneyCalcClient;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegrationTestUtils {
    private static final String messageRegex = "\"message\":\"(.*?)\"";
    private static final Pattern messageGetterPattern = Pattern.compile(messageRegex);

    public static <T> Response<T> sendRequest(Call<T> call) {
        return sendRequest(call, null);
    }

    public static <T> Response<T> sendRequest(Call<T> call, HttpStatus expectedStatus) {
        Response<T> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException("Can not send Request!", e);
        }

        assertNotNull(response, "Response is null!");

        if (response.body() == null) {
            String errorBodyMessage = getErrorBodyMessage(response);
            log.debug("{} - {}", errorBodyMessage, response.code());
            if (expectedStatus == null)
                assertEquals(response.code(), HttpStatus.OK.value(), "Response http code is not OK!");
            else
                assertEquals(response.code(), expectedStatus.value(), "Response http code is incorrect!");
        } else {
            assertNotNull(response.body(), "Response body is null!");
            log.debug("Received - {} with HTTP status {}", response.body(), response.code());
        }
        return response;
    }

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
        } catch (IOException e) {
            log.error("Error has occurred while extracting errorBody message", e);
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
        sendRequest(service.registerPerson(credentials), HttpStatus.OK);

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
        sendRequest(service.registerPerson(credentials), HttpStatus.OK);

        return new Pair<>(login, getToken(service, credentials.getAccess()));
    }

    /**
     * Save Person with random login and return container with Entity IDs
     *
     * @param service - Retrofit configured services
     */
    public static Pair<Person, String> savePersonGetIdsAndToken(MoneyCalcClient service) {
        Credentials credentials = generateCredentials(Generator.UUID());
        Person registerRs = sendRequest(service.registerPerson(credentials), HttpStatus.OK).body();

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
        sendRequest(service.registerPerson(credentials), HttpStatus.OK);

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
        SpendingSectionsSearchRs addSectionRs = sendRequest(service.addSpendingSection(token, spendingSection), HttpStatus.OK).body();

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
        return sendRequest(service.addSpendingSection(token, spendingSection), HttpStatus.OK).body();
    }

    /**
     * Delete SpendingSection and return Rs from the server
     *
     * @param token - token after login
     * @param id    - deleted Transaction id
     * @return income Rs object
     */
    public static SpendingSectionsSearchRs deleteSpendingSectionByIdGetRs(MoneyCalcClient service, String token, Integer id) {
        return sendRequest(service.deleteSpendingSection(token, id), HttpStatus.OK).body();
    }

    /**
     * Add Transaction and return Rs from the server
     *
     * @param token       - token after login
     * @param transaction - added Transaction object
     * @return income Rs object
     */
    public static Transaction addTransaction(MoneyCalcClient service, String token, Transaction transaction) {
        return sendRequest(service.addTransaction(token, transaction), HttpStatus.OK).body();
    }

    /**
     * Get Transactions with applied filter
     */
    public static TransactionsSearchRs getTransactions(MoneyCalcClient service, String token, TransactionsSearchFilter
            searchContainer) {
        return sendRequest(service.getTransactions(token, searchContainer), HttpStatus.OK).body();
    }

    /**
     * Get Transactions without filter
     */
    public static TransactionsSearchRs getTransactions(MoneyCalcClient service, String token) {
        return sendRequest(service.getTransactions(token), HttpStatus.OK).body();
    }

    public static TransactionsSearchRs getTransactions(MoneyCalcClient service, String token, LocalDate dateFrom,
                                                       LocalDate dateTo, List<Integer> sectionIds) {
        TransactionsSearchFilter container = new TransactionsSearchFilter();
        container.setDateFrom(dateFrom);
        container.setDateTo(dateTo);
        container.setRequiredSections(sectionIds);
        return sendRequest(service.getTransactions(token, container), HttpStatus.OK).body();
    }

    /**
     * Update settings
     */
    public static Settings updateSettings(MoneyCalcClient service, String token, LocalDate periodFrom, LocalDate periodTo) {
        return sendRequest(service.updateSettings(token, new Settings(periodFrom, periodTo))).body();
    }

}
