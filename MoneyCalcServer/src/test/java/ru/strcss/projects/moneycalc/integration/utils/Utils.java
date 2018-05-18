package ru.strcss.projects.moneycalc.integration.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.integration.testapi.MoneyCalcClient;
import ru.strcss.projects.moneycalc.testutils.Generator;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateCredentials;

@Slf4j
public class Utils {
    private final static String messageRegex = "\"message\":\"(.*?)\"";
    private final static Pattern messageGetterPattern = Pattern.compile(messageRegex);

    public static <T> Response<MoneyCalcRs<T>> sendRequest(Call<MoneyCalcRs<T>> call) {
        return sendRequest(call, null);
    }

    public static <T> Response<MoneyCalcRs<T>> sendRequest(Call<MoneyCalcRs<T>> call, Status expectedStatus) {
        Response<MoneyCalcRs<T>> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException("Can not send Request!", e);
        }

        assertNotNull(response, "Response is null!");

        if (response.body() == null /*&& expectedStatus != null && expectedStatus.equals(Status.ERROR)*/) {
            String errorBodyMessage = getErrorBodyMessage(response);
            log.debug("{} - {}", errorBodyMessage, response.code());
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
     * @param service - Retrofit configured service
     * @return token for created and logged in person
     */
    public static String savePersonGetToken(MoneyCalcClient service) {
        Credentials credentials = generateCredentials(Generator.UUID());
        sendRequest(service.registerPerson(credentials), Status.SUCCESS).body();

        return getToken(service, credentials.getAccess());
    }

    /**
     * Save Person with random login and return login with token
     *
     * @param service - Retrofit configured service
     * @return login and token for created and logged in person
     */
    public static Pair<String, String> savePersonGetLoginAndToken(MoneyCalcClient service) {
        String login = Generator.UUID();
        Credentials credentials = generateCredentials(login);
        sendRequest(service.registerPerson(credentials), Status.SUCCESS).body();

        return new Pair<>(login, getToken(service, credentials.getAccess()));
    }

    /**
     * Save Person with random login and return credentials with token
     *
     * @param service - Retrofit configured service
     * @return Credentials and token for created and logged in person
     */
    public static Pair<Credentials, String> savePersonGetCredentialsAndToken(MoneyCalcClient service) {
        Credentials credentials = generateCredentials(Generator.UUID());
        sendRequest(service.registerPerson(credentials), Status.SUCCESS).body();

        return new Pair<>(credentials, getToken(service, credentials.getAccess()));
    }

    public static String getToken(MoneyCalcClient service, Access access){
        Headers headers;
        try {
            headers = service.login(access).execute().headers();
        } catch (IOException e) {
            throw new RuntimeException("Can not get Token!", e);
        }
        return headers.get("Authorization");
    }
}
