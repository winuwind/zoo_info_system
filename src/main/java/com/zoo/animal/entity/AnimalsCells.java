package com.zoo.animal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "animals_cells")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AnimalsCells.AnimalsCellsId.class)
public class AnimalsCells {

    @Id
    @Column(name = "animal_id")
    private Long animalId;

    @Id
    @Column(name = "cell_id")
    private Integer cellId;

    @Id
    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnimalsCellsId implements Serializable {
        private Long animalId;
        private Integer cellId;
        private LocalDate dateStart;
    }
}