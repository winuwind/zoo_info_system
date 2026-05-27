package com.zoo.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public List<String> getAllTables() {
        String sql = """
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_type = 'BASE TABLE'
            ORDER BY table_name
        """;
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<Map<String, String>> getTableColumns(String tableName) {
        String sql = """
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = ?
            ORDER BY ordinal_position
        """;
        return jdbcTemplate.queryForList(sql, tableName).stream()
                .map(row -> Map.of(
                        "name", (String) row.get("column_name"),
                        "type", (String) row.get("data_type"),
                        "nullable", (String) row.get("is_nullable")
                ))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTableData(String tableName) {
        String sql = "SELECT * FROM " + tableName + " LIMIT 1000";
        return jdbcTemplate.queryForList(sql);
    }

    @Transactional
    public List<Map<String, Object>> executeSql(String base64Sql) {
        String decodedSql = new String(Base64.getDecoder().decode(base64Sql));
        List<Map<String, Object>> result = jdbcTemplate.queryForList(decodedSql);
        return result;
    }

    @Transactional
    public Map<String, Object> insertRecord(String tableName, Map<String, Object> data) {
        try {
            // Получаем информацию о колонках, которые являются автоинкрементными
            String autoColumnsSql = """
            SELECT column_name 
            FROM information_schema.columns 
            WHERE table_schema = 'public' 
              AND table_name = ? 
              AND (is_identity = 'YES' 
                   OR column_default LIKE 'nextval%%'
                   OR data_type = 'bigserial'
                   OR data_type = 'serial')
        """;

            List<String> autoColumns = jdbcTemplate.queryForList(autoColumnsSql, String.class, tableName);

            // Добавляем стандартные колонки, которые не нужно вставлять
            autoColumns.add("created_at");
            autoColumns.add("updated_at");

            // Фильтруем данные, убирая автоинкрементные колонки
            Map<String, Object> filteredData = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!autoColumns.contains(entry.getKey()) && entry.getValue() != null && !"null".equals(entry.getValue())) {
                    filteredData.put(entry.getKey(), entry.getValue());
                }
            }

            if (filteredData.isEmpty()) {
                return Map.of("status", "error", "message", "No data to insert after removing auto-generated fields");
            }

            String columns = String.join(", ", filteredData.keySet());
            String placeholders = String.join(", ", Collections.nCopies(filteredData.size(), "?"));
            String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

            System.out.println("SQL: " + sql);
            System.out.println("Values: " + filteredData.values());

            jdbcTemplate.update(sql, filteredData.values().toArray());

            return Map.of("status", "success", "message", "Record inserted into " + tableName);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> updateRecord(String tableName, Map<String, Object> primaryKey, Map<String, Object> data) {
        // Экранируем имена колонок
        List<String> setClauses = data.keySet().stream()
            .map(column -> "\"" + column + "\" = ?")
            .collect(Collectors.toList());
    
        List<String> whereClauses = primaryKey.keySet().stream()
            .map(column -> "\"" + column + "\" = ?")
            .collect(Collectors.toList());
    
        String sql = "UPDATE " + tableName + " SET " + String.join(", ", setClauses) + 
                     " WHERE " + String.join(" AND ", whereClauses);
    
        List<Object> params = new ArrayList<>();
        params.addAll(data.values());
        params.addAll(primaryKey.values());
    
        System.out.println("SQL: " + sql);
        System.out.println("Params: " + params);
    
        int updated = jdbcTemplate.update(sql, params.toArray());
    
        if (updated > 0) {
            return Map.of("status", "success", "message", "Record updated successfully");
        } else {
            return Map.of("status", "error", "message", "No record found with given primary key");
        }
    }
}



//TODO: поработать над оформлением (мелкий шрифт, поубирать id). Отчет