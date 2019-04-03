package ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Created by Stormcss
 * Date: 31.03.2019
 */
public class IncorrectRequestException extends RequestFailedException {
    public IncorrectRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
