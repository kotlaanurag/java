package com.cobalairlines.controller;

import com.cobalairlines.entity.Department;
import com.cobalairlines.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Department reference data controller.
 * 9 departments seeded from DB2 insertion-1 script.
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    public ResponseEntity<List<Department>> getAll() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @GetMapping("/{deptId}")
    public ResponseEntity<Department> getById(@PathVariable Integer deptId) {
        return departmentRepository.findById(deptId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
