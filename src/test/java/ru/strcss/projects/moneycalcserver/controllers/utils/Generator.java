package ru.strcss.projects.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalcserver.enitities.dto.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Generator {
    public static Person personGenerator() {
        return Person.builder()
                .access(Access.builder()
                        .login(UUID())
                        .password("qwerty")
                        .email(UUID() + "@mail.ru")
                        .build())
//                .iD(UUID())
                .personalIdentifications(PersonalIdentifications.builder()
                        .name("Вася")
                        .build())
                .personalSettings(PersonalSettings.builder()
                        .periodFrom(createDate())
                        .periodTo(generateDatePlus(ChronoUnit.MONTHS, 1))
                        .sections(Arrays.asList(SettingsSection.builder().name("Авто").build(),
                                SettingsSection.builder().name("Семья").build()))
                        .build())
                .financeStatistics(new FinanceStatistics(generateTransactions(5)))
                .build();
    }

    public static List<Transaction> generateTransactions(int count){
        return Stream.generate(Generator::generateTransaction)
                .limit(count)
                .collect(Collectors.toList());
    }

    static Transaction generateTransaction(){
        return Transaction.builder()
                .date(createDate())
                .sum(ThreadLocalRandom.current().nextInt(10,2000))
                .currency("RUR")
                .description("5ка")
                .sectionID("0")
                .build();
    }

    public static String UUID() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    public static String generateDatePlus(TemporalUnit unit, int count) {
        LocalDate now = LocalDate.now().plus(count,unit);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }

    public static String createDate(){
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }
}
