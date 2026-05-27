package com.zoo.animal.repository;

import com.zoo.animal.entity.AnimalVaccination;
import com.zoo.animal.entity.AnimalVaccination.AnimalVaccinationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalVaccinationRepository extends JpaRepository<AnimalVaccination, AnimalVaccinationId> {

    @Query("SELECT av FROM AnimalVaccination av WHERE av.animalId = :animalId")
    List<AnimalVaccination> findByAnimalId(@Param("animalId") Long animalId);

    @Query("SELECT av.animalId FROM AnimalVaccination av WHERE av.vaccinationId IN :vaccinationIds " +
            "GROUP BY av.animalId HAVING COUNT(DISTINCT av.vaccinationId) = :count")
    List<Long> findAnimalsWithAllVaccinations(@Param("vaccinationIds") List<Integer> vaccinationIds,
                                              @Param("count") Long count);
}