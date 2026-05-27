package com.zoo.animal.repository;

import com.zoo.animal.entity.Animal;
import com.zoo.animal.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long>, JpaSpecificationExecutor<Animal> {

    Optional<Animal> findById(Long id);

    @Query("SELECT a FROM Animal a")
    List<Animal> findAllInZoo();

    @Query("SELECT a FROM Animal a WHERE a.dateDeath IS NULL AND a.dateDeparture IS NULL")
    List<Animal> findAllAliveInZoo();

    @Query("SELECT a FROM Animal a WHERE a.speciesId = :speciesId AND a.dateDeparture IS NULL AND a.dateDeath IS NULL")
    List<Animal> findBySpeciesId(@Param("speciesId") Integer speciesId);

    @Query(value = "SELECT COUNT(*) FROM animals a WHERE a.species_id = :speciesId " +
            "AND a.date_departure IS NULL AND a.date_death IS NULL", nativeQuery = true)
    Long countBySpeciesId(@Param("speciesId") Integer speciesId);

    @Query(value = "SELECT s.id, s.title, COUNT(a.id) as count " +
            "FROM species s LEFT JOIN animals a ON s.id = a.species_id " +
            "AND (a.date_departure IS NULL AND a.date_death IS NULL) " +
            "GROUP BY s.id, s.title ORDER BY s.title", nativeQuery = true)
    List<Object[]> getSpeciesCount();

    @Query("SELECT a FROM Animal a WHERE a.parentMaleId = :parentId OR a.parentFemaleId = :parentId")
    List<Animal> findOffspringByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(a) FROM Animal a WHERE a.parentMaleId = :parentId OR a.parentFemaleId = :parentId")
    Long countOffspringByParentId(@Param("parentId") Long parentId);
}