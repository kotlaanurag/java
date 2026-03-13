package com.cobalairlines.controller;

import com.cobalairlines.dto.EmployeeDTO;
import com.cobalairlines.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Employee controller — replaces EMPINSRT batch job (EMPLO-MAIN-INSERT).
 *
 * Endpoints:
 *   GET  /api/employees              -> list all (CEO, HR, IT Support)
 *   GET  /api/employees/{empId}      -> get by ID
 *   GET  /api/employees?deptId=N     -> filter by department
 *   POST /api/employees              -> create single employee (includes password hash)
 *   POST /api/employees/import       -> bulk import JSON (replaces EMPINSRT batch + EMPLOYEE-LIST.json)
 *   PUT  /api/employees/{id}/password -> change password (replaces CRYPTPGM)
 *   DELETE /api/employees/{id}       -> delete
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAll(
            @RequestParam(required = false) Integer deptId) {
        if (deptId != null) {
            return ResponseEntity.ok(employeeService.getByDepartment(deptId));
        }
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{empId}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable String empId) {
        return ResponseEntity.ok(employeeService.getById(empId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CEO', 'HUMAN_RESOURCES')")
    public ResponseEntity<EmployeeDTO> create(@Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.ok(employeeService.create(dto));
    }

    /**
     * Bulk employee import — replaces EMPINSRT batch job.
     * Original: parsed EMPLOYEE-LIST.json (30 records), called CRYPTPGM per employee.
     * Here: POST a JSON array to this endpoint.
     */
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('CEO', 'HUMAN_RESOURCES', 'IT_SUPPORT')")
    public ResponseEntity<List<EmployeeDTO>> bulkImport(@RequestBody List<@Valid EmployeeDTO> employees) {
        return ResponseEntity.ok(employeeService.bulkImport(employees));
    }

    /**
     * Password update — replaces CRYPTPGM (EMPLO-CRYPTO-PASS) batch program.
     * PUT /api/employees/{empId}/password  body: { "password": "newPassword" }
     */
    @PutMapping("/{empId}/password")
    public ResponseEntity<EmployeeDTO> updatePassword(
            @PathVariable String empId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(employeeService.updatePassword(empId, body.get("password")));
    }

    @DeleteMapping("/{empId}")
    @PreAuthorize("hasAnyRole('CEO', 'HUMAN_RESOURCES')")
    public ResponseEntity<Void> delete(@PathVariable String empId) {
        employeeService.delete(empId);
        return ResponseEntity.noContent().build();
    }
}
