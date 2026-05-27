package com.zoo.animal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vaccinations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vaccination {

    @Id
    private Integer id;

    @Column(name = "desease_id")
    private Integer diseaseId;

    private String description;
}