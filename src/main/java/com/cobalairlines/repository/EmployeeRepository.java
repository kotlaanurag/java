package com.cobalairlines.repository;

import com.cobalairlines.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Replaces COBOL DB2 queries in LOGIN-COB and EMPLO-MAIN-INSERT.
 *
 * Original COBOL:
 *   EXEC SQL SELECT ... INTO :WS-EMP-FIELDS
 *            FROM EMPLO WHERE EMPID = :WS-USERID END-EXEC
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmpId(String empId);

    List<Employee> findByDepartmentDeptId(Integer deptId);

    List<Employee> findByLastNameIgnoreCase(String lastName);

    List<Employee> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
}
