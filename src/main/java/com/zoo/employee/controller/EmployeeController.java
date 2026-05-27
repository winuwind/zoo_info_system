package com.zoo.employee.controller;

import com.zoo.employee.dto.EmployeeFilterRequest;
import com.zoo.employee.dto.EmployeeResponse;
import com.zoo.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/list")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(@RequestBody(required = false) EmployeeFilterRequest filter) {
        return ResponseEntity.ok(employeeService.getAllEmployees(filter));
    }

    @GetMapping("/types")
    public ResponseEntity<List<Map<String, Object>>> getEmployeeTypes() {
        return ResponseEntity.ok(employeeService.getEmployeeTypes());
    }

    @GetMapping("/by-animal/{animalId}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesForAnimal(
            @PathVariable Long animalId,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd) {
        return ResponseEntity.ok(employeeService.getEmployeesForAnimal(animalId, dateStart, dateEnd));
    }

    @GetMapping("/access-to-animal/{animalId}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesWithAccessToAnimal(@PathVariable Long animalId) {
        return ResponseEntity.ok(employeeService.getEmployeesWithAccessToAnimal(animalId));
    }
}