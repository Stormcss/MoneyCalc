package ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Created by Stormcss
 * Date: 31.03.2019
 */
@Getter
@RequiredArgsConstructor
public class RequestFailedException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;
}
