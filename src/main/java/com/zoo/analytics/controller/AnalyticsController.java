package com.zoo.analytics.controller;

import com.zoo.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dealer-foods")
    public ResponseEntity<List<Map<String, Object>>> getDealerFoods(
            @RequestParam(required = false) Integer foodId,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd,
            @RequestParam(required = false) Integer countMin,
            @RequestParam(required = false) Integer countMax,
            @RequestParam(required = false) Integer priceMin,
            @RequestParam(required = false) Integer priceMax) {
        return ResponseEntity.ok(analyticsService.getDealerFoods(foodId, dateStart, dateEnd,
                countMin, countMax, priceMin, priceMax));
    }

    @GetMapping("/zoo-produced-foods")
    public ResponseEntity<List<Map<String, Object>>> getZooProducedFoods() {
        return ResponseEntity.ok(analyticsService.getZooProducedFoods());
    }

    @GetMapping("/animals-by-food-type")
    public ResponseEntity<List<Map<String, Object>>> getAnimalsByFoodType(
            @RequestParam Integer foodTypeId,
            @RequestParam(required = false) Integer seasonId,
            @RequestParam(required = false) Integer ageMin) {
        return ResponseEntity.ok(analyticsService.getAnimalsByFoodType(foodTypeId, seasonId, ageMin));
    }

    @GetMapping("/exchange-zoos")
    public ResponseEntity<List<Map<String, Object>>> getExchangeZoos(
            @RequestParam(required = false) Integer speciesId) {
        return ResponseEntity.ok(analyticsService.getExchangeZoos(speciesId));
    }

    @GetMapping("/all-foods")
    public ResponseEntity<List<Map<String, Object>>> getFoods() {
        return ResponseEntity.ok(analyticsService.getAllFoods());
    }

    @GetMapping("/food-types")
    public ResponseEntity<List<Map<String, Object>>> getFoodTypes() {
        return ResponseEntity.ok(analyticsService.getFoodTypes());
    }
}