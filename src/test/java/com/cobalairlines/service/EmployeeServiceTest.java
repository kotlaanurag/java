package com.cobalairlines.service;

import com.cobalairlines.dto.EmployeeDTO;
import com.cobalairlines.entity.Department;
import com.cobalairlines.entity.Employee;
import com.cobalairlines.exception.ResourceNotFoundException;
import com.cobalairlines.repository.DepartmentRepository;
import com.cobalairlines.repository.EmployeeRepository;
import com.cobalairlines.security.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private CryptoService cryptoService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department(5, "Human Resources", null);

        employee = new Employee();
        employee.setEmpId("EMP001");
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setEmail("jane.doe@cobal.com");
        employee.setDepartment(department);
        employee.setPasswordHash("hashed");
    }

    @Test
    void getById_found_returnsDTO() {
        when(employeeRepository.findByEmpId("EMP001")).thenReturn(Optional.of(employee));

        EmployeeDTO dto = employeeService.getById("EMP001");

        assertThat(dto.getEmpId()).isEqualTo("EMP001");
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getDepartmentName()).isEqualTo("Human Resources");
    }

    @Test
    void getById_notFound_throwsException() {
        when(employeeRepository.findByEmpId("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById("UNKNOWN"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_returnsMappedList() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<EmployeeDTO> result = employeeService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmpId()).isEqualTo("EMP001");
    }

    @Test
    void getByDepartment_returnsDeptEmployees() {
        when(employeeRepository.findByDepartmentDeptId(5)).thenReturn(List.of(employee));

        List<EmployeeDTO> result = employeeService.getByDepartment(5);

        assertThat(result).hasSize(1);
    }

    @Test
    void create_withPassword_hashesAndSaves() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmpId("EMP002");
        dto.setFirstName("Mark");
        dto.setLastName("Lee");
        dto.setDeptId(5);
        dto.setPassword("plain123");

        when(departmentRepository.findById(5)).thenReturn(Optional.of(department));
        when(cryptoService.hashPassword("plain123")).thenReturn("bcrypt_hash");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setDepartment(department);
            return e;
        });

        EmployeeDTO result = employeeService.create(dto);

        assertThat(result.getEmpId()).isEqualTo("EMP002");
        verify(cryptoService).hashPassword("plain123");
    }

    @Test
    void create_withoutPassword_doesNotHash() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmpId("EMP003");
        dto.setFirstName("Anna");
        dto.setLastName("Brown");
        dto.setDeptId(5);

        when(departmentRepository.findById(5)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setDepartment(department);
            return e;
        });

        employeeService.create(dto);

        verify(cryptoService, never()).hashPassword(any());
    }

    @Test
    void create_deptNotFound_throwsException() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmpId("EMP004");
        dto.setFirstName("Tom");
        dto.setLastName("White");
        dto.setDeptId(99);

        when(departmentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePassword_updatesHashAndSaves() {
        when(employeeRepository.findByEmpId("EMP001")).thenReturn(Optional.of(employee));
        when(cryptoService.hashPassword("newpass")).thenReturn("new_hash");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.updatePassword("EMP001", "newpass");

        verify(cryptoService).hashPassword("newpass");
        verify(employeeRepository).save(employee);
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        employeeService.delete("EMP001");
        verify(employeeRepository).deleteById("EMP001");
    }

    @Test
    void bulkImport_createsAllEmployees() {
        EmployeeDTO dto1 = new EmployeeDTO();
        dto1.setEmpId("E01"); dto1.setFirstName("A"); dto1.setLastName("B"); dto1.setDeptId(5);

        EmployeeDTO dto2 = new EmployeeDTO();
        dto2.setEmpId("E02"); dto2.setFirstName("C"); dto2.setLastName("D"); dto2.setDeptId(5);

        when(departmentRepository.findById(5)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setDepartment(department);
            return e;
        });

        List<EmployeeDTO> result = employeeService.bulkImport(List.of(dto1, dto2));

        assertThat(result).hasSize(2);
        verify(employeeRepository, times(2)).save(any(Employee.class));
    }
}
