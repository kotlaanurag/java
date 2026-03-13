package com.cobalairlines.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Migrated from DB2 EMPLO table and EMPLO-DCLGEN copybook.
 *
 * Original COBOL DB2 definition:
 *   EMPID     CHAR(8)        PRIMARY KEY
 *   FIRSTNAME VARCHAR(30)    NOT NULL
 *   LASTNAME  VARCHAR(30)    NOT NULL
 *   ADDRE     VARCHAR(100)
 *   CITY      VARCHAR(50)
 *   ZIPCODE   VARCHAR(15)
 *   TELEPHONE VARCHAR(10)
 *   EMAIL     VARCHAR(100)
 *   ADMIDATE  DATE
 *   SALARY    DEC(8,2)
 *   DEPTID    INT            FK -> DEPT
 *
 * Bulk load: migrated from EMPLO-MAIN-INSERT + EMPLO-LECTURE-JSON
 * (JSON file with 30 employee records).
 * Password encryption: migrated from EMPLO-CRYPTO-PASS (CRYPTPGM).
 */
@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @Column(name = "emp_id", length = 8)
    @NotBlank
    private String empId;

    @Column(name = "first_name", length = 30, nullable = false)
    @NotBlank
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    @NotBlank
    private String lastName;

    @Column(name = "address", length = 100)
    private String address;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "zipcode", length = 15)
    private String zipcode;

    @Column(name = "telephone", length = 10)
    private String telephone;

    @Column(name = "email", length = 100)
    @Email
    private String email;

    /** Date employee was admitted / hired. Used by COBOL crypto algorithm as seed. */
    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "salary", precision = 8, scale = 2)
    private BigDecimal salary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    /**
     * BCrypt-hashed password stored by CryptoService.
     * Replaces the COBOL custom XOR-based encryption in EMPLO-CRYPTO-PASS.
     * Original: 8-char encrypted entry in sequential PASSDOC file.
     */
    @Column(name = "password_hash", length = 256)
    private String passwordHash;
}
