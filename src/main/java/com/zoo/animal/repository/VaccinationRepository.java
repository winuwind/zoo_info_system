package com.zoo.animal.repository;

import com.zoo.animal.entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {

    @Query("SELECT v FROM Vaccination v ORDER BY v.id")
    List<Vaccination> findAllOrdered();
}