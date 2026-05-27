package com.zoo.animal.repository;

import com.zoo.animal.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Integer> {

    @Query("SELECT s FROM Species s ORDER BY s.title")
    List<Species> findAllOrderByTitle();

    @Query(value = "SELECT DISTINCT s1.id, s1.title FROM species s1 " +
            "JOIN compatibility_species cs ON s1.id = cs.species_1_id OR s1.id = cs.species_2_id " +
            "WHERE (cs.species_1_id = :speciesId OR cs.species_2_id = :speciesId) " +
            "AND s1.id != :speciesId", nativeQuery = true)
    List<Object[]> findCompatibleSpecies(@Param("speciesId") Integer speciesId);
}