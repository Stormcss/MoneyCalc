package ru.strcss.projects.moneycalcmigrator.dto;

public enum MigrationType {

    /**
     * Migrate from previous standalone version of MoneyCalc to current version
     * Requires data files from previous version
     */
    OLD_MONEYCALC_TO_NEW,

    /**
     * Migrate data from DB to Serialized file
     */
    DB_TO_FILE_BACKUP,

    /**
     * Load data from serialized file to DB
     * Requires serialized data files
     */
    FILE_BACKUP_TO_DB
}
