package com.zoo.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFilterRequest {
    private Integer employeeTypeId;
    private Integer workExperienceMin;
    private Integer workExperienceMax;
    private Boolean isMale;
    private Integer ageMin;
    private Integer ageMax;
    private Integer salaryMin;
    private Integer salaryMax;
}