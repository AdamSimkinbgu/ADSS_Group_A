package PresentationLayer;

import java.util.List;
import DomainLayer.Classes.Agreement;
import ServiceLayer.AgreementService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

/**
 * Controller for Agreement-related commands.
 */
public class AgreementController extends AbstractController {
   public AgreementController(View view, AgreementService agreementService) {
      super(view, agreementService);
      this.implemented = true;
      controllerMenuOptions.put("1", this::createAgreement);
      controllerMenuOptions.put("2", this::updateAgreement);
      controllerMenuOptions.put("3", this::deleteAgreement);
      controllerMenuOptions.put("4", this::viewAgreement);
      controllerMenuOptions.put("5", () -> System.out.println("Returning to the main menu..."));
      controllerMenuOptions.put("?", () -> System.out.println("Invalid choice. Please try again."));
   }

   @Override
   public List<String> showMenu() {
      return List.of(
            "Please choose an option:",
            "Create Agreement",
            "Update Agreement",
            "Delete Agreement",
            "View Agreement",
            "Back to Main Menu");
   }

   public void createAgreement() {
      view.showMessage("Creating a new agreement...");
      List<String> params = view.readParameters(
            "Please enter agreement details: <field1>-<field2>-...");
      String json = fuseClassAttributesAndParametersToJson(Agreement.class, params);
      view.dispatchResponse(
            handleModuleCommand("addAgreement", json),
            Agreement.class);
   }

   public void updateAgreement() {
      view.showMessage("Updating an existing agreement...");
      view.showMessage("Enter Agreement ID to update:");
      String id = view.readLine();
      view.showMessage("Which field to update?");
      String field = view.readLine();
      view.showMessage("Enter new value:");
      String value = view.readLine();
      String updateJson = String.format(
            "{\"agreementId\":\"%s\", \"%s\":\"%s\"}",
            id, field, value);
      view.dispatchResponse(
            handleModuleCommand("updateAgreement", updateJson),
            Agreement.class);
   }

   public void deleteAgreement() {
      view.showMessage("Deleting an existing agreement...");
      view.showMessage("Enter Agreement ID to delete:");
      String id = view.readLine();
      String response = handleModuleCommand("removeAgreement", id);
      view.dispatchResponse(response, Boolean.class);
   }

   public void viewAgreement() {
      view.showMessage("Viewing an existing agreement...");
      view.showMessage("Enter Agreement ID to view:");
      String id = view.readLine();
      view.dispatchResponse(
            handleModuleCommand("getAgreement", id),
            Agreement.class);
   }
}