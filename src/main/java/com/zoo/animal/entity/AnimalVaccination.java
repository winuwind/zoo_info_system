package com.zoo.animal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "animal_vaccinations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AnimalVaccination.AnimalVaccinationId.class)
public class AnimalVaccination {

    @Id
    @Column(name = "animal_id")
    private Long animalId;

    @Id
    @Column(name = "vaccination_id")
    private Integer vaccinationId;

    @Column(name = "vet_id")
    private Long vetId;

    @Column(name = "date_vaccination")
    private LocalDate dateVaccination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnimalVaccinationId implements Serializable {
        private Long animalId;
        private Integer vaccinationId;
    }
}