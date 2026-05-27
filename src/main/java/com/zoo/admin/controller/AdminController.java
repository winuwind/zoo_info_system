package com.zoo.admin.controller;

import com.zoo.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/tables")
    public ResponseEntity<List<String>> getAllTables() {
        return ResponseEntity.ok(adminService.getAllTables());
    }

    @GetMapping("/tables/{tableName}/columns")
    public ResponseEntity<List<Map<String, String>>> getTableColumns(@PathVariable String tableName) {
        return ResponseEntity.ok(adminService.getTableColumns(tableName));
    }

    @GetMapping("/tables/{tableName}/data")
    public ResponseEntity<List<Map<String, Object>>> getTableData(@PathVariable String tableName) {
        return ResponseEntity.ok(adminService.getTableData(tableName));
    }

    @PostMapping("/sql")
    public ResponseEntity<List<Map<String, Object>>> executeSql(@RequestBody Map<String, String> request) {
        String base64Sql = request.get("sql");
        return ResponseEntity.ok(adminService.executeSql(base64Sql));
    }

    @PostMapping("/tables/{tableName}/insert")
    public ResponseEntity<Map<String, Object>> insertRecord(
            @PathVariable String tableName,
            @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(adminService.insertRecord(tableName, data));
    }

    @PutMapping("/tables/{tableName}/update")
    public ResponseEntity<?> updateRecord(
            @PathVariable String tableName,
            @RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> primaryKey = (Map<String, Object>) request.get("primaryKey");
            Map<String, Object> data = (Map<String, Object>) request.get("data");

            Map<String, Object> result = adminService.updateRecord(tableName, primaryKey, data);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}