package com.cobalairlines.dto;

import com.cobalairlines.entity.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Migrated from EMPLOYEE-LIST.json structure parsed by EMPLO-LECTURE-JSON.
 *
 * Used for both:
 *  - Bulk JSON import (POST /api/employees/import) — replaces EMPINSRT batch job
 *  - Single employee creation (POST /api/employees)
 *  - Employee search results
 */
@Data
public class EmployeeDTO {

    @NotBlank
    private String empId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String address;
    private String city;
    private String zipcode;
    private String telephone;

    @Email
    private String email;

    private LocalDate admissionDate;
    private BigDecimal salary;

    @NotNull
    private Integer deptId;

    /** Plain text password — hashed before storage (replaces CRYPTPGM batch) */
    private String password;

    // dept name — populated on read
    private String departmentName;

    public static EmployeeDTO from(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmpId(e.getEmpId());
        dto.setFirstName(e.getFirstName());
        dto.setLastName(e.getLastName());
        dto.setAddress(e.getAddress());
        dto.setCity(e.getCity());
        dto.setZipcode(e.getZipcode());
        dto.setTelephone(e.getTelephone());
        dto.setEmail(e.getEmail());
        dto.setAdmissionDate(e.getAdmissionDate());
        dto.setSalary(e.getSalary());
        if (e.getDepartment() != null) {
            dto.setDeptId(e.getDepartment().getDeptId());
            dto.setDepartmentName(e.getDepartment().getName());
        }
        return dto;
    }
}
