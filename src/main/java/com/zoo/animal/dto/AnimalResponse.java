package com.zoo.animal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalResponse {
    private Long id;
    private String species;
    private Integer speciesId;
    private Boolean isMale;
    private Integer age;
    private LocalDate birthDate;
    private LocalDate dateOfAdmission;
    private LocalDate dateDeparture;
    private LocalDate dateDeath;
    private Double weight;
    private Double height;
    private Integer offspringCount;
    private List<VaccinationInfo> vaccinations;
    private List<DiseaseInfo> diseases;
    private Integer zooExperience;
    private Boolean needToWarm;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VaccinationInfo {
        private Integer id;
        private String description;
        private LocalDate date;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiseaseInfo {
        private Integer id;
        private String description;
        private LocalDate dateStart;
        private LocalDate dateEnd;
    }
}