package com.cobalairlines.service;

import com.cobalairlines.dto.EmployeeDTO;
import com.cobalairlines.entity.Department;
import com.cobalairlines.entity.Employee;
import com.cobalairlines.exception.ResourceNotFoundException;
import com.cobalairlines.repository.DepartmentRepository;
import com.cobalairlines.repository.EmployeeRepository;
import com.cobalairlines.security.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Employee service — migrated from:
 *   - EMPLO-MAIN-INSERT / EMPLO-LECTURE-JSON / CRYPTPGM (batch employee load)
 *
 * Original batch (EMPINSRT):
 *   1. Call SUINSRT to parse JSON (up to 30 employees)
 *   2. For each employee:
 *      a. CALL 'CRYPTPGM' to encrypt password -> write to PASSDOC
 *      b. EXEC SQL INSERT INTO EMPLO (...) END-EXEC
 *      c. Report SQLCODE
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CryptoService cryptoService;

    public EmployeeDTO getById(String empId) {
        return EmployeeDTO.from(employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + empId)));
    }

    public List<EmployeeDTO> getAll() {
        return employeeRepository.findAll().stream().map(EmployeeDTO::from).collect(Collectors.toList());
    }

    public List<EmployeeDTO> getByDepartment(Integer deptId) {
        return employeeRepository.findByDepartmentDeptId(deptId).stream()
                .map(EmployeeDTO::from).collect(Collectors.toList());
    }

    /**
     * Create single employee — includes password hashing via CryptoService.
     * Replaces CRYPTPGM + DB2 INSERT per employee in EMPINSRT.
     */
    @Transactional
    public EmployeeDTO create(EmployeeDTO dto) {
        Department dept = departmentRepository.findById(dto.getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.getDeptId()));

        Employee e = new Employee();
        e.setEmpId(dto.getEmpId());
        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setAddress(dto.getAddress());
        e.setCity(dto.getCity());
        e.setZipcode(dto.getZipcode());
        e.setTelephone(dto.getTelephone());
        e.setEmail(dto.getEmail());
        e.setAdmissionDate(dto.getAdmissionDate());
        e.setSalary(dto.getSalary());
        e.setDepartment(dept);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            e.setPasswordHash(cryptoService.hashPassword(dto.getPassword()));
        }

        return EmployeeDTO.from(employeeRepository.save(e));
    }

    /**
     * Bulk import (up to 30 employees) — replaces EMPINSRT batch job.
     * Accepts a JSON list — equivalent to EMPLOYEE-LIST.json parsed by SUINSRT.
     */
    @Transactional
    public List<EmployeeDTO> bulkImport(List<EmployeeDTO> employees) {
        return employees.stream().map(this::create).collect(Collectors.toList());
    }

    @Transactional
    public EmployeeDTO updatePassword(String empId, String newPassword) {
        Employee e = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + empId));
        e.setPasswordHash(cryptoService.hashPassword(newPassword));
        return EmployeeDTO.from(employeeRepository.save(e));
    }

    @Transactional
    public void delete(String empId) {
        employeeRepository.deleteById(empId);
    }
}
