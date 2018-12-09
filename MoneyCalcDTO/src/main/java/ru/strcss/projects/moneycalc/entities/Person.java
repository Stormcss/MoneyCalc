package ru.strcss.projects.moneycalc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {
    private Long id;
    private Long accessId;
    private Long identificationsId;
    private Long settingsId;
}