package ru.strcss.projects.moneycalcmigrator.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Stormcss
 * Date: 01.12.2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

    public static <T> Call<T> mockedCall(Response<T> response) {
        return new Call<T>() {
            @Override
            public Response<T> execute() {
                return response;
            }

            @Override
            public void enqueue(Callback<T> callback) {

            }

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<T> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }
}
