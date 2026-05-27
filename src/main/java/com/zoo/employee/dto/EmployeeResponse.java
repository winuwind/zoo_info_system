package com.zoo.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String surname;
    private String name;
    private String employeeType;
    private LocalDate hireDate;
    private LocalDate dateOfDismissal;
    private Integer salary;
    private Boolean isMale;
    private Integer age;
    private Integer workExperience;
}