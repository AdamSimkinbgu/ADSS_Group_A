package DataAccessLayer.SuppliersDAL.Interfaces;

import java.util.List;

import DTOs.SuppliersModuleDTOs.BillofQuantitiesItemDTO;

public interface BillofQuantitiesItemDAOInterface {

   BillofQuantitiesItemDTO createBillofQuantitiesItem(BillofQuantitiesItemDTO item);

   boolean updateBillofQuantitiesItem(BillofQuantitiesItemDTO item);

   List<BillofQuantitiesItemDTO> getBillofQuantitiesItemsById(int lineId);

   boolean deleteBillofQuantitiesItem(int agreementId, int lineId);

   boolean deleteAllBillofQuantitiesItems(int id);

   List<BillofQuantitiesItemDTO> getAllBillofQantitiesItemsForAgreementId(int agreementId);

}
