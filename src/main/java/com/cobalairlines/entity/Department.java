package com.cobalairlines.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Migrated from DB2 DEPT table.
 *
 * Original COBOL DB2 definition:
 *   DEPTID   INT            PRIMARY KEY
 *   NAME     VARCHAR(20)    NOT NULL
 *   MANAGER  CHAR(8)        FK -> EMPLO (circular FK, set via ALTER TABLE)
 *
 * Departments: 1=CEO, 2=Commander, 3=Copilote, 4=Flight Attendant,
 *              5=Human Resources, 6=IT Support, 7=Sales, 8=Legal, 9=Schedule
 *
 * Note: The manager FK back to Employee creates a circular reference.
 * We store it as a plain String empId to avoid circular JPA dependency.
 */
@Entity
@Table(name = "department")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "name", length = 20, nullable = false)
    @NotBlank
    private String name;

    /**
     * Manager employee ID — stored as string to avoid circular JPA mapping.
     * Corresponds to EMPLO.EMPID (CHAR 8).
     */
    @Column(name = "manager_emp_id", length = 8)
    private String managerEmpId;
}
