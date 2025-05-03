package customerTest;

import com.inventorymanagement.dto.CustomerDTO;
import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.CustomerRepository;
import com.inventorymanagement.services.impl.CustomerServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class CustomerServicesTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServicesImpl customerServicesImpl;

    private CustomerDTO customerDTO;
    private Customer savedCustomer;

    @BeforeEach
    void setUp() {
        customerDTO = CustomerDTO.builder()
                .name("John Doe")
                .address("123 Main St")
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .website("www.johndoe.com")
                .build();

        savedCustomer = Customer.builder()
                .id(1)
                .name(customerDTO.getName())
                .address(customerDTO.getAddress())
                .phoneNumber(customerDTO.getPhoneNumber())
                .email(customerDTO.getEmail())
                .website(customerDTO.getWebsite())
                .build();
    }

    @Test
    void testCreateCustomer_Success() throws InventoryException {
        // Giả lập chưa tồn tại số điện thoại
        when(customerRepository.existsByPhoneNumber(customerDTO.getPhoneNumber()))
                .thenReturn(false);
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        Customer result = customerServicesImpl.createCustomer(customerDTO);

        // Xác nhận thông tin trả về đúng
        assertNotNull(result);
        assertEquals(customerDTO.getName(), result.getName());
        assertEquals(customerDTO.getPhoneNumber(), result.getPhoneNumber());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_PhoneNumberExists_ThrowsException() {
        // Giả lập số điện thoại đã tồn tại
        when(customerRepository.existsByPhoneNumber(customerDTO.getPhoneNumber()))
                .thenReturn(true);

        InventoryException ex = assertThrows(
                InventoryException.class,
                () -> customerServicesImpl.createCustomer(customerDTO)
        );

        // Xác nhận thông điệp lỗi
        assertEquals(
                ExceptionMessage.messages.get(ExceptionMessage.CUSTOMER_PHONE_EXISTED),
                ex.getMessage()
        );

        verify(customerRepository, never()).save(any(Customer.class));
    }
}