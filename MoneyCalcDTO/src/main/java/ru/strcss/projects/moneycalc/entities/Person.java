package ru.strcss.projects.moneycalc.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class Person implements Serializable {

    private int id;

    private int accessId;

    private int identificationsId;

    private int settingsId;
}