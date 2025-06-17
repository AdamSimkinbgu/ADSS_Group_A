package Suppliers.DataLayer.Interfaces;

import java.util.List;

import Suppliers.DTOs.BillofQuantitiesItemDTO;

public interface BillofQuantitiesItemDAOInterface {

   BillofQuantitiesItemDTO createBillofQuantitiesItem(BillofQuantitiesItemDTO item);

   boolean updateBillofQuantitiesItem(BillofQuantitiesItemDTO item);

   List<BillofQuantitiesItemDTO> getBillofQuantitiesItemsById(int lineId);

   boolean deleteBillofQuantitiesItem(int agreementId, int lineId);

   boolean deleteAllBillofQuantitiesItems(int id);

   List<BillofQuantitiesItemDTO> getAllBillofQantitiesItemsForAgreementId(int agreementId);

}
