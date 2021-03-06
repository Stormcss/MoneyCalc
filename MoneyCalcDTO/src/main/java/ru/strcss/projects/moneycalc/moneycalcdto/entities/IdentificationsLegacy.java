package ru.strcss.projects.moneycalc.moneycalcdto.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentificationsLegacy implements Serializable {

    private int id;

    private String name;
}
