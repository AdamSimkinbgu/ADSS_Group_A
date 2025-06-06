package Tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.PaymentDetailsDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.Enums.PaymentMethod;
import Suppliers.DTOs.Enums.PaymentTerm;
import Suppliers.PresentationLayer.View;
import Suppliers.PresentationLayer.Commands.SupplierCommands.CreateSupplierCMD;
import Suppliers.ServiceLayer.SupplierService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

@ExtendWith(MockitoExtension.class)
class CreateSupplierCmdTest {

      @Mock
      private View view;

      @Mock
      private SupplierService supplierService;
      @InjectMocks
      private CreateSupplierCMD createSupplierCMD;

      // @Test
      // public void
      // createSupplier_AllFieldsValid_ShouldCallServiceAndDisplaySuccess() {
      // // Arrange
      // SupplierDTO supplier = new SupplierDTO(
      // -1,
      // "Test Supplier",
      // "512345678",
      // new AddressDTO("Test Street", "Test City", "12345"),
      // false,
      // EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
      // 3,
      // new PaymentDetailsDTO("420420", PaymentMethod.BANK_TRANSFER,
      // PaymentTerm.N30),
      // List.of(),
      // List.of(),
      // List.of());
      // ServiceResponse<SupplierDTO> response = new ServiceResponse<>(supplier,
      // List.of());
      // //
      // when(supplierService.createSupplier(any(SupplierDTO.class))).thenReturn(supplier);

      // // Act
      // createSupplierCMD.execute(supplier);

      // // Assert
      // verify(supplierService).createSupplier(any(SupplierDTO.class));
      // verify(view).showMessage(contains("Supplier created successfully"));
      // verify(view, never()).showError(any(String.class));

      // }
}
