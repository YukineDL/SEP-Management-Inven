package EmployeeControllerTest;

import com.inventorymanagement.dto.EmployeePasswordUpdateDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.repository.EmployeeRepository;
import com.inventorymanagement.services.impl.EmployeeServicesImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeServicesImpl.updatePasswordForEmployee(...)
 *
 * <p>Để tắt warning inline-mock-maker, add dependency:</p>
 * <pre>
 * &lt;dependency&gt;
 *   &lt;groupId&gt;org.mockito&lt;/groupId&gt;
 *   &lt;artifactId&gt;mockito-inline&lt;/artifactId&gt;
 *   &lt;version&gt;${mockito.version}&lt;/version&gt;
 *   &lt;scope&gt;test&lt;/scope&gt;
 * &lt;/dependency&gt;
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeServicesImplPasswordTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    @InjectMocks
    private EmployeeServicesImpl employeeServicesImpl;

    private Employee adminUser;
    private Employee normalUser;
    private EmployeePasswordUpdateDTO dto;

    @BeforeEach
    void setUp() {
        // Admin
        adminUser = Employee.builder()
                .id(1)
                .code("ADMIN01")
                .roleCode("ADMIN")
                .password("irrelevant")   // not used for admin path
                .build();

        // Normal user
        normalUser = Employee.builder()
                .id(2)
                .code("EMP02")
                .roleCode("EMPLOYEE")
                .password("hashedOld")    // giả định lưu hashed
                .build();

        dto = new EmployeePasswordUpdateDTO("oldPass", "newPass");
    }

    @Test
    void whenAdmin_thenCanUpdateAnyPassword() throws InventoryException {
        // stub getFullInformation→admin, repo returns normalUser
        doReturn(adminUser).when(employeeServicesImpl).getFullInformation(anyString());
        when(employeeRepository.findByCode("EMP02")).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.encode("newPass")).thenReturn("hashedNew");

        // call
        employeeServicesImpl.updatePasswordForEmployee("token", "EMP02", dto);

        // verify
        assertEquals("hashedNew", normalUser.getPassword());
        verify(employeeRepository, times(1)).save(normalUser);
    }

    @Test
    void whenMeIsNull_thenThrowNoPermission() {
        // stub getFullInformation→null
        doReturn(null).when(employeeServicesImpl).getFullInformation(anyString());

        InventoryException ex = assertThrows(
                InventoryException.class,
                () -> employeeServicesImpl.updatePasswordForEmployee("token", "EMP02", dto)
        );

        assertEquals(
                ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION),
                ex.getMessage()
        );
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void whenEmployeeNotFound_thenThrowNotExisted() {
        doReturn(adminUser).when(employeeServicesImpl).getFullInformation(anyString());
        when(employeeRepository.findByCode("EMP02")).thenReturn(Optional.empty());

        InventoryException ex = assertThrows(
                InventoryException.class,
                () -> employeeServicesImpl.updatePasswordForEmployee("token", "EMP02", dto)
        );

        assertEquals(
                ExceptionMessage.messages.get(ExceptionMessage.EMPLOYEE_NOT_EXISTED),
                ex.getMessage()
        );
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void whenNonAdminUpdatingOther_thenThrowNoPermission() {
        // normalUser tries to update someone else
        doReturn(normalUser).when(employeeServicesImpl).getFullInformation(anyString());
        // employee being updated is a different code
        Employee other = Employee.builder().code("EMP99").roleCode("EMPLOYEE").build();
        when(employeeRepository.findByCode("EMP99")).thenReturn(Optional.of(other));

        InventoryException ex = assertThrows(
                InventoryException.class,
                () -> employeeServicesImpl.updatePasswordForEmployee("token", "EMP99", dto)
        );

        assertEquals(
                ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION),
                ex.getMessage()
        );
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void whenNonAdminSelfOldPasswordMismatch_thenThrowUpdatePasswordFail() {
        // normalUser updates self, but old password mismatch
        doReturn(normalUser).when(employeeServicesImpl).getFullInformation(anyString());
        when(employeeRepository.findByCode("EMP02")).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.matches("oldPass", normalUser.getPassword())).thenReturn(false);

        InventoryException ex = assertThrows(
                InventoryException.class,
                () -> employeeServicesImpl.updatePasswordForEmployee("token", "EMP02", dto)
        );

        assertEquals(
                ExceptionMessage.messages.get(ExceptionMessage.UPDATE_PASSWORD_FAIL),
                ex.getMessage()
        );
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void whenNonAdminSelfOldPasswordMatches_thenCanUpdatePassword() throws InventoryException {
        doReturn(normalUser).when(employeeServicesImpl).getFullInformation(anyString());
        when(employeeRepository.findByCode("EMP02")).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.matches("oldPass", normalUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("hashedNew");

        employeeServicesImpl.updatePasswordForEmployee("token", "EMP02", dto);

        assertEquals("hashedNew", normalUser.getPassword());
        verify(employeeRepository, times(1)).save(normalUser);
    }
}
