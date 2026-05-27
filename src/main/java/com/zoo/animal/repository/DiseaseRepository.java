package com.zoo.animal.repository;

import com.zoo.animal.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Integer> {

    @Query("SELECT d FROM Disease d ORDER BY d.id")
    List<Disease> findAllOrdered();
}