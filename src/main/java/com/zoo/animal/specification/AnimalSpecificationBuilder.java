package com.zoo.animal.specification;

import com.zoo.animal.dto.AnimalFilterRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AnimalSpecificationBuilder {

    public Specification<Object[]> buildSpecification(AnimalFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join with species - предполагаем, что у нас есть join с таблицей species
            // В реальном коде нужно использовать правильные entity классы

            // Species filter
            if (filter.getSpeciesId() != null) {
                predicates.add(cb.equal(root.get("speciesId"), filter.getSpeciesId()));
            }

            // Gender filter
            if (filter.getIsMale() != null) {
                predicates.add(cb.equal(root.get("isMale"), filter.getIsMale()));
            }

            // Age filter
            if (filter.getAgeMin() != null || filter.getAgeMax() != null) {
                Expression<Integer> ageExpr = cb.function(
                        "EXTRACT", Integer.class,
                        cb.literal("YEAR"),
                        cb.function("AGE", LocalDate.class, cb.currentDate(), root.get("birthDate"))
                );

                if (filter.getAgeMin() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(ageExpr, filter.getAgeMin()));
                }
                if (filter.getAgeMax() != null) {
                    predicates.add(cb.lessThanOrEqualTo(ageExpr, filter.getAgeMax()));
                }
            }

            // Offspring filter - упрощенная версия без подзапросов
            if (filter.getHasOffspring() != null || filter.getOffspringCountMin() != null || filter.getOffspringCountMax() != null) {
                // Для простоты используем native query или обрабатываем в сервисе
                // Вместо сложных подзапросов в Specification
            }

            // Need to warm filter - требует join с species
            if (filter.getNeedToWarm() != null) {
                // Добавляем join с species если нужно
                // predicates.add(cb.equal(speciesJoin.get("needToWarm"), filter.getNeedToWarm()));
            }

            // Live/Dead filter (Admin)
            if (filter.getIsAlive() != null) {
                if (filter.getIsAlive()) {
                    predicates.add(cb.isNull(root.get("dateDeath")));
                } else {
                    predicates.add(cb.isNotNull(root.get("dateDeath")));
                }
            }

            // In zoo filter (Admin)
            if (filter.getIsInZoo() != null) {
                if (filter.getIsInZoo()) {
                    predicates.add(cb.isNull(root.get("dateDeparture")));
                    predicates.add(cb.isNull(root.get("dateDeath")));
                } else {
                    predicates.add(cb.or(
                            cb.isNotNull(root.get("dateDeparture")),
                            cb.isNotNull(root.get("dateDeath"))
                    ));
                }
            }

            // Zoo experience filter (Admin)
            if (filter.getZooExperienceMin() != null || filter.getZooExperienceMax() != null) {
                Expression<Integer> experienceExpr = cb.function(
                        "EXTRACT", Integer.class,
                        cb.literal("YEAR"),
                        cb.function("AGE", LocalDate.class, cb.currentDate(), root.get("dateOfAdmission"))
                );

                if (filter.getZooExperienceMin() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(experienceExpr, filter.getZooExperienceMin()));
                }
                if (filter.getZooExperienceMax() != null) {
                    predicates.add(cb.lessThanOrEqualTo(experienceExpr, filter.getZooExperienceMax()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}