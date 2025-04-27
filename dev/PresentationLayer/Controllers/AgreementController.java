package PresentationLayer.Controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.Classes.Agreement;
import DomainLayer.Enums.WeekofDay;
import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.AgreementService;

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
      controllerMenuOptions.put("5", this::listAllAgreements);
      controllerMenuOptions.put("6", () -> System.out.println("Returning to the main menu..."));
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
            "List All Agreements",
            "Back to Main Menu");
   }

   public void createAgreement() {
      view.showMessage("Creating a new agreement... Please enter the following details:");

      ObjectNode payload = mapper.createObjectNode();
      String supplierId = view.readLine("Supplier ID:");
      payload.put("supplierId", supplierId);
      Boolean selfSupply = requestBoolean("Is this a self-supply agreement (true/false):");
      payload.put("selfSupply", selfSupply);

      ArrayNode daysArray = payload.putArray("supplyDays");
      // askForSupplyDays() -> Stream<WeekofDay> -> map each day to its String name
      // -> collect into a Jackson ArrayNode -> daysArray.addAll(that ArrayNode)
      daysArray.addAll(askForSupplyDays().stream()
            .map(WeekofDay::name)
            .collect(mapper::createArrayNode, ArrayNode::add, ArrayNode::addAll));
      payload.put("agreementStartDate",
            askForFutureOrTodayDate("Enter agreement start date").toString());
      payload.put("agreementEndDate",
            askForFutureOrTodayDate("Enter agreement end date").toString());
      Boolean hasFixedSupplyDays = requestBoolean("Does this agreement have fixed supply days (true/false):");
      payload.put("hasFixedSupplyDays", hasFixedSupplyDays);
      String response = handleModuleCommand("addAgreement", payload.toString());
      view.dispatchResponse(response, Agreement.class);
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

   public void listAllAgreements() {
      view.showMessage("Listing all agreements...");
      view.dispatchResponse(
            handleModuleCommand("listAllAgreements", ""),
            Agreement[].class);
   }

   private LocalDate askForFutureOrTodayDate(String prompt) {
      DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
      while (true) {
         view.showMessage(prompt + " (format YYYY-MM-DD, must be today or later):");
         String raw = view.readLine().trim();
         try {
            LocalDate entered = LocalDate.parse(raw, fmt);

            if (entered.isBefore(LocalDate.now())) {
               view.showMessage("The date cannot be in the past. Please enter today’s date or later.");
               continue;
            }

            return entered;

         } catch (DateTimeParseException ex) {
            view.showMessage("Invalid format. Please use YYYY-MM-DD, e.g. 2025-04-15.");
         }
      }
   }

   private EnumSet<WeekofDay> askForSupplyDays() {
      EnumSet<WeekofDay> supplyDays = EnumSet.noneOf(WeekofDay.class);
      String userInput = "";
      while (userInput == null || userInput.isEmpty()) {
         userInput = view
               .readLine(
                     "Enter delivery day (e.g. Monday/monday/MONDAY), then enter 'done' (To deselect, enter the day again):")
               .trim().toUpperCase();
         if (userInput.equals("DONE")) {
            view.showMessage("Selected days: " + supplyDays);
            break;
         }
         try {
            WeekofDay day = WeekofDay.valueOf(userInput);
            if (!supplyDays.add(day)) {
               supplyDays.remove(day);
               view.showMessage(day + " removed. Current days: " + supplyDays);
            } else {
               view.showMessage(day + " added. Current days: " + supplyDays);
            }
         } catch (IllegalArgumentException e) {
            view.showMessage("Invalid entry. Please enter a valid weekday or DONE.");
         }
      }

      return supplyDays;
   }
}