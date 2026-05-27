package com.zoo.animal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "inspections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Inspection.InspectionId.class)
public class Inspection {

    @Id
    @Column(name = "vet_id")
    private Long vetId;

    @Id
    @Column(name = "animal_id")
    private Long animalId;

    @Id
    private LocalDate date;

    private Short weight;
    private Short height;

    @Column(name = "is_sick")
    private Boolean isSick;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspectionId implements Serializable {
        private Long vetId;
        private Long animalId;
        private LocalDate date;
    }
}