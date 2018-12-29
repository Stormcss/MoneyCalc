package ru.strcss.projects.moneycalc.moneycalcmigrator.model.exceptions;

/**
 * Created by Stormcss
 * Date: 29.12.2018
 */
public class MigratorException extends RuntimeException {
    public MigratorException(String description) {
        super(description);
    }

    public MigratorException(String description, Throwable reason) {
        super(description, reason);
    }
}
