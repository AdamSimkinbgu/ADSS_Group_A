package DomainLayer.Classes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import DTOs.Enums.*;
import DomainLayer.AgreementFacade;
import DomainLayer.SupplierFacade;
import ServiceLayer.SupplierService;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

class SupplierTest {
   private ObjectMapper mapper;
   private SupplierFacade supplierFacade;
   private AgreementFacade agreementFacade;

   @BeforeEach
   void setUp() {
      supplierFacade = new SupplierFacade(false, "");
      agreementFacade = new AgreementFacade();
      mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
   }

   @AfterEach
   void tearDown() {
      mapper = null;
      supplierFacade = null;
      agreementFacade = null;
   }

}
