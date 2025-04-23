package AuthenticatedTest;

import com.inventorymanagement.dto.RegisterDTO;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.EmployeeRepository;
import com.inventorymanagement.services.impl.AuthenticatedServices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedTest {

    @InjectMocks
    private AuthenticatedServices authenticatedServices;

    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    void testRegister_UsernameAlreadyExists() {
        // Arrange
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("admin");

        when(employeeRepository.existsByUsername("admin")).thenReturn(true);

        // Act & Assert
        InventoryException ex = assertThrows(
                InventoryException.class,
                () -> authenticatedServices.register(dto)
        );
        assertEquals(ExceptionMessage.EMPLOYEE_EXISTED, ex.getCodeMessage());
        assertEquals(ExceptionMessage.messages.get(ExceptionMessage.EMPLOYEE_EXISTED), ex.getMessage());
    }
}
