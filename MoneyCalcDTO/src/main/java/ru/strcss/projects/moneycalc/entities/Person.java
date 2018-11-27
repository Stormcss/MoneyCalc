package ru.strcss.projects.moneycalc.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class Person implements Serializable {

    private Long id;

    private Long accessId;

    private Long identificationsId;

    private Long settingsId;
}