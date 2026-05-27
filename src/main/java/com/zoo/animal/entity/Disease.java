package com.zoo.animal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diseases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    @Id
    private Integer id;

    private String description;
    private String symptoms;
}