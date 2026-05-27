package com.zoo.animal.repository;

import com.zoo.animal.entity.Inspection;
import com.zoo.animal.entity.Inspection.InspectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, InspectionId> {

    @Query("SELECT i FROM Inspection i WHERE i.animalId = :animalId ORDER BY i.date DESC")
    List<Inspection> findByAnimalIdOrderByDateDesc(@Param("animalId") Long animalId);

    @Query("SELECT i FROM Inspection i WHERE i.animalId = :animalId AND i.date = " +
            "(SELECT MAX(i2.date) FROM Inspection i2 WHERE i2.animalId = :animalId)")
    Optional<Inspection> findLatestInspectionByAnimalId(@Param("animalId") Long animalId);

    @Query("SELECT i.animalId, i.weight, i.height FROM Inspection i WHERE i.date = " +
            "(SELECT MAX(i2.date) FROM Inspection i2 WHERE i2.animalId = i.animalId)")
    List<Object[]> findAllLatestWeightsAndHeights();
}