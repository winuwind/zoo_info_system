package com.zoo.animal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "animal_diseases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AnimalDisease.AnimalDiseaseId.class)
public class AnimalDisease {

    @Id
    @Column(name = "animal_id")
    private Long animalId;

    @Id
    @Column(name = "disease_id")
    private Integer diseaseId;

    @Id
    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "vet_id")
    private Long vetId;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Column(name = "is_isolated")
    private Boolean isIsolated;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnimalDiseaseId implements Serializable {
        private Long animalId;
        private Integer diseaseId;
        private LocalDate dateStart;
    }
}