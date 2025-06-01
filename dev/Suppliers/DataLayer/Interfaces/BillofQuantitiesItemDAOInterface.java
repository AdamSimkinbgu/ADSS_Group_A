package Suppliers.DataLayer.Interfaces;

import java.sql.SQLException;
import java.util.List;

import Suppliers.DTOs.BillofQuantitiesItemDTO;

public interface BillofQuantitiesItemDAOInterface {

   BillofQuantitiesItemDTO createBillofQuantitiesItem(BillofQuantitiesItemDTO item) throws SQLException;

   void updateBillofQuantitiesItem(BillofQuantitiesItemDTO item) throws SQLException;

   BillofQuantitiesItemDTO getBillofQuantitiesItemById(int agreementId, int lineId) throws SQLException;

   void deleteBillofQuantitiesItem(int agreementId, int lineId) throws SQLException;

   void deleteAllBillofQuantitiesItems(int id) throws SQLException;

   List<BillofQuantitiesItemDTO> getAllBillofQuantitiesItems(int agreementId) throws SQLException;

}
