package com.zoo.animal.controller;

import com.zoo.animal.dto.AnimalFilterRequest;
import com.zoo.animal.dto.AnimalListResponse;
import com.zoo.animal.dto.AnimalResponse;
import com.zoo.animal.entity.Disease;
import com.zoo.animal.entity.Species;
import com.zoo.animal.entity.Vaccination;
import com.zoo.animal.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @PostMapping("/list")
    public ResponseEntity<AnimalListResponse> getAnimalsList(@RequestBody(required = false) AnimalFilterRequest filter) {
        List<AnimalResponse> animals = animalService.getAllAnimals(filter);
        return ResponseEntity.ok(AnimalListResponse.builder()
                .animals(animals)
                .totalCount((long) animals.size())
                .page(1)
                .pageSize(animals.size())
                .build());
    }

    @GetMapping("/species-count")
    public ResponseEntity<Map<String, Long>> getSpeciesCount() {
        return ResponseEntity.ok(animalService.getSpeciesCount());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> getAnimalById(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.getAnimalById(id));
    }

    @GetMapping("/species")
    public ResponseEntity<List<Species>> getAllSpecies() {
        return ResponseEntity.ok(animalService.getAllSpecies());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/vaccinations")
    public ResponseEntity<List<Vaccination>> getAllVaccinations() {
        return ResponseEntity.ok(animalService.getAllVaccinations());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/diseases")
    public ResponseEntity<List<Disease>> getAllDiseases() {
        return ResponseEntity.ok(animalService.getAllDiseases());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/compatible/{speciesId}")
    public ResponseEntity<List<Species>> getCompatibleSpecies(@PathVariable Integer speciesId) {
        return ResponseEntity.ok(animalService.getCompatibleSpecies(speciesId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/breeding")
    public ResponseEntity<List<AnimalResponse>> getAnimalsForBreeding() {
        return ResponseEntity.ok(animalService.getAnimalsForBreeding());
    }

    @GetMapping("/simple-list")
    public ResponseEntity<List<Map<String, Object>>> getSimpleAnimalList() {
        return ResponseEntity.ok(animalService.getSimpleAnimalList());
    }

}