package com.zoo.animal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalFilterRequest {
    // Common filters (User level)
    private Integer speciesId;
    private Integer ageMin;
    private Integer ageMax;
    private Boolean isMale;
    private Boolean hasOffspring;
    private Integer offspringCountMin;
    private Integer offspringCountMax;
    private Double weightMin;
    private Double weightMax;
    private Double heightMin;
    private Double heightMax;
    private Long livedWithAnimalId;
    private Boolean needToWarm;

    // Admin filters
    private List<Integer> vaccinationIds;
    private List<Integer> diseaseIds;
    private Integer zooExperienceMin;
    private Integer zooExperienceMax;
    private Boolean isAlive;
    private Boolean isInZoo;
    private Integer cellNumber;

    private Integer compatibilitySpeciesId;
}