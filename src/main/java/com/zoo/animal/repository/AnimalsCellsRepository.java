package com.zoo.animal.repository;

import com.zoo.animal.entity.Animal;
import com.zoo.animal.entity.AnimalsCells;
import com.zoo.animal.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalsCellsRepository extends JpaRepository<AnimalsCells, Long>, JpaSpecificationExecutor<AnimalsCells> {

    @Query("SELECT ac " +
            "FROM AnimalsCells ac " +
            "WHERE ac.animalId = :animalId " +
            "ORDER BY ac.dateStart ASC")
    List<AnimalsCells> getCellHistory(@Param("animalId") Long animalId);
}