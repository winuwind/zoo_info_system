package com.zoo.animal.service;

import com.zoo.animal.dto.AnimalFilterRequest;
import com.zoo.animal.dto.AnimalResponse;
import com.zoo.animal.entity.*;
import com.zoo.animal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final SpeciesRepository speciesRepository;
    private final InspectionRepository inspectionRepository;
    private final AnimalVaccinationRepository animalVaccinationRepository;
    private final AnimalDiseaseRepository animalDiseaseRepository;
    private final VaccinationRepository vaccinationRepository;
    private final DiseaseRepository diseaseRepository;
    private final AnimalsCellsRepository animalsCellsRepository;
    private final JdbcTemplate jdbcTemplate;

    public List<AnimalResponse> getAllAnimals(AnimalFilterRequest filter) {
        List<Animal> animals;

        if (filter != null && hasFilters(filter)) {
            animals = applyFilters(filter);
        } else {
            animals = animalRepository.findAllAliveInZoo();
        }

        return animals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getSpeciesCount() {
        List<Object[]> results = animalRepository.getSpeciesCount();
        Map<String, Long> speciesCount = new LinkedHashMap<>();
        for (Object[] result : results) {
            speciesCount.put((String) result[1], ((Number) result[2]).longValue());
        }
        return speciesCount;
    }

    public AnimalResponse getAnimalById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found: " + id));
        return convertToResponse(animal);
    }

    public List<Species> getAllSpecies() {
        return speciesRepository.findAllOrderByTitle();
    }

    public List<Vaccination> getAllVaccinations() {
        return vaccinationRepository.findAllOrdered();
    }

    public List<Disease> getAllDiseases() {
        return diseaseRepository.findAllOrdered();
    }

    public List<Species> getCompatibleSpecies(Integer speciesId) {
        List<Object[]> results = speciesRepository.findCompatibleSpecies(speciesId);
        List<Species> compatible = new ArrayList<>();

        Species mainSpecies = speciesRepository.findById(speciesId).orElse(null);
        if (mainSpecies != null) {
            compatible.add(mainSpecies);
        }

        for (Object[] result : results) {
            Species species = new Species();
            species.setId((Integer) result[0]);
            species.setTitle((String) result[1]);
            compatible.add(species);
        }

        return compatible;
    }

    public List<AnimalResponse> getAnimalsForBreeding() {
        List<Animal> potentialParents = new ArrayList<>();
        List<Animal> allAnimals = animalRepository.findAllAliveInZoo();

        for (Animal animal : allAnimals) {
            Long offspringCount = animalRepository.countOffspringByParentId(animal.getId());
            if (offspringCount == 0) {
                potentialParents.add(animal);
            }
        }

        return potentialParents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private List<Animal> applyFilters(AnimalFilterRequest filter) {
        List<Animal> animals = animalRepository.findAllInZoo();

        return animals.stream()
                .filter(animal -> filterSpecies(animal, filter.getSpeciesId()))
                .filter(animal -> filterAge(animal, filter.getAgeMin(), filter.getAgeMax()))
                .filter(animal -> filterGender(animal, filter.getIsMale()))
                .filter(animal -> filterOffspring(animal, filter))
                .filter(animal -> filterNeedToWarm(animal, filter.getNeedToWarm()))
                .filter(animal -> filterVaccinations(animal, filter.getVaccinationIds()))
                .filter(animal -> filterDiseases(animal, filter.getDiseaseIds()))
                .filter(animal -> filterZooExperience(animal, filter.getZooExperienceMin(), filter.getZooExperienceMax()))
                .filter(animal -> filterAlive(animal, filter.getIsAlive()))
                .filter(animal -> filterInZoo(animal, filter.getIsInZoo()))
                .filter(animal -> filterWeightHeight(animal, filter))
                .filter(animal -> filterCell(animal, filter.getCellNumber()))
                .filter(animal -> filterCompatibility(animal, filter.getCompatibilitySpeciesId()))
                .collect(Collectors.toList());
    }

    private boolean filterSpecies(Animal animal, Integer speciesId) {
        return speciesId == null || animal.getSpeciesId().equals(speciesId);
    }

    private boolean filterAge(Animal animal, Integer ageMin, Integer ageMax) {
        int age = Period.between(animal.getBirthDate(), LocalDate.now()).getYears();
        if (ageMin != null && age < ageMin) return false;
        if (ageMax != null && age > ageMax) return false;
        return true;
    }

    private boolean filterGender(Animal animal, Boolean isMale) {
        return isMale == null || animal.getIsMale().equals(isMale);
    }

    private boolean filterOffspring(Animal animal, AnimalFilterRequest filter) {
        Long offspringCount = animalRepository.countOffspringByParentId(animal.getId());

        if (filter.getHasOffspring() != null) {
            if (filter.getHasOffspring() && offspringCount == 0) return false;
            if (!filter.getHasOffspring() && offspringCount > 0) return false;
        }

        if (filter.getOffspringCountMin() != null && offspringCount < filter.getOffspringCountMin()) return false;
        if (filter.getOffspringCountMax() != null && offspringCount > filter.getOffspringCountMax()) return false;

        return true;
    }

    private boolean filterNeedToWarm(Animal animal, Boolean needToWarm) {
        if (needToWarm == null) return true;

        Optional<Species> species = speciesRepository.findById(animal.getSpeciesId());
        return species.map(s -> s.getNeedToWarm().equals(needToWarm)).orElse(false);
    }

    private boolean filterVaccinations(Animal animal, List<Integer> vaccinationIds) {
        if (vaccinationIds == null || vaccinationIds.isEmpty()) return true;

        List<AnimalVaccination> vaccinations = animalVaccinationRepository.findByAnimalId(animal.getId());
        Set<Integer> animalVaccinationIds = vaccinations.stream()
                .map(AnimalVaccination::getVaccinationId)
                .collect(Collectors.toSet());

        return animalVaccinationIds.containsAll(vaccinationIds);
    }

    private boolean filterDiseases(Animal animal, List<Integer> diseaseIds) {
        if (diseaseIds == null || diseaseIds.isEmpty()) return true;

        List<AnimalDisease> diseases = animalDiseaseRepository.findByAnimalId(animal.getId());
        Set<Integer> animalDiseaseIds = diseases.stream()
                .map(AnimalDisease::getDiseaseId)
                .collect(Collectors.toSet());

        return animalDiseaseIds.containsAll(diseaseIds);
    }

    private boolean filterZooExperience(Animal animal, Integer expMin, Integer expMax) {
        int experience = Period.between(animal.getDateOfAdmission(), LocalDate.now()).getYears();

        if (expMin != null && experience < expMin) return false;
        if (expMax != null && experience > expMax) return false;
        return true;
    }

    private boolean filterAlive(Animal animal, Boolean isAlive) {
        if (isAlive == null) return true;
        boolean alive = animal.getDateDeath() == null;
        return alive == isAlive;
    }

    private boolean filterInZoo(Animal animal, Boolean isInZoo) {
        if (isInZoo == null) return true;
        boolean inZoo = animal.getDateDeparture() == null && animal.getDateDeath() == null;
        return inZoo == isInZoo;
    }

    private boolean filterCell(Animal animal, Integer cellNumber) {
        if(cellNumber == null) return true;
        List<AnimalsCells> cells = animalsCellsRepository.getCellHistory(animal.getId());
        if (cells == null || cells.isEmpty()) return false;
        return Objects.equals(cellNumber, cells.getLast().getCellId());
    }

    private boolean filterWeightHeight(Animal animal, AnimalFilterRequest filter) {
        if(filter.getHeightMin() == null && filter.getHeightMax() == null && filter.getWeightMin() == null && filter.getWeightMax() == null) return true;
        Optional<Inspection> inspectionOpt = inspectionRepository.findLatestInspectionByAnimalId(animal.getId());
        if(inspectionOpt.isEmpty()) return false;
        Inspection inspection = inspectionOpt.get();
        if(filter.getHeightMin() != null && inspection.getHeight() < filter.getHeightMin()) return false;
        if(filter.getHeightMax() != null && inspection.getHeight() > filter.getHeightMax()) return false;
        if(filter.getWeightMin() != null && inspection.getWeight() < filter.getWeightMin()) return false;
        if(filter.getWeightMax() != null && inspection.getWeight() > filter.getWeightMax()) return false;
        return true;
    }

    private boolean filterCompatibility(Animal animal, Integer speciesId) {
        if(speciesId == null) return true;
        List<Object[]> speciesList = speciesRepository.findCompatibleSpecies(animal.getSpeciesId());
        for(Object[] species: speciesList) {
            if(species.length != 2) continue;
            if(species[0].equals(speciesId)){
                return true;
            }
        }
        return false;
    }

    private boolean hasFilters(AnimalFilterRequest filter) {
        return filter.getSpeciesId() != null ||
                filter.getAgeMin() != null ||
                filter.getAgeMax() != null ||
                filter.getIsMale() != null ||
                filter.getHasOffspring() != null ||
                filter.getOffspringCountMin() != null ||
                filter.getOffspringCountMax() != null ||
                filter.getNeedToWarm() != null ||
                (filter.getVaccinationIds() != null && !filter.getVaccinationIds().isEmpty()) ||
                (filter.getDiseaseIds() != null && !filter.getDiseaseIds().isEmpty()) ||
                filter.getZooExperienceMin() != null ||
                filter.getZooExperienceMax() != null ||
                filter.getIsAlive() != null ||
                filter.getIsInZoo() != null ||
                filter.getCellNumber() != null ||
                filter.getHeightMin() != null ||
                filter.getHeightMax() != null ||
                filter.getWeightMin() != null ||
                filter.getWeightMax() != null ||
                filter.getCompatibilitySpeciesId() != null;
    }

    private AnimalResponse convertToResponse(Animal animal) {
        Species species = speciesRepository.findById(animal.getSpeciesId()).orElse(null);

        Optional<Inspection> latestInspection = inspectionRepository.findLatestInspectionByAnimalId(animal.getId());
        Double weight = latestInspection.map(Inspection::getWeight).map(w -> w.doubleValue()).orElse(null);
        Double height = latestInspection.map(Inspection::getHeight).map(h -> h.doubleValue()).orElse(null);
        Long offspringCount = animalRepository.countOffspringByParentId(animal.getId());

        List<AnimalVaccination> animalVaccinations = animalVaccinationRepository.findByAnimalId(animal.getId());
        List<AnimalResponse.VaccinationInfo> vaccinationInfos = new ArrayList<>();
        for (AnimalVaccination av : animalVaccinations) {
            Vaccination vaccination = vaccinationRepository.findById(av.getVaccinationId()).orElse(null);
            if (vaccination != null) {
                vaccinationInfos.add(AnimalResponse.VaccinationInfo.builder()
                        .id(vaccination.getId())
                        .description(vaccination.getDescription())
                        .date(av.getDateVaccination())
                        .build());
            }
        }

        List<AnimalDisease> animalDiseases = animalDiseaseRepository.findByAnimalId(animal.getId());
        List<AnimalResponse.DiseaseInfo> diseaseInfos = new ArrayList<>();
        for (AnimalDisease ad : animalDiseases) {
            Disease disease = diseaseRepository.findById(ad.getDiseaseId()).orElse(null);
            if (disease != null) {
                diseaseInfos.add(AnimalResponse.DiseaseInfo.builder()
                        .id(disease.getId())
                        .description(disease.getDescription())
                        .dateStart(ad.getDateStart())
                        .dateEnd(ad.getDateEnd())
                        .build());
            }
        }

        int age = Period.between(animal.getBirthDate(), LocalDate.now()).getYears();
        int zooExperience = Period.between(animal.getDateOfAdmission(), LocalDate.now()).getYears();

        return AnimalResponse.builder()
                .id(animal.getId())
                .species(species != null ? species.getTitle() : "Unknown")
                .speciesId(animal.getSpeciesId())
                .isMale(animal.getIsMale())
                .age(age)
                .birthDate(animal.getBirthDate())
                .dateOfAdmission(animal.getDateOfAdmission())
                .dateDeparture(animal.getDateDeparture())
                .dateDeath(animal.getDateDeath())
                .weight(weight)
                .height(height)
                .offspringCount(offspringCount != null ? offspringCount.intValue() : 0)
                .vaccinations(vaccinationInfos)
                .diseases(diseaseInfos)
                .zooExperience(zooExperience)
                .needToWarm(species != null ? species.getNeedToWarm() : false)
                .build();
    }

    public List<Map<String, Object>> getSimpleAnimalList() {
        String sql = """
            SELECT a.id, s.title as species_name, a.is_male, 
                   EXTRACT(YEAR FROM AGE(CURRENT_DATE, a.birth_date)) as age
            FROM animals a
            JOIN species s ON a.species_id = s.id
            WHERE a.date_departure IS NULL AND a.date_death IS NULL
            ORDER BY s.title, a.id
        """;
        return jdbcTemplate.queryForList(sql);
    }
}