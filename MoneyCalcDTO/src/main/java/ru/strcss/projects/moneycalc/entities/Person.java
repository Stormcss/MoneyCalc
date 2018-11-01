package ru.strcss.projects.moneycalc.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@Entity
@Table(name = "\"Person\"")
public class Person implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "\"accessId\"")
//    @OneToOne(cascade = CascadeType.REMOVE)
    private int accessId;

    @Column(name = "\"identificationsId\"")
//    @OneToOne(cascade = CascadeType.REMOVE)
    private int identificationsId;

    @Column(name = "\"settingsId\"")
//    @OneToOne(cascade = CascadeType.REMOVE)
    private int settingsId;
}