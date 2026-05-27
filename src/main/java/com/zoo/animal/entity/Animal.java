package com.zoo.animal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "animals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    private Long id;

    @Column(name = "parent_male_id")
    private Long parentMaleId;

    @Column(name = "parent_female_id")
    private Long parentFemaleId;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "date_of_admission", nullable = false)
    private LocalDate dateOfAdmission;

    @Column(name = "date_departure")
    private LocalDate dateDeparture;

    @Column(name = "date_death")
    private LocalDate dateDeath;

    @Column(name = "species_id", nullable = false)
    private Integer speciesId;

    @Column(name = "is_male", nullable = false)
    private Boolean isMale;

    @Transient
    private Species species;
}