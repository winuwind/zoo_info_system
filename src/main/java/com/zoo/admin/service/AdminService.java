package com.zoo.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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
        // Экранируем имена колонок двойными кавычками
        List<String> escapedColumns = data.keySet().stream()
            .map(column -> "\"" + column + "\"")
            .collect(Collectors.toList());
    
        String columns = String.join(", ", escapedColumns);
        String placeholders = data.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
    
        System.out.println("SQL: " + sql);
        System.out.println("Values: " + data.values());
    
        jdbcTemplate.update(sql, data.values().toArray());
    
        return Map.of("status", "success", "message", "Record inserted into " + tableName);
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