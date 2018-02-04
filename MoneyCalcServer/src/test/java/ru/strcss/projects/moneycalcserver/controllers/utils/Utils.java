package ru.strcss.projects.moneycalcserver.controllers.utils;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.AjaxRs;

import java.io.IOException;

@Slf4j
public class Utils {
    public static <T> Response<AjaxRs<T>> sendRequest(Call<AjaxRs<T>> call) throws IOException {

        Response<AjaxRs<T>> response = call.execute();

        Assert.assertTrue(response != null, "Response is null!");
        Assert.assertTrue(response.body() != null, "Response body is null!");
        log.debug("{} - {}", response.body().getMessage(), response.body().getStatus().name());
        return response;
    }



}
