package inventoryReceiptTest;


import com.inventorymanagement.dto.InventoryReceiptReqDTO;
import com.inventorymanagement.entity.PurchaseOrder;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.InventoryReceiptRepository;
import com.inventorymanagement.repository.PurchaseOrderRepository;
import com.inventorymanagement.repository.ProcessCheckRepository;
import com.inventorymanagement.services.impl.InventoryReceiptServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryReceiptServicesImplTest {

    @Mock
    private InventoryReceiptRepository inventoryReceiptRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ProcessCheckRepository processCheckRepository; // 👉 ADD Mock đây!!

    @InjectMocks
    private InventoryReceiptServicesImpl inventoryReceiptServices;

    private InventoryReceiptReqDTO receiptReqDTO;

    @BeforeEach
    void setUp() {
        receiptReqDTO = InventoryReceiptReqDTO.builder()
                .purchaseOrderCode("PO123")
                .accountingDate(LocalDate.now())
                .documentDate(LocalDate.now())
                .numberOfReceipts("NR001")
                .employeeCode("EMP001")
                .totalAmount(1000.0)
                .build();
    }

    @Test
    void testCreateReceipt_Success() throws InventoryException {
        // Given
        when(inventoryReceiptRepository.existsByPurchaseOrderCodeAndApproveIn(
                any(String.class), any(List.class))).thenReturn(false);
        when(purchaseOrderRepository.findByCode("PO123"))
                .thenReturn(Optional.of(new PurchaseOrder()));
        when(processCheckRepository.save(any())).thenReturn(null); // 👉 Mock thêm save processCheck

        // When
        String result = inventoryReceiptServices.createReceipt(receiptReqDTO);

        // Then
        assertNotNull(result);
        verify(inventoryReceiptRepository, times(1))
                .existsByPurchaseOrderCodeAndApproveIn(any(String.class), any(List.class));
        verify(purchaseOrderRepository, times(1))
                .findByCode("PO123");
        verify(processCheckRepository, times(1)) // 👉 kiểm tra đã gọi
                .save(any());
    }

    @Test
    void testCreateReceipt_AlreadyExists_ThrowException() {
        // Given
        when(inventoryReceiptRepository.existsByPurchaseOrderCodeAndApproveIn(
                any(String.class), any(List.class))).thenReturn(true);

        // When + Then
        InventoryException exception = assertThrows(InventoryException.class, () ->
                inventoryReceiptServices.createReceipt(receiptReqDTO));

        assertEquals("INVALID_CREATE_INVENTORY_RECEIPT", exception.getCodeMessage());
        verify(inventoryReceiptRepository, times(1))
                .existsByPurchaseOrderCodeAndApproveIn(any(String.class), any(List.class));
    }

    @Test
    void testCreateReceipt_PurchaseOrderNotFound_ThrowException() {
        // Given
        when(inventoryReceiptRepository.existsByPurchaseOrderCodeAndApproveIn(
                any(String.class), any(List.class))).thenReturn(false);
        when(purchaseOrderRepository.findByCode("PO123"))
                .thenReturn(Optional.empty());

        // When + Then
        InventoryException exception = assertThrows(InventoryException.class, () ->
                inventoryReceiptServices.createReceipt(receiptReqDTO));

        assertEquals("PURCHASE_ORDER_NOT_EXIST", exception.getCodeMessage());
        verify(inventoryReceiptRepository, times(1))
                .existsByPurchaseOrderCodeAndApproveIn(any(String.class), any(List.class));
        verify(purchaseOrderRepository, times(1))
                .findByCode("PO123");
    }
}