package EmployeeControllerTest;
import com.inventorymanagement.dto.EmployeeUpdateDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.EmployeeRepository;
import com.inventorymanagement.services.impl.EmployeeServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeUpdateTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    @InjectMocks
    private EmployeeServicesImpl employeeServicesImpl;

    private Employee managerEmployee;
    private Employee targetEmployee;
    private EmployeeUpdateDTO employeeUpdateDTO;

    @BeforeEach
    void setUp() {
        // Prepare a manager with permission role (must be in LIST_MANAGER in production code)
        managerEmployee = Employee.builder()
                .id(1)
                .code("MANAGER001")
                .roleCode("MANAGER")
                .name("Manager Test")
                .build();

        // Employee to update
        targetEmployee = Employee.builder()
                .id(2)
                .code("EMP002")
                .roleCode("EMPLOYEE")
                .name("Old Name")
                .phoneNumber("123456789")
                .build();

        // DTO with new info
        employeeUpdateDTO = new EmployeeUpdateDTO(
                "New Name",
                "987654321",
                "ADMIN"
        );
    }

    @Test
    void testUpdateByCode_Success() throws InventoryException {
        // Stub internal call
        doReturn(managerEmployee)
                .when(employeeServicesImpl)
                .getFullInformation(anyString());
        when(employeeRepository.findByCode("EMP002"))
                .thenReturn(Optional.of(targetEmployee));

        // Execute method
        employeeServicesImpl.updateByCode("Bearer token", "EMP002", employeeUpdateDTO);

        // Assert updated values
        assertEquals("New Name", targetEmployee.getName());
        assertEquals("ADMIN", targetEmployee.getRoleCode());
        assertEquals("987654321", targetEmployee.getPhoneNumber());

        // Verify save called once
        verify(employeeRepository, times(1)).save(targetEmployee);
    }

    @Test
    void testUpdateByCode_NoPermission_ThrowsException() {
        // Manager has no permission (role not in LIST_MANAGER)
        managerEmployee.setRoleCode("EMPLOYEE");
        doReturn(managerEmployee)
                .when(employeeServicesImpl)
                .getFullInformation(anyString());

        InventoryException ex = assertThrows(
                InventoryException.class,
                () -> employeeServicesImpl.updateByCode("Bearer token", "EMP002", employeeUpdateDTO)
        );

        // Verify exception contains correct error message
        assertEquals(
                ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION),
                ex.getMessage()
        );
    }

    @Test
    void testUpdateByCode_EmployeeNotFound_ThrowsException() {
        // Stub to return manager
        doReturn(managerEmployee)
                .when(employeeServicesImpl)
                .getFullInformation(anyString());
        // Employee not found
        when(employeeRepository.findByCode("EMP002"))
                .thenReturn(Optional.empty());

        InventoryException ex = assertThrows(
                InventoryException.class,
                () -> employeeServicesImpl.updateByCode("Bearer token", "EMP002", employeeUpdateDTO)
        );

        // Verify exception contains correct error message
        assertEquals(
                ExceptionMessage.messages.get(ExceptionMessage.EMPLOYEE_NOT_EXISTED),
                ex.getMessage()
        );
    }
}
