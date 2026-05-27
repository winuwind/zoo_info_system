package com.zoo.animal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "species")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Species {

    @Id
    private Integer id;

    @Column(name = "is_predator", nullable = false)
    private Boolean isPredator;

    @Column(name = "need_to_warm", nullable = false)
    private Boolean needToWarm;

    @Column(nullable = false, unique = true)
    private String title;
}