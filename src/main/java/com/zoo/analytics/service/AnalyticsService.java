package com.zoo.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getDealerFoods(Integer foodId, String dateStart, String dateEnd,
                                                    Integer countMin, Integer countMax,
                                                    Integer priceMin, Integer priceMax) {
        StringBuilder sql = new StringBuilder("""
            SELECT d.id, d.contact, d.location, f.description as food_name,
                   df.count, df.price, df.date_of_purchase, df.date_delivery
            FROM dealers d
            JOIN dealer_food df ON d.id = df.dealer_id
            JOIN foods f ON df.food_id = f.id
            WHERE 1=1
        """);

        if (foodId != null) {
            sql.append(" AND df.food_id = ").append(foodId);
        }
        if (dateStart != null) {
            sql.append(" AND df.date_of_purchase >= '").append(dateStart).append("'");
        }
        if (dateEnd != null) {
            sql.append(" AND df.date_of_purchase <= '").append(dateEnd).append("'");
        }
        if (countMin != null) {
            sql.append(" AND df.count >= ").append(countMin);
        }
        if (countMax != null) {
            sql.append(" AND df.count <= ").append(countMax);
        }
        if (priceMin != null) {
            sql.append(" AND df.price >= ").append(priceMin);
        }
        if (priceMax != null) {
            sql.append(" AND df.price <= ").append(priceMax);
        }

        sql.append(" ORDER BY df.date_of_purchase");

        return jdbcTemplate.queryForList(sql.toString());
    }

    public List<Map<String, Object>> getZooProducedFoods() {
        String sql = """
            SELECT f.id, f.description, f.type_id, tf.description as type_name,
                   SUM(df.count) as total_produced, AVG(df.price) as avg_price
            FROM foods f
            JOIN type_of_foods tf ON f.type_id = tf.id
            JOIN dealer_food df ON f.id = df.food_id
            WHERE f.is_it_produced_by_the_zoo = true
            GROUP BY f.id, f.type_id, tf.description
            ORDER BY f.description
        """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getAnimalsByFoodType(Integer foodTypeId, Integer seasonId, Integer ageMin) {
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT a.id, s.title as species, a.is_male,
                   EXTRACT(YEAR FROM AGE(CURRENT_DATE, a.birth_date)) as age
            FROM animals a
            JOIN species s ON a.species_id = s.id
            JOIN animals_type_of_foods atf ON s.id = atf.species_id
            WHERE atf.type_id = ? AND a.date_departure IS NULL AND a.date_death IS NULL
        """);

        List<Object> params = new java.util.ArrayList<>();
        params.add(foodTypeId);

        if (seasonId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM menu m WHERE m.species_id = s.id AND m.season_id = ?)");
            params.add(seasonId);
        }

        if (ageMin != null) {
            sql.append(" AND EXTRACT(YEAR FROM AGE(CURRENT_DATE, a.birth_date)) >= ?");
            params.add(ageMin);
        }

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public List<Map<String, Object>> getExchangeZoos(Integer speciesId) {
        StringBuilder sql = new StringBuilder("""
            SELECT z.id, z.location, COUNT(DISTINCT ase.animal_id) as animals_sent,
                   COUNT(DISTINCT are.animal_id) as animals_received
            FROM zoos z
            JOIN exchanges e ON z.id = e.zoo_id
            LEFT JOIN animals_sent ase ON e.id = ase.exchange_id
            LEFT JOIN animals_recieved are ON e.id = are.exchange_id
        """);

        if (speciesId != null) {
            sql.append(" LEFT JOIN animals a_sent ON ase.animal_id = a_sent.id");
            sql.append(" LEFT JOIN animals a_rec ON are.animal_id = a_rec.id");
            sql.append(" WHERE a_sent.species_id = ? OR a_rec.species_id = ?");
            sql.append(" GROUP BY z.id, z.location");
            return jdbcTemplate.queryForList(sql.toString(), speciesId, speciesId);
        }

        sql.append(" GROUP BY z.id, z.location ORDER BY z.location");
        return jdbcTemplate.queryForList(sql.toString());
    }

    public List<Map<String, Object>> getAllFoods(){
        StringBuilder sql = new StringBuilder("""
            SELECT f.id as id,
                f.quantity_in_a_conventional_unit as quantity_in_a_conventional_unit,
                f.type_id as type_id, tof.description as type_description,
                f.is_it_produced_by_the_zoo as is_it_produced_by_the_zoo,
                f.description as description
            FROM foods f
            JOIN type_of_foods tof ON f.type_id = tof.id
        """);
        return jdbcTemplate.queryForList(sql.toString());
    }

    public List<Map<String, Object>> getFoodTypes() {
        String sql = "SELECT id, description FROM type_of_foods ORDER BY id";
        return jdbcTemplate.queryForList(sql);
    }
}