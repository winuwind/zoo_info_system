package com.zoo.employee.service;

import com.zoo.employee.dto.EmployeeFilterRequest;
import com.zoo.employee.dto.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final JdbcTemplate jdbcTemplate;

    public List<EmployeeResponse> getAllEmployees(EmployeeFilterRequest filter) {
        StringBuilder sql = new StringBuilder("""
            SELECT e.id, e.surname, e.name, et.description as employee_type,
                   e.hire_date, e.date_of_dismissal, e.salary, e.is_male,
                   e.date_of_birth
            FROM employees e
            JOIN employee_types et ON e.type = et.id
            WHERE e.date_of_dismissal IS NULL
        """);

        List<Object> params = new ArrayList<>();

        if (filter != null) {
            if (filter.getEmployeeTypeId() != null) {
                sql.append(" AND e.type = ?");
                params.add(filter.getEmployeeTypeId());
            }
            if (filter.getIsMale() != null) {
                sql.append(" AND e.is_male = ?");
                params.add(filter.getIsMale());
            }
            if (filter.getSalaryMin() != null) {
                sql.append(" AND e.salary >= ?");
                params.add(filter.getSalaryMin());
            }
            if (filter.getSalaryMax() != null) {
                sql.append(" AND e.salary <= ?");
                params.add(filter.getSalaryMax());
            }
        }

        sql.append(" ORDER BY e.hire_date");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        List<EmployeeResponse> employees = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            LocalDate birthDate = null;
            LocalDate hireDate = null;

            Object birthDateObj = row.get("date_of_birth");
            if (birthDateObj instanceof java.sql.Date) {
                birthDate = ((java.sql.Date) birthDateObj).toLocalDate();
            } else if (birthDateObj instanceof LocalDate) {
                birthDate = (LocalDate) birthDateObj;
            }

            Object hireDateObj = row.get("hire_date");
            if (hireDateObj instanceof java.sql.Date) {
                hireDate = ((java.sql.Date) hireDateObj).toLocalDate();
            } else if (hireDateObj instanceof LocalDate) {
                hireDate = (LocalDate) hireDateObj;
            }

            int age = birthDate != null ? Period.between(birthDate, LocalDate.now()).getYears() : 0;
            int workExperience = hireDate != null ? Period.between(hireDate, LocalDate.now()).getYears() : 0;

            if (filter != null) {
                if (filter.getAgeMin() != null && age < filter.getAgeMin()) continue;
                if (filter.getAgeMax() != null && age > filter.getAgeMax()) continue;
                if (filter.getWorkExperienceMin() != null && workExperience < filter.getWorkExperienceMin()) continue;
                if (filter.getWorkExperienceMax() != null && workExperience > filter.getWorkExperienceMax()) continue;
            }

            LocalDate dateOfDismissal = null;

            Object dateOfDismissalObj = row.get("date_of_dismissal");
            if (dateOfDismissalObj instanceof java.sql.Date) {
                dateOfDismissal = ((java.sql.Date) dateOfDismissalObj).toLocalDate();
            } else if (birthDateObj instanceof LocalDate) {
                dateOfDismissal = (LocalDate) dateOfDismissalObj;
            }

            employees.add(EmployeeResponse.builder()
                    .id((Long) row.get("id"))
                    .surname((String) row.get("surname"))
                    .name((String) row.get("name"))
                    .employeeType((String) row.get("employee_type"))
                    .hireDate(hireDate)
                    .dateOfDismissal(dateOfDismissal)
                    .salary((Integer) row.get("salary"))
                    .isMale((Boolean) row.get("is_male"))
                    .age(age)
                    .workExperience(workExperience)
                    .build());
        }

        return employees;
    }

    public List<Map<String, Object>> getEmployeeTypes() {
        String sql = "SELECT id, description FROM employee_types ORDER BY id";
        return jdbcTemplate.queryForList(sql);
    }

    public List<EmployeeResponse> getEmployeesForAnimal(Long animalId, String dateStart, String dateEnd) {
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT e.id, e.surname, e.name, et.description as employee_type,
                   e.hire_date, e.date_of_dismissal, e.salary, e.is_male,
                   e.date_of_birth
            FROM employees e
            JOIN employee_types et ON e.type = et.id
            LEFT JOIN trainer t ON e.id = t.id
            LEFT JOIN train_animals ta ON t.id = ta.trainer_id
            LEFT JOIN vets v ON e.id = v.id
            LEFT JOIN inspections i ON v.id = i.vet_id
            WHERE (ta.animal_id = ? OR i.animal_id = ?)
        """);

        List<Object> params = new ArrayList<>();
        params.add(animalId);
        params.add(animalId);

        if (dateStart != null && dateEnd != null) {
            sql.append(" AND ((ta.date_start BETWEEN ? AND ?) OR (ta.date_end BETWEEN ? AND ?) OR (i.date BETWEEN ? AND ?))");
            params.add(dateStart);
            params.add(dateEnd);
            params.add(dateStart);
            params.add(dateEnd);
            params.add(dateStart);
            params.add(dateEnd);
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return mapToEmployeeResponses(rows);
    }

    public List<EmployeeResponse> getEmployeesWithAccessToAnimal(Long animalId) {
        String sql = """
            SELECT DISTINCT e.id, e.surname, e.name, et.description as employee_type,
                   e.hire_date, e.date_of_dismissal, e.salary, e.is_male,
                   e.date_of_birth
            FROM employees e
            JOIN employee_types et ON e.type = et.id
            LEFT JOIN vets v ON e.id = v.id
            LEFT JOIN inspections i ON v.id = i.vet_id AND i.animal_id = ? AND i.date >= CURRENT_DATE - INTERVAL '1 year'
            LEFT JOIN trainer t ON e.id = t.id
            LEFT JOIN train_animals ta ON t.id = ta.trainer_id AND ta.animal_id = ? AND (ta.date_end IS NULL OR ta.date_end > CURRENT_DATE)
            LEFT JOIN janitor j ON e.id = j.id
            LEFT JOIN janitor_cells jc ON j.id = jc.janitor_id
            LEFT JOIN animals_cells ac ON jc.cell_id = ac.cell_id AND ac.animal_id = ? AND (ac.date_end IS NULL OR ac.date_end > CURRENT_DATE)
            WHERE i.animal_id IS NOT NULL OR ta.animal_id IS NOT NULL OR ac.animal_id IS NOT NULL
        """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, animalId, animalId, animalId);
        return mapToEmployeeResponses(rows);
    }

    private List<EmployeeResponse> mapToEmployeeResponses(List<Map<String, Object>> rows) {
        List<EmployeeResponse> employees = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            LocalDate birthDate = null;
            LocalDate hireDate = null;

            Object birthDateObj = row.get("date_of_birth");
            if (birthDateObj instanceof java.sql.Date) {
                birthDate = ((java.sql.Date) birthDateObj).toLocalDate();
            } else if (birthDateObj instanceof LocalDate) {
                birthDate = (LocalDate) birthDateObj;
            }

            Object hireDateObj = row.get("hire_date");
            if (hireDateObj instanceof java.sql.Date) {
                hireDate = ((java.sql.Date) hireDateObj).toLocalDate();
            } else if (hireDateObj instanceof LocalDate) {
                hireDate = (LocalDate) hireDateObj;
            }

            LocalDate dateOfDismissal = null;

            Object dateOfDismissalObj = row.get("date_of_dismissal");
            if (dateOfDismissalObj instanceof java.sql.Date) {
                dateOfDismissal = ((java.sql.Date) dateOfDismissalObj).toLocalDate();
            } else if (birthDateObj instanceof LocalDate) {
                dateOfDismissal = (LocalDate) dateOfDismissalObj;
            }

            employees.add(EmployeeResponse.builder()
                    .id((Long) row.get("id"))
                    .surname((String) row.get("surname"))
                    .name((String) row.get("name"))
                    .employeeType((String) row.get("employee_type"))
                    .hireDate(hireDate)
                    .dateOfDismissal(dateOfDismissal)
                    .salary((Integer) row.get("salary"))
                    .isMale((Boolean) row.get("is_male"))
                    .age(birthDate != null ? Period.between(birthDate, LocalDate.now()).getYears() : 0)
                    .workExperience(hireDate != null ? Period.between(hireDate, LocalDate.now()).getYears() : 0)
                    .build());
        }
        return employees;
    }
}