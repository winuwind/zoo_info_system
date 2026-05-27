package com.zoo.animal.repository;

import com.zoo.animal.entity.AnimalDisease;
import com.zoo.animal.entity.AnimalDisease.AnimalDiseaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalDiseaseRepository extends JpaRepository<AnimalDisease, AnimalDiseaseId> {

    @Query("SELECT ad FROM AnimalDisease ad WHERE ad.animalId = :animalId")
    List<AnimalDisease> findByAnimalId(@Param("animalId") Long animalId);

    @Query("SELECT ad.animalId FROM AnimalDisease ad WHERE ad.diseaseId IN :diseaseIds " +
            "GROUP BY ad.animalId HAVING COUNT(DISTINCT ad.diseaseId) = :count")
    List<Long> findAnimalsWithAllDiseases(@Param("diseaseIds") List<Integer> diseaseIds,
                                          @Param("count") Long count);
}