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
      View view; // fake console
      @Mock
      SupplierService service; // fake backend
      @InjectMocks
      CreateSupplierCMD cmd; // unit under test

      @Test
      void happy_path() {
            SupplierDTO dto = dummyDto_GoodInput();
            when(service.createSupplier(dto))
                        .thenReturn(ServiceResponse.ok(any())); // mock service response

            cmd.execute(dto); // act

            verify(service).createSupplier(dto); // behaviour assertion
            verify(view).showMessage(contains("success"));
            verify(view, never()).showError(toString());
      }

      private SupplierDTO dummyDto_GoodInput() {
            return new SupplierDTO(
                        "ACME",
                        "123",
                        new AddressDTO("Main St", "TLV", "12345"),
                        true,
                        EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                        0, // no supplier ID for creation
                        new PaymentDetailsDTO("1234-5678-9012-3456", PaymentMethod.CREDIT_CARD,
                                    PaymentTerm.N30),
                        List.of(), // contacts
                        List.of(), // products
                        List.of() // agreements
            );
      }

      @Test
      void bad_input() {
            SupplierDTO dto = dummyDto_BadInput();
            when(service.createSupplier(dto))
                        .thenReturn(ServiceResponse.fail(List.of("Invalid input"))); // mock service

            cmd.execute(dto); // act

            verify(service).createSupplier(dto); // behaviour assertion
            verify(view).showError(contains("Invalid input"));
            verify(view, never()).showMessage(toString());
      }

      private SupplierDTO dummyDto_BadInput() {
            return new SupplierDTO(
                        "", // empty name
                        "123", // Invalid phone
                        new AddressDTO("Main St", "TLV", "12345"), // valid address
                        true, // active
                        EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), // valid days
                        0, // no supplier ID for creation
                        new PaymentDetailsDTO("1234-5678-9012-3456", PaymentMethod.CREDIT_CARD,
                                    PaymentTerm.N30), // valid payment details
                        List.of(), // contacts
                        List.of(), // products
                        List.of() // agreements
            );
      }
}
