package ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions;

/**
 * Created by Stormcss
 * Date: 29.12.2018
 */
public class MoneyCalcServerException extends RuntimeException {
    public MoneyCalcServerException(String message) {
        super(message);
    }

    public MoneyCalcServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
