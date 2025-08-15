package PresentationLayer.TransportPresentation;

import DTOs.TransportModuleDTOs.*;
import DomainLayer.enums.enumTranStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import ServiceLayer.TransportServices.SiteService;
import ServiceLayer.TransportServices.TransportService;
import ServiceLayer.TransportServices.TruckService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TranManCLI {
    private TruckService tru_ser;
    private TransportService tra_ser;
    private SiteService site_ser;
    private Scanner scanner;
    private ObjectMapper objectMapper;

    public TranManCLI(TruckService ts, TransportService trs, SiteService sis, Scanner sc, ObjectMapper oM) {
        this.tru_ser = ts;
        this.tra_ser = trs;
        this.site_ser = sis;
        this.scanner = sc;
        this.objectMapper = oM;
    }


    void transportManagerMainMenu(long loggedID) {   ////////////////////////////////   Main Menu   <<<--------------------------------------------
        while(true){
            System.out.println("\n       --------    Transport Manager Menu    -------");
            System.out.println("(1)  Transports Options Menu");
            System.out.println("(2)  Shipping Areas Options Menu");
            System.out.println("(3)  Sites Options Menu");
            System.out.println("(4)  Trucks Options Menu");
            System.out.println("(5)  View All Drivers");
            System.out.println("(6)  Go back to Welcoming Transport System Screen");
            System.out.println();
            System.out.println(" Select Options Menu: ");

            String choice = "6";
            try {
                choice = scanner.nextLine();
            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }


            switch (choice){
                case "1":
                    transportsOptionsMenu(loggedID);
                    break;
                case "2":
                    shippingAreasOptionsMenu(loggedID);
                    break;
                case "3":
                    sitesOptionsMenu(loggedID);
                    break;
                case "4":
                    trucksOptionsMenu(loggedID);
                    break;
                case "5":
                    showAllDrivers(loggedID);
                    break;
                case "6":
                    System.out.println("\nGoing Back to Welcoming Transport System Screen.\n");
                    return;
                default:
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                    break;
            }
        }
    }












    private void transportsOptionsMenu(long loggedID){   ////////////////////////////////   Transports Menu   <<<--------------------------------------------
        while (true){
            System.out.println("   --------    Transports Options Menu    -------\n");
            System.out.println("(1)  View All Transports");
            System.out.println("(2)  Create a Transport");
            System.out.println("(3)  Check if a Queued Transport Can Be Sent");
            System.out.println("(4)  Delete a Transport");
            System.out.println("(5)  Edit a Transport");
            System.out.println("(6)  Back to Transport Manager Main Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            String choice = "6";
            try {
                choice = scanner.nextLine();
            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }


            if(choice.equals("1")){
                showAllTransports(loggedID);
            }else if(choice.equals("2")){
                createaTransportMenu(loggedID);
            }else if(choice.equals("3")){
                checkIfQueuedTransportCanBeSent(loggedID);
            } else if (choice.equals("4")) {
                deleteaTransportMenu(loggedID);
            } else if (choice.equals("5")) {
                editaTransportMenu(loggedID);
            } else if (choice.equals("6")) {
                System.out.println("\n\n");
                return;
            } else {
                System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
            }
        }
    }

    //////////////////////          HELPER FUNCTIONS FOR THE Transports Options Menu

    private void showAllTransports(long loggedID){
        System.out.println("   --------    Showing All Transports    --------\n");
        System.out.println(tra_ser.showAllTransports(loggedID));
        System.out.println();
    }





    private void createaTransportMenu(long loggedID) {

        System.out.println("   --------    Transport Creation    --------\n");
        System.out.println("Ok, let's start creating your new Transport :)");
        System.out.println("\n- Enter the following information:");


        int truckNum = 402;
        int driverID = 0;
        SiteDTO srcSitedto = null;
        ArrayList<ItemsDocDTO> dests_Docs_for_Transport = new ArrayList<>();
        LocalDateTime selectedDepartureDT = LocalDateTime.now();

        try {
            System.out.println("Enter Source Area Number: ");
            int sourceAreaNum = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline
            System.out.println("Enter Source Address String: ");
            String sourceAddressString = scanner.nextLine();

            boolean siteExists1 = this.site_ser.doesSiteExist(loggedID, sourceAreaNum, sourceAddressString);
            while (!siteExists1){
                System.out.println("Site Doesn't Exist, please choose a site that actually exists.\n");
                System.out.println("Enter Source Area Number:");
                sourceAreaNum = scanner.nextInt();
                scanner.nextLine(); // consume the leftover newline
                System.out.println("Enter Source Address String:");
                sourceAddressString = scanner.nextLine();
                siteExists1 = this.site_ser.doesSiteExist(loggedID, sourceAreaNum, sourceAddressString);
            }
            srcSitedto = new SiteDTO(sourceAreaNum, sourceAddressString);


            System.out.println("Enter Desired Truck Number: ");
            truckNum = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline
            System.out.println("Enter Desired Driver ID: ");
            driverID = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline

            ///   checking the Truck-Driver pairing as the first check
            String resForNow = this.tra_ser.isTruckDriverPairingGood(loggedID, truckNum, driverID);   //  first check
            if(resForNow.equals("Success")){
                System.out.println("The Truck-Driver pairing you chose is Compatible and Available right now :)\n");
            } else if(resForNow.equals("Exception")){
                System.out.println("Exception due to technical machine error\n");
            } else { System.out.println(resForNow + "\n"); }

            if (!resForNow.equals("Success")){
                System.out.println("The Truck-Driver pairing you chose is not possible right now.");
                System.out.println("Tip: Use the Menu to View All Trucks and All Drivers, so You'd find a matching pair for you're Transport");

                System.out.println("Would you like to continue setting up the Sites and Items for your Transport and configure a Truck-Driver pairing later ? (Y/N(or any other key))");
                String choice = scanner.nextLine();
                if(!choice.equals("Y")){
                    System.out.println("returning to Transports Options Menu...\n");
                    return;
                } else {
                    System.out.println("Okay, continuing Transport creation with that Truck-Driver pairing for now...\n");
                }
            }



//        boolean validTime = false, validPick = false;
//        LocalDateTime selectedDepartureDT = null;
//        while (!validPick){
//            System.out.println("Ok, when do you want to your new Transport to depart the source site ?\n");
//            System.out.println(".1. Now.");
//            System.out.println(".2. Let me pick a future time.");
//            System.out.println("enter your selection:");
//            int pick = scanner.nextInt();
//            scanner.nextLine(); // consume the leftover newline
//
//            if (pick == 1){
//                selectedDepartureDT = LocalDateTime.now();
//                validPick = true;
//            } else if (pick == 2){
//                System.out.println("Ok, let's pick a future time for your Transport to depart the source site:");
//                while (!validTime){
//                    System.out.println("Year:");
//                    int Syear = scanner.nextInt();
//                    scanner.nextLine(); // consume the leftover newline
//                    System.out.println("Month:");
//                    int Smonth = scanner.nextInt();
//                    scanner.nextLine(); // consume the leftover newline
//                    System.out.println("Day:");
//                    int Sday = scanner.nextInt();
//                    scanner.nextLine(); // consume the leftover newline
//                    System.out.println("Hour:");
//                    int Shour = scanner.nextInt();
//                    scanner.nextLine(); // consume the leftover newline
//                    System.out.println("Minute:");
//                    int Sminute = scanner.nextInt();
//                    scanner.nextLine(); // consume the leftover newline
//                    selectedDepartureDT = LocalDateTime.of(Syear, Smonth, Sday, Shour, Sminute);
//
//                    if (selectedDepartureDT.isBefore(LocalDateTime.now())){
//                        System.out.println("You cannot choose a departure time in the past :(  try again:\n");
//                    } else {
//                        validTime = true;
//                    }
//                }
//                validPick = true;
//            } else {
//                System.out.println("--->  Please enter a number between the menu's margins  <---  try again:\n");
//            }
//        }

            selectedDepartureDT = LocalDateTime.now();

            ///  Starting with the Sites and Items for the Transport
            dests_Docs_for_Transport = new ArrayList<ItemsDocDTO>();  //  for the Transport's field

            System.out.println("We'll now add the Sites(and the items for each site).");
            System.out.println("\nThe Transport's Site Arrival Order will be based on the order of Sites you add (first added -> first arrived to)");
            System.out.println("\nNote: Destination sites can only be Branches of Super Lee.");             ///   Added according to requirements.   <<----------
            System.out.println("\n- Now Enter Each Site and for Each Site enter the Items for that site.");

            boolean continueAnotherSite = true, continueAnotherItem = true, continueAskingDifferentAreaNum = true, siteExists = false;
            boolean destIsBranch = false;
            Integer currSiteAreaNum = -99;
            String currDestinationAddress = "";
            ArrayList<Integer> areasNumsUptoNow = new ArrayList<Integer>();  //  for the area numbers in this Transport
            ArrayList<Integer> ItemsDocsNumsUsed = new ArrayList<>();  // for the items Docs numbers in this Transport
            areasNumsUptoNow.add(sourceAreaNum);
            LocalDateTime timeCounter = selectedDepartureDT.plusMinutes(0);    //  to create a copy

            while (continueAnotherSite){   ///   Sites WHILE(TRUE) LOOP
                siteExists = false;
                destIsBranch = false;
                while (!siteExists || !destIsBranch){
                    continueAskingDifferentAreaNum = true;
                    while (continueAskingDifferentAreaNum){
                        System.out.println("Enter Destination Site Area Number: ");
                        currSiteAreaNum = scanner.nextInt();
                        scanner.nextLine(); // consume the leftover newline
                        if(!areasNumsUptoNow.contains(currSiteAreaNum)){
                            if(areasNumsUptoNow.isEmpty()){   //  if first site
                                areasNumsUptoNow.add(currSiteAreaNum);
                                continueAskingDifferentAreaNum = false;
                                continue;  // will break
                            }
                            System.out.println("The destination's area number is not within the area numbers in this Transport's destinations, continue with it ? ( Enter 'Y' / 'N'(or any other key) )");
                            String ifAnotherSiteChoice = scanner.nextLine();
                            if(!ifAnotherSiteChoice.equals("Y")){
                                continue;  ///  continue this Loop
                            }
                            areasNumsUptoNow.add(currSiteAreaNum);
                            timeCounter = timeCounter.plusHours(1);
                            continueAskingDifferentAreaNum = false;  ///  breaks from this Loop
                        }else {
                            timeCounter = timeCounter.plusMinutes(30);
                            continueAskingDifferentAreaNum = false;  ///  breaks from this Loop
                        }
                    }

                    System.out.println("Enter Destination Site Address String: ");
                    currDestinationAddress = scanner.nextLine();

                    siteExists = this.site_ser.doesSiteExist(loggedID, currSiteAreaNum, currDestinationAddress);
                    destIsBranch = this.tra_ser.isBranch(currDestinationAddress, currSiteAreaNum);
                    if (!siteExists){
                        areasNumsUptoNow.remove(currSiteAreaNum);
                        System.out.println("Site Doesn't Exist, please choose a site that actually exists.\n");
                    } else if (!destIsBranch) {
                        areasNumsUptoNow.remove(currSiteAreaNum);
                        System.out.println("The Destination Site you chose is not a Branch, please choose a destination site that is a Branch.\n");
                    } else {  // siteExists & destIsBranch
                        break;
                    }
                }


                SiteDTO destSitedto = new SiteDTO(currSiteAreaNum, currDestinationAddress);

                System.out.println("\n- Now let's add the Items for that Destination Site:\n");

                ArrayList<ItemQuantityDTO> itemsListToCurrDestSite = new ArrayList<>();  // for the ItemsDoc's field

                System.out.println("Enter Unique Items Document Number: ");
                int currItemsDocNum = scanner.nextInt();
                scanner.nextLine(); // consume the leftover newline
                while ((!tra_ser.checkValidItemsDocID(loggedID, currItemsDocNum)) || ItemsDocsNumsUsed.contains(currItemsDocNum)){
                    System.out.println("Please Enter a *Unique* and Valid Items Document Number: ");
                    currItemsDocNum = scanner.nextInt();
                    scanner.nextLine(); // consume the leftover newline
                }
                ItemsDocsNumsUsed.add(currItemsDocNum);
                System.out.println("Valid Items Document Number :)\n");

                System.out.println("Now let's add the Items you want for this destination Site, one by one:");
                continueAnotherItem = true;
                while (continueAnotherItem){   ///   site's Items WHILE(TRUE) LOOP
                    System.out.println("Enter Item Name: ");
                    String itemName = scanner.nextLine();
                    System.out.println("Enter Item Weight: ");
                    double itemWeight  = scanner.nextDouble();
                    System.out.println("Enter Item Amount: ");
                    int itemAmount = scanner.nextInt();
                    scanner.nextLine(); // consume the leftover newline
                    System.out.println("Enter these Items Condition: ( Enter 'Good' / 'Bad'(or any other key) )");
                    String condition = scanner.nextLine();

                    ItemDTO itemAddition = new ItemDTO(itemName, itemWeight, condition.equals("Good"));

                    boolean itemExistsInCurrDestSite = false;
                    for (ItemQuantityDTO itemQuantityDTO : itemsListToCurrDestSite){
                        if (itemQuantityDTO.getItem().equals(itemAddition)){
                            itemExistsInCurrDestSite = true;
                            itemQuantityDTO.setQuantity(itemQuantityDTO.getQuantity() + itemAmount);
                            break;
                        }
                    }
                    if (!itemExistsInCurrDestSite){
                        itemsListToCurrDestSite.add(new ItemQuantityDTO(currItemsDocNum, itemAddition, itemAmount));
                    }

                    System.out.println("Item Added to listed items associated with current Site, do you want to add another Item ? ( Enter 'Y' / 'N'(or any other key) )");
                    String ifAnotherItemChoice = scanner.nextLine();
                    if(!ifAnotherItemChoice.equals("Y")){ continueAnotherItem = false; }  ///  breaks from this Item Addition Loop
                }



                ItemsDocDTO itemsDocAddition = new ItemsDocDTO(currItemsDocNum, srcSitedto, destSitedto, itemsListToCurrDestSite, timeCounter, -99);
                System.out.println("Ok, Finished adding the current destination Site's items");
                dests_Docs_for_Transport.add(itemsDocAddition);   //  adding new ItemsDoc to the destSitesDocs

                System.out.println("Items for that Site added.\nDo you want to Add another Site and it's Items ? ( Enter 'Y' / 'N'(or any other key) )");
                String choice = scanner.nextLine();
                if(!choice.equals("Y")){
                    continueAnotherSite = false;  ///  breaks from this Site Addition Loop
                }
            }
        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }




        System.out.println("Ok, Finished adding the Sites & Items to the Transport");

        // And create the DTO object (The Package to send downwards):
        TransportDTO transportDTO = new TransportDTO(-99, truckNum, driverID, srcSitedto, dests_Docs_for_Transport, selectedDepartureDT, enumTranStatus.BeingAssembled, 0, new ArrayList<>());

        ////////////////////////////////////////////////    NOW WE HAVE THE WHOLE TRANSPORT's DTO     <<<-----------------------------------------


        /// ////////////////////////////////////////////    NOW WE'LL DO THE CHECKS          <<<-----------------------------------------

        String resValid = checkIfTransportDTOIsValid(loggedID, transportDTO);

        if (resValid.equals("Valid")){
            System.out.println("Okay, Transport is Valid :)");

            String resOfNewTransportAddition = "";
            try {
                resOfNewTransportAddition = this.tra_ser.createTransport(loggedID, objectMapper.writeValueAsString(transportDTO), -100);  /// <<------  HERE WE CREATE THE TRANSPORT AFTER THE CHECKS
            } catch (Exception e) {
                System.out.println("Serialization's fault");
                e.printStackTrace();
            }

            if(resOfNewTransportAddition.equals("Success")){
                System.out.println("Successfully Added Transport, You can view the Transport's Details (and it's given ID) using the Menu.\n");
            } else if(resOfNewTransportAddition.equals("Exception")){
                System.out.println("Failed to add Transport due to technical machine error\n");
            }else { System.out.println(resOfNewTransportAddition + "\n"); }
        }

        System.out.println();
    }





    private void transportPairingRePlanning(long loggedID, TransportDTO transportDTO) {
        //  Note: if we got to here it seems there is a possible pairing in the system right now. (because we didn't automatically go to the "Queue")
        System.out.println("The Good news is that we've Detected that a Compatible Driver-Truck Pairing is Available, try and choose a matching pair:");
        System.out.println("These are the available Trucks and the available Drivers, from them, let's choose a new Truck-Driver pairing for your Transport:\n");
        System.out.println("Available Trucks:\n" + this.tru_ser.showTrucks(loggedID) + "\n");
///        System.out.println("Available Drivers: \n" + this.emp_ser.showDrivers() + "\n");    ////   commented

        int truckNum = 0;
        int driverID = 0;

        try {
            showAllDrivers(loggedID);
            System.out.println("Please Enter the New Truck-Driver pairing you want:");
            System.out.println("Enter Truck number:");
            truckNum = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter Driver ID:");
            driverID = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }

        transportDTO.setTransportTruckNum(truckNum);
        transportDTO.setTransportDriverID(driverID);
        // and then it will check validity again in the caller function
    }




    private void transportWeightRePlanning(long loggedID, TransportDTO transportDTO, String issue){
        System.out.println("The problem seems to be that the Transport's overall weight exceeds the feasible possible weight that can travel on this Truck");

        String[] parts = issue.split("-");
        double overallWeight = Double.parseDouble(parts[0]);
        double truckMaxCarryWeight = Double.parseDouble(parts[1]);

        boolean stillLoopingInReplanningChiceLoop = true;
        while (stillLoopingInReplanningChiceLoop){
            System.out.println("The current overall weight of the truck is: " + overallWeight + ", and the truck's Max. Carry Weight is: " + truckMaxCarryWeight);
            System.out.println("Please Choose an Option from the Possible RePlanning Options below:");
            System.out.println("(1)  Remove Items for a specific destination Site from the Transport");
            System.out.println("(2)  Remove a Destination Site from the Transport (and all of it's Items)");
            System.out.println("(3)  Choose a Different Truck for the Transport");
            System.out.println(" Enter your choice: ");

            String choice = "";
            try {
                choice = scanner.nextLine();

                if(choice.equals("1")){
                    System.out.println("Here are all the Site Items in the Transport:");
                    System.out.println(transportDTO.showAllTransportItemsDocs());

                    boolean stillSearching = true;
                    while (stillSearching){
                        System.out.println("\nLet's now Remove an Item from an existing Transport:");
                        System.out.println("Enter the Item's Site's area number: ");
                        int itemsSiteArea = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter the Item's Site's address string: ");
                        String itemsSiteAddress = scanner.nextLine();
                        System.out.println("Enter the Item's name:");
                        String itemsName = scanner.nextLine();
                        System.out.println("Enter the Item's weight:");
                        double itemsWeight = Double.parseDouble(scanner.nextLine());
                        System.out.println("Enter the condition of the Item you want to remove: ( ( 'Good' ) / ( 'Bad'(or any other key) )");
                        boolean condition = scanner.nextLine().equals("Good");
                        System.out.println("Enter the Item's amount you want to remove:");
                        int itemsAmountToRemove = Integer.parseInt(scanner.nextLine());

                        ItemQuantityDTO itemToRemove = null;
                        boolean searchingItemToRemove = true;
                        for (ItemsDocDTO itemsDocDTO : transportDTO.getDests_Docs()){
                            if (itemsDocDTO.getDest_siteDTO().getSiteAreaNum() == itemsSiteArea && itemsDocDTO.getDest_siteDTO().getSiteAddressString().equals(itemsSiteAddress)){
                                // found the Site from which we will deduct the item's amount
                                for (ItemQuantityDTO itemQuantityDTO : itemsDocDTO.getItemQuantityDTOs()){
                                    if (itemQuantityDTO.getItem().getName().equals(itemsName) && itemQuantityDTO.getItem().getWeight() == itemsWeight && itemQuantityDTO.getItem().getCondition() == condition){
                                        stillSearching = false;  // because found
                                        if(itemQuantityDTO.getQuantity() <= itemsAmountToRemove){
                                            itemToRemove = itemQuantityDTO;  // remove after the break
                                            searchingItemToRemove = false;  //  because found
                                        } else {
                                            itemQuantityDTO.setQuantity(itemQuantityDTO.getQuantity() - itemsAmountToRemove);
                                        }
                                        break;  // because found
                                    }
                                }
                                if (!stillSearching){  // so found item to remove amount from
                                    if(!searchingItemToRemove){  // because item to remove completely was found
                                        itemsDocDTO.getItemQuantityDTOs().remove(itemToRemove);
                                    }
                                    break;  //  because found already
                                }
                            }
                        }

                        if (stillSearching){
                            System.out.println("couldn't find any item matching the Details you've put in, try again.\n");
                        }
                    }

                    System.out.println("Removed Entered Item's desired quantity from this Transport.");
                    stillLoopingInReplanningChiceLoop = false;


                }else if(choice.equals("2")){
                    System.out.println("Here are all the Sites and their Items in the Transport:");
                    System.out.println(transportDTO.showAllTransportItemsDocs());

                    System.out.println("Ok, let's remove a destination Site from this Transport.");
                    System.out.println("Enter the area number of the Destination Site to remove: ");
                    int areaNum = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter the address string of the Destination Site to remove: ");
                    String address = scanner.nextLine();

                    int indexToRemoveFromItemsDocs = 0;
                    for (ItemsDocDTO itemsDocDTO : transportDTO.getDests_Docs()){
                        if (itemsDocDTO.getDest_siteDTO().getSiteAreaNum() == areaNum && itemsDocDTO.getDest_siteDTO().getSiteAddressString().equals(address)){ break ;}
                        indexToRemoveFromItemsDocs++;
                    }
                    transportDTO.getDests_Docs().remove(indexToRemoveFromItemsDocs);
                    System.out.println("Removed Entered Destination Site from this Transport (and all of it's items).");
                    stillLoopingInReplanningChiceLoop = false;


                }else if(choice.equals("3")){
                    System.out.println("These are the available Trucks in the WareHouse, let's choose a new Truck for your Transport:\n");
                    System.out.println("Available Trucks:\n" + this.tru_ser.showTrucks(loggedID) + "\n");
                    System.out.println("Please Enter the New Truck-Driver pairing you want:");
                    System.out.println("Please Enter desired Truck number:");
                    int truckNum = Integer.parseInt(scanner.nextLine());
                    transportDTO.setTransportTruckNum(truckNum);
                    System.out.println("Changed Transport Truck");
                    stillLoopingInReplanningChiceLoop = false;


                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                    //  not changing stillLoopingInReplanningChoiceLoop (boolean) so it remains true and will ask for an option choice again.
                }
            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
        // and then it will check validity again in the caller function
    }









    private String checkIfTransportDTOIsValid(long loggedID, TransportDTO transportDTO){
        System.out.println("Checking Transport Validity...");

        String resOfTransportCheck = "";
        try {
            resOfTransportCheck = this.tra_ser.checkTransportValidity(loggedID, objectMapper.writeValueAsString(transportDTO));  //  check Transport Validity
            ///  returns: "Valid", "BadLicenses", "overallWeight-truckMaxCarryWeight", "Queue", "Occupied", "WareHouseManUnavailable", "DriverUnavailable"
        } catch (Exception e) {
            System.out.println("Serialization's fault");
            e.printStackTrace();
        }

        while (!resOfTransportCheck.equals("Valid")){
            System.out.println("There seems There is a problem with The Transport you're trying to create:\n");
            if (resOfTransportCheck.equals("Queue")){
                System.out.println("This Transport doesn't have a proper Truck-Driver matching available at all right now.");
                System.out.println("If this is a new Transport, this Transport will now go automatically into the Queued Transports.");
                System.out.println("If it's an already Queued Transport that is being checked again, it will remain where it is in the Queued Transports.");
                System.out.println("We save the Queued Transports in order of creation, so when you want to, you can go to (Transports Options Menu)->((3)  Check if a Queued Transport Can Be Sent) ");
                System.out.println("And try to send out a Queued Transport, you can of course also delete that transport using the Menu.\n");
                break;

            } else if (resOfTransportCheck.equals("Occupied")){
                System.out.println("The Driver or/and the Truck you designated for this Transport are Occupied with another Active Transport/s");
                transportPairingRePlanning(loggedID, transportDTO);

            } else if (resOfTransportCheck.equals("BadLicenses")) {
                System.out.println("The Driver you designated doesn't have a License that matches the License required for the Truck you selected");
                transportPairingRePlanning(loggedID, transportDTO);

            } else if (resOfTransportCheck.equals("WareHouseManUnavailable")) {
                System.out.println("It seems that at least one of the Sites is missing a WareHouse Man Employee at the time the Transport is visiting that site, can't handle the load this way.");
                System.out.println("\nThis Transport is going to the Queued Transport, where it is saved, you can change it's details or wait for the right moment and try to send this Transport again later");
                break;
            } else if (resOfTransportCheck.equals("DriverUnavailable")) {
                System.out.println("It seems that the Driver you chose isn't at any of the sites associated in this Transport at this time, so we're missing a Legitimate Driver.");
                System.out.println("\nThis Transport is going to the Queued Transport, where it is saved, you can change it's details or wait for the right moment and try to send this Transport again later");
                break;
            } else {    ///  "overallWeight-truckMaxCarryWeight" Case
                transportWeightRePlanning(loggedID, transportDTO, resOfTransportCheck);
            }

            System.out.println("Okay, Let's Check Transport Validity again...");
            try {
                resOfTransportCheck = this.tra_ser.checkTransportValidity(loggedID, objectMapper.writeValueAsString(transportDTO));  //  check Transport Validity again
                ///  returns: "Valid", "BadLicenses", "overallWeight-truckMaxCarryWeight", "Queue", "Occupied", "WareHouseManUnavailable", "DriverUnavailable"
            } catch (Exception e) {
                System.out.println("Serialization's fault");
                e.printStackTrace();
            }
        }
        return resOfTransportCheck;

    }






    private void checkIfQueuedTransportCanBeSent(long loggedID){
        while (true){
            System.out.println("   --------    Transport Queue Checkup    --------\n");
            System.out.println("(1)  View All Queued Transports");
            System.out.println("(2)  Try Initiating a Queued Transport");
            System.out.println("(3)  Back to Transports Options Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            try {
                String choice = scanner.nextLine();

                if(choice.equals("1")){
                    System.out.println(this.tra_ser.showAllQueuedTransports(loggedID));


                }else if(choice.equals("2")){
                    System.out.println("These are all of the Queued Transports, choose one you want to try to Initiate:");
                    System.out.println(this.tra_ser.showAllQueuedTransports(loggedID));
                    System.out.println("\nEnter your choice");
                    int choiceInt = Integer.parseInt(scanner.nextLine());

                    String resTransportDTOAsJson = this.tra_ser.getAQueuedTransportAsDTOJson(loggedID, choiceInt);

                    if (resTransportDTOAsJson.equals("index")){
                        System.out.println("The index you've entered in invalid. (it's above the last index)\n");
                    } else if (resTransportDTOAsJson.equals("Json")) {
                        System.out.println("JsonProcessingException\n");
                    } else if (resTransportDTOAsJson.equals("noQueued")) {
                        System.out.println("There are no Queued Transports.\n");
                    } else if (resTransportDTOAsJson.equals("Exception")) {
                        System.out.println("machine error Exception\n");
                    }else {   /// if got a TransportDTOAsJson result

                        TransportDTO transport_DTO = null;
                        try {
                            transport_DTO = this.objectMapper.readValue(resTransportDTOAsJson, TransportDTO.class);
                        } catch (Exception e) {
                            System.out.println("Serialization's fault");
                            e.printStackTrace();
                        }

                        String resValid = checkIfTransportDTOIsValid(loggedID, transport_DTO);   //  checking loop function

                        if (resValid.equals("Valid")){
                            System.out.println("Hurray, the Queued Transport you chose is now Valid :)");  //  because got to this line

                            String resOfNewTransportAddition = "";
                            try {
                                resOfNewTransportAddition = this.tra_ser.createTransport(loggedID, objectMapper.writeValueAsString(transport_DTO), choiceInt);  /// <<------  HERE WE CREATE THE TRANSPORT AFTER THE CHECKS
                            } catch (Exception e) {
                                System.out.println("Serialization's fault");
                                e.printStackTrace();
                            }

                            if(resOfNewTransportAddition.equals("Success")){
                                System.out.println("Successfully Added Transport, You can view the Transport's Details (and it's given ID) using the Menu.\n");
                            } else if(resOfNewTransportAddition.equals("Exception")){
                                System.out.println("Failed to add Transport due to technical machine error\n");
                            }else { System.out.println(resOfNewTransportAddition + "\n"); }
                        }
                    }



                }else if (choice.equals("3")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }


        }
    }






    private void deleteaTransportMenu(long loggedID){
        System.out.println("   --------    Transport Deletion    --------\n");
        System.out.println("Let's delete a Transport, Enter the Transport's ID (can be seen with the option, in the Menu, to show all Transports):");

        int transportId = 0;
        try {
            transportId = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }

        String res = this.tra_ser.deleteTransport(loggedID, transportId);
        if(res.equals("Success")){
            System.out.println("Successfully Deleted Transport.\n");
        } else if(res.equals("Exception")){
            System.out.println("Failed to delete Transport due to technical machine error\n");
        }else { System.out.println(res + "\n"); }

        System.out.println();
    }





















    private void editaTransportMenu(long loggedID){
        while (true){
            System.out.println("   --------    Transport Edition Menu    --------\n");
            System.out.println("(1)  Edit a Transport's Status");
            System.out.println("(2)  Edit a Transport's Problems");
            System.out.println("(3)  Edit a Transport's Sites");
            System.out.println("(4)  Edit a Transport's Items");
            System.out.println("(5)  Edit a Transport's Driver/Truck");
            System.out.println("(6)  Back to Transports Options Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            String choice = "";
            try {
                choice = scanner.nextLine();
            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }


            if(choice.equals("1")){
                editATransportsStatus(loggedID);
            }else if(choice.equals("2")){
                editATransportsProblems(loggedID);
            }else if(choice.equals("3")){
                editATransportsSites(loggedID);
            } else if (choice.equals("4")) {
                editATransportsItems(loggedID);
            } else if (choice.equals("5")) {
                editATransportsDriverOrTruck(loggedID);
            } else if (choice.equals("6")) {
                System.out.println("\n\n");
                return;
            } else {
                System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
            }
            System.out.println();
        }
    }






    private void editATransportsStatus(long loggedID){
        while (true){
            System.out.println("   --------    Transport's Status Edition    --------\n");
            System.out.println("(1)  Set a Transport's Status");
            System.out.println("(2)  Back to Transport Edition Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            try {

                String choice = scanner.nextLine();
                if(choice.equals("1")){

                    System.out.println("Enter the Transport ID of the Transport you want to change it's status:");
                    int transportId = Integer.parseInt(scanner.nextLine());
                    System.out.println(" To which status do you want to change that Transport's Status ?");
                    System.out.println("(1)  Being Assembled");
                    System.out.println("(2)  Queued");
                    System.out.println("(3)  In Transit");
                    System.out.println("(4)  Completed");
                    System.out.println("(5)  Canceled");
                    System.out.println("(6)  Being Delayed");
                    System.out.println(" Select Option: ");
                    String statusChoice = scanner.nextLine();

                    String res = this.tra_ser.setTransportStatus(loggedID, transportId, statusChoice);
                    if(res.equals("Success")){
                        System.out.println("Successfully Changed Transport's Status.\n");
                    } else if(res.equals("Exception")){
                        System.out.println("Failed to change the Transport's status due to technical machine error.\n");
                    }else { System.out.println(res + "\n"); }

                }else if (choice.equals("2")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
    }








    private void editATransportsProblems(long loggedID){
        while (true){
            System.out.println("   --------    Transport's Problems Edition    --------\n");
            System.out.println("(1)  Add a Problem to a Transport");
            System.out.println("(2)  Remove a Problem from a Transport");
            System.out.println("(3)  Back to Transport Edition Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            try {

                String choice = scanner.nextLine();
                if(choice.equals("1")){

                    System.out.println("Enter the Transport ID of the Transport you want to add a problem to:");
                    int transportId1 = Integer.parseInt(scanner.nextLine());
                    System.out.println(" Which Problem do you want to add to that Transport's Document ?");
                    System.out.println("(1)  Puncture");
                    System.out.println("(2)  HeavyTraffic");
                    System.out.println("(3)  RoadAccident");
                    System.out.println("(4)  UnresponsiveContact");
                    System.out.println("(5)  TruckVehicleProblem");
                    System.out.println("(6)  EmptyTruckGasTank");
                    System.out.println(" Select Option: ");
                    String statusChoice1 = scanner.nextLine();

                    String res1 = this.tra_ser.addTransportProblem(loggedID, transportId1, statusChoice1);
                    if(res1.equals("Success")){
                        System.out.println("Successfully Added to Transport's Problems, I hope it gets solved as as fast as possible.\n");
                    } else if(res1.equals("Exception")){
                        System.out.println("Failed to add the Transport problem due to technical machine error.\n");
                    }else { System.out.println(res1 + "\n"); }

                }else if(choice.equals("2")){

                    System.out.println("Enter the Transport ID of the Transport you want to remove a problem from:");
                    int transportId2 = Integer.parseInt(scanner.nextLine());
                    System.out.println(" Which Problem do you want to remove from that Transport's Document ?");
                    System.out.println("(1)  Puncture");
                    System.out.println("(2)  HeavyTraffic");
                    System.out.println("(3)  RoadAccident");
                    System.out.println("(4)  UnresponsiveContact");
                    System.out.println("(5)  TruckVehicleProblem");
                    System.out.println("(6)  EmptyTruckGasTank");
                    System.out.println(" Select Option: ");
                    String statusChoice2 = scanner.nextLine();

                    String res2 = this.tra_ser.removeTransportProblem(loggedID, transportId2, statusChoice2);
                    if(res2.equals("Success")){
                        System.out.println("Successfully removed from Transport's Problems.\n");
                    } else if(res2.equals("Exception")){
                        System.out.println("Failed to remove the Transport problem due to technical machine error.\n");
                    }else { System.out.println(res2 + "\n"); }

                }else if (choice.equals("3")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
    }








    private void editATransportsSites(long loggedID){
        while (true){
            System.out.println("   --------    Transport's Sites Edition Menu    --------\n");
            System.out.println("(1)  Create and Add a New Site's Items Document to a Transport");
            System.out.println("(2)  Delete a Site's Items Document from a Transport");
            System.out.println("(3)  Edit a Transport's Site's Items Document ID");
            System.out.println("(4)  Set a Site's Arrival order in it's Transport");
            System.out.println("(5)  Back to Transport Edition Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            try {

                String choice = scanner.nextLine();

                if(choice.equals("1")){
                    System.out.println("Enter the Transport ID of the Transport you want to add a New Site's Items Document to:");
                    int transportId1 = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter the New Items Document ID number, for that site, you want to add:");
                    int newItemsDocId1 = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter Destination Area number for that site:");
                    int destAreaNumber1 = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter Destination Site Address String for that site:");
                    String destSiteAddress1 = scanner.nextLine();

                    String res1 = this.tra_ser.addDestSite(loggedID, transportId1, newItemsDocId1, destAreaNumber1, destSiteAddress1);
                    if(res1.equals("Success")){
                        System.out.println("Successfully Created and Added a New Site's Items Document to a Transport.\n");
                    } else if(res1.equals("Exception")){
                        System.out.println("Failed to create and add a New Site's Items Document to a Transport due to technical machine error.\n");
                    }else { System.out.println(res1 + "\n"); }




                }else if(choice.equals("2")){
                    System.out.println("Enter the Transport ID of the Transport you want to remove a Site's Items Document from:");
                    int transportId2 = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter the Items Document ID number, for that site, you want to remove:");
                    int oldItemsDocId2 = Integer.parseInt(scanner.nextLine());

                    String res2 = this.tra_ser.removeDestSite(loggedID, transportId2, oldItemsDocId2);
                    if(res2.equals("Success")){
                        System.out.println("Successfully removed from Transport's Site's Items Documents.\n");
                    } else if(res2.equals("Exception")){
                        System.out.println("Failed to remove the Transport's Site's Items Document due to technical machine error.\n");
                    }else { System.out.println(res2 + "\n"); }




                }else if(choice.equals("3")){
                    System.out.println("Let's change a Transport's Site's Items Document ID.");
                    System.out.println("Enter Old Items Document ID number:");
                    int oldItemsDocId3 = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter New Items Document ID number:");
                    int newItemsDocId3 = Integer.parseInt(scanner.nextLine());

                    String res3 = this.tra_ser.changeAnItemsDocNum(loggedID, oldItemsDocId3, newItemsDocId3);
                    if(res3.equals("Success")){
                        System.out.println("Successfully changed Items Documents ID.\n");
                    } else if(res3.equals("Exception")){
                        System.out.println("Failed to change Items Documents ID due to technical machine error.\n");
                    }else { System.out.println(res3 + "\n"); }




                } else if (choice.equals("4")) {
                    System.out.println("Enter the Transport ID:");
                    int transportID = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter the Site's Area number:");
                    int areaNumber1 = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter the Site's Site Address String:");
                    String siteAddress1 = scanner.nextLine();
                    System.out.println("Enter the New index in the Sites Arrival Order, of that Transport, that you want to put that Site:");
                    String newIndex1 = scanner.nextLine();

                    String res4 = this.tra_ser.setSiteArrivalIndexInTransport(loggedID, transportID, areaNumber1, siteAddress1, newIndex1);
                    if(res4.equals("Success")){
                        System.out.println("Successfully changed the Site's Arrival Order.\n");
                    } else if(res4.equals("Exception")){
                        System.out.println("Failed to change the Site's Arrival Order due to technical machine error.\n");
                    }else { System.out.println(res4 + "\n"); }

                } else if (choice.equals("5")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
    }



















    private void editATransportsItems(long loggedID){
        while(true){
            System.out.println("   --------    Transport's Items Edition Menu    --------\n");
            System.out.println("(1)  Add an Item to a Transport");
            System.out.println("(2)  Remove an Item from a Transport");
            System.out.println("(3)  Set the condition of an Item in a Transport");
            System.out.println("(4)  Back to Transport Edition Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            try {

                String choice = scanner.nextLine();
                if(choice.equals("1")){
                    System.out.println("Let's add an Item to a Transport.");
                    System.out.println("Enter The Items Document ID of the Items Document you want to add an Item to:");
                    int itemsDocId = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter Item Name:");
                    String itemName = scanner.nextLine();
                    System.out.println("Enter Item Weight:");
                    double itemWeight = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter Item Condition: ( ('Good') / ('Bad'/or any other key) )");
                    boolean itemCondition = scanner.nextLine().equals("Good");
                    System.out.println("Enter Item Amount you want to add:");
                    int itemAmount = Integer.parseInt(scanner.nextLine());

                    String res1 = this.tra_ser.addItem(loggedID, itemsDocId, itemName, itemWeight, itemAmount, itemCondition);
                    if(res1.equals("Success")){
                        System.out.println("Successfully added Item.\n");
                    } else if(res1.equals("Exception")){
                        System.out.println("Failed to add Item due to technical machine error.\n");
                    }else { System.out.println(res1 + "\n"); }

                }else if(choice.equals("2")){

                    System.out.println("Let's remove an Item from a Transport.");
                    System.out.println("Enter The Items Document ID of the Items Document you want to remove an Item from:");
                    int itemsDocId = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter Item Name:");
                    String itemName = scanner.nextLine();
                    System.out.println("Enter Item Weight:");
                    double itemWeight = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter Item Condition: ( ('Good') / ('Bad'/or any other key) )");
                    boolean itemCondition = (scanner.nextLine().equals("Good"));
                    System.out.println("Enter Item Amount you want to remove:");
                    int itemAmount = Integer.parseInt(scanner.nextLine());

                    String res1 = this.tra_ser.removeItem(loggedID, itemsDocId, itemName, itemWeight, itemAmount, itemCondition);
                    if(res1.equals("Success")){
                        System.out.println("Successfully removed Item.\n");
                    } else if(res1.equals("Exception")){
                        System.out.println("Failed to remove Item due to technical machine error.\n");
                    }else { System.out.println(res1 + "\n"); }


                }else if(choice.equals("3")){

                    System.out.println("Let's change the Condition of an Item in a Transport.");
                    System.out.println("Enter The Items Document ID of the Items Document you want to change an Item's condition in:");
                    int itemsDocId = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter Item Name:");
                    String itemName = scanner.nextLine();
                    System.out.println("Enter Item Weight:");
                    double itemWeight = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter the New Item Condition you want to set to that Item: ( ('Good') / ('Bad'/or any other key) )");
                    boolean itemCondition = (scanner.nextLine().equals("Good"));
                    System.out.println("Enter Item Amount you want to change condition to:");
                    int itemAmount = Integer.parseInt(scanner.nextLine());

                    String res1 = this.tra_ser.setItemCond(loggedID, itemsDocId, itemName, itemWeight, itemAmount, itemCondition);
                    if(res1.equals("Success")){
                        System.out.println("Successfully changed Item's Condition.\n");
                    } else if(res1.equals("Exception")){
                        System.out.println("Failed to change Item's Condition due to technical machine error.\n");
                    }else { System.out.println(res1 + "\n"); }

                } else if (choice.equals("4")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
    }















    private void editATransportsDriverOrTruck(long loggedID){
        while(true){
            System.out.println("   --------    Transport's Driver/Truck Edition    --------\n");
            System.out.println("(1)  Set a Transport's Truck");
            System.out.println("(2)  Set a Transport's Driver");
            System.out.println("(3)  Back to Transport Edition Menu");
            System.out.println();
            System.out.println(" Select Option: ");


            try {

                String choice = scanner.nextLine();
                if(choice.equals("1")){
                    System.out.println("Enter the Transport ID:");
                    int transportID = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter the Desired Truck's Number:");
                    int truckID = Integer.parseInt(scanner.nextLine());

                    String res1 = this.tra_ser.setTransportTruck(loggedID, transportID, truckID);
                    if(res1.equals("Success")){
                        System.out.println("Successfully set Transport's Truck.\n");
                    } else if(res1.equals("Exception")){
                        System.out.println("Failed to set Transport's Truck due to technical machine error.\n");
                    }else { System.out.println(res1 + "\n"); }

                }else if(choice.equals("2")){
                    System.out.println("Enter the Transport ID:");
                    int transportID = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter the Desired Driver's ID:");
                    int driverID = Integer.parseInt(scanner.nextLine());

                    String res2 = this.tra_ser.setTransportDriver(loggedID, transportID, driverID);
                    if(res2.equals("Success")){
                        System.out.println("Successfully set Transport's Driver.\n");
                    } else if(res2.equals("Exception")){
                        System.out.println("Failed to set Transport's Driver due to technical machine error.\n");
                    }else { System.out.println(res2 + "\n"); }

                }else if (choice.equals("3")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
    }






























    









    private void shippingAreasOptionsMenu(long loggedID){   ////////////////////////////////   Shipping Areas Menu   <<<--------------------------------------------
        while(true){
            System.out.println("   --------    Shipping Areas Options Menu    -------\n");
            System.out.println("(1)  View All Shipping Areas");
            System.out.println("(2)  Add a Shipping Area");
            System.out.println("(3)  Edit a Shipping Area's Details");
            System.out.println("(4)  Delete a Shipping Area");
            System.out.println("(5)  Back to Transport Manager Main Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            try {

                String choice = scanner.nextLine();
                if(choice.equals("1")){
                    viewAllShippingAreas(loggedID);
                }else if(choice.equals("2")){
                    addaShippingArea(loggedID);
                }else if(choice.equals("3")){
                    editaShippingAreasDetails(loggedID);
                } else if (choice.equals("4")) {
                    deleteaShippingArea(loggedID);
                } else if (choice.equals("5")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
    }



    //////////////////////          HELPER FUNCTIONS FOR THE Shipping Areas Options Menu

    private void viewAllShippingAreas(long loggedID){
        System.out.println("   --------    Showing All Shipping Areas    --------\n");
        System.out.println(site_ser.showAllShippingAreas(loggedID));
        System.out.println();
    }

    private void addaShippingArea(long loggedID){
        System.out.println("   --------    Adding a Shipping Area    -------\n");

        try {

            System.out.println("Enter Area Number: ");
            int areaNum = Integer.parseInt(scanner.nextLine());
//                scanner.nextLine(); // consume the leftover newline   <<---  if needed
            System.out.println("Enter Area Name: ");
            String areaName = scanner.nextLine();

            String res = site_ser.addShippingArea(loggedID, areaNum, areaName);
            if(res.equals("Success")){
                System.out.println("Successfully Added Shipping Area\n");
            } else if(res.equals("Exception")){
                System.out.println("Failed to add Shipping Area due to technical machine error\n");
            }else { System.out.println(res + "\n"); }

            System.out.println();

        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }

    }



    private void deleteaShippingArea(long loggedID){
        System.out.println("   --------    Deleting a Shipping Area    -------\n");

        try {

            System.out.println("Enter area number: ");
            int areaNum = Integer.parseInt(scanner.nextLine());
//                scanner.nextLine(); // consume the leftover newline   <<---  if needed

            String res = site_ser.deleteShippingArea(loggedID, areaNum);

            if(res.equals("Success")){
                System.out.println("Successfully Deleted Shipping Area\n");
            } else if(res.equals("Exception")){
                System.out.println("Failed to Delete Shipping Area due to technical machine error\n");
            }else { System.out.println(res + "\n"); }

            System.out.println();

        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }

    }


    private void editaShippingAreasDetails(long loggedID){
        System.out.println("   --------    Editing a Shipping Area Menu    -------\n");

        try {

            System.out.println("Enter Area Number of Shipping Area to Edit: ");
            int areaNum = Integer.parseInt(scanner.nextLine());

            String res = "";
            while (true){
                System.out.println("\nWhat information would you like to edit?: ");
                System.out.println("(1)  Area Number");
                System.out.println("(2)  Area Name");
                System.out.println("(3)  Go Back to Shipping Areas Options Menu");
                System.out.println(" Select Option: ");
                int infoType = Integer.parseInt(scanner.nextLine());
//                scanner.nextLine(); // consume the leftover newline   <<---  if needed

                if (infoType == 1){
                    System.out.println("Enter Updated Data: ");
                    int Newareanum = Integer.parseInt(scanner.nextLine());
//                scanner.nextLine(); // consume the leftover newline   <<---  if needed
                    res = site_ser.setShippingAreaNum(loggedID, areaNum, Newareanum);
                    break;
                } else if (infoType == 2) {
                    System.out.println("Enter Updated Data: ");
                    String NewareaName = scanner.nextLine();
                    res = site_ser.setShippingAreaName(loggedID, areaNum, NewareaName);
                    break;
                } else if (infoType == 3){
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();
            }

            if(res.equals("Success")){
                System.out.println("Successfully Edited Site\n");
            } else if(res.equals("Exception")){
                System.out.println("Failed to Edit Site due to technical machine error\n");
            }else { System.out.println(res + "\n"); }

            System.out.println();

        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }


    }








    private void sitesOptionsMenu(long loggedID){   ////////////////////////////////    Sites Menu   <<<--------------------------------------------
        while(true){
            System.out.println("   --------    Sites Options Menu    -------\n");
            System.out.println("(1)  View All Sites");
            System.out.println("(2)  Add a Site");
            System.out.println("(3)  Edit a Site's Details");
            System.out.println("(4)  Delete a Site");
            System.out.println("(5)  Back to Transport Manager Main Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            try {

                String choice = scanner.nextLine();
                if(choice.equals("1")){
                    viewAllSites(loggedID);
                }else if(choice.equals("2")){
                    addaSite(loggedID);
                }else if(choice.equals("3")){
                    editaSitesDetails(loggedID);
                } else if (choice.equals("4")) {
                    deleteaSite(loggedID);
                } else if (choice.equals("5")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
    }

    //////////////////////          HELPER FUNCTIONS FOR THE Sites Options Menu

    private void viewAllSites(long loggedID){
        System.out.println("   --------    Showing All Sites    --------\n");
        System.out.println(site_ser.showAllSites(loggedID));
        System.out.println();
    }


    private void addaSite(long loggedID){
        System.out.println("   --------    Adding a Site    -------\n");

        try {

            System.out.println("Enter Area Number: ");
            int areaNum = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter Address: ");
            String address = scanner.nextLine();
            System.out.println("Enter contact name: ");
            String contName = scanner.nextLine();
            System.out.println("Enter contact number: ");
            long contNum = Long.parseLong(scanner.nextLine());

            String res = site_ser.addSite(loggedID, areaNum, address, contName, contNum);
            if(res.equals("Success")){
                System.out.println("Successfully Added Site\n");
            } else if(res.equals("Exception")){
                System.out.println("Failed to add Site due to technical machine error\n");
            }else { System.out.println(res + "\n"); }

            System.out.println();

        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }

    }


    private void deleteaSite(long loggedID){
        System.out.println("   --------    Deleting a Site    -------\n");

        try {

            System.out.println("Enter area number: ");
            int areaNum = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter address: ");
            String address = scanner.nextLine();

            String res = site_ser.deleteSite(loggedID, areaNum, address);

            if(res.equals("Success")){
                System.out.println("Successfully Deleted Site\n");
            } else if(res.equals("Exception")){
                System.out.println("Failed to Delete Site due to technical machine error\n");
            }else { System.out.println(res + "\n"); }

            System.out.println();

        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }

    }




    private void editaSitesDetails(long loggedID){
        System.out.println("   --------    Editing a Site Menu    -------\n");

        try {

            System.out.println("Enter Data of Site to Edit:");
            System.out.println("Enter Area Number: ");
            int areaNum = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter Address: ");
            String address = scanner.nextLine();

            String res = "";
            while (true){
                System.out.println("\nWhat information would you like to edit?: ");
                System.out.println("(1)  Site Area Number");
                System.out.println("(2)  Site Address String");
                System.out.println("(3)  Site Contact Name");
                System.out.println("(4)  Site Contact Number");
                System.out.println("(5)  Go Back to Sites Options Menu");
                System.out.println(" Select Option: ");
                int infoType = Integer.parseInt(scanner.nextLine());

                System.out.println("Enter Updated Data: ");
                if (infoType == 1){
                    int Newareanum = Integer.parseInt(scanner.nextLine());
                    res = site_ser.setSiteAreaNum(loggedID, areaNum, Newareanum, address);
                    break;
                } else if (infoType == 2) {
                    String Newaddress = scanner.nextLine();
                    res = site_ser.setSiteAddress(loggedID, areaNum, address, Newaddress);
                    break;
                } else if (infoType == 3) {
                    String NewcontName = scanner.nextLine();
                    res = site_ser.setSiteContName(loggedID, areaNum, address, NewcontName);
                    break;
                } else if (infoType == 4) {
                    long NewContnum = Long.parseLong(scanner.nextLine());
                    res = site_ser.setSiteContNum(loggedID, areaNum, address, NewContnum);
                    break;
                } else if (infoType == 5) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();
            }

            if(res.equals("Success")){
                System.out.println("Successfully Edited Site\n");
            } else if(res.equals("Exception")){
                System.out.println("Failed to Edit Site due to technical machine error\n");
            }else { System.out.println(res + "\n"); }

            System.out.println();

        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }


    }























    private void trucksOptionsMenu(long loggedID){   ////////////////////////////////    Trucks Menu   <<<--------------------------------------------
        while(true){
            System.out.println("   --------    Trucks Options Menu    -------\n");
            System.out.println("(1)  View All Trucks");
            System.out.println("(2)  Add a Truck");
            System.out.println("(3)  Delete a Truck");
            System.out.println("(4)  Back to Transport Manager Menu");
            System.out.println();
            System.out.println(" Select Option: ");

            try {

                String choice = scanner.nextLine();
                if(choice.equals("1")){
                    viewAllTrucks(loggedID);
                }else if(choice.equals("2")){
                    addaTruck(loggedID);
                }else if(choice.equals("3")){
                    deleteaTruck(loggedID);
                } else if (choice.equals("4")) {
                    System.out.println("\n\n");
                    return;
                } else {
                    System.out.println("\n  --->  Please enter a number between the menu's margins  <---\n");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("You've entered the wrong input type, going back a menu");
                return;
            }

        }
    }

    //////////////////////          HELPER FUNCTIONS FOR THE Trucks Options Menu

    private void viewAllTrucks(long loggedID){
        System.out.println("   --------    Showing All Trucks    --------\n");
        System.out.println(tru_ser.showTrucks(loggedID));
        System.out.println();
    }


    private void addaTruck(long loggedID){
        System.out.println("   --------    Truck Addition    --------\n");

        try {

            System.out.println("Let's add a Truck:");
            System.out.println("Enter Truck Number:");
            int truck_num = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter Truck Model:");
            String model = scanner.nextLine();
            System.out.println("Enter the Truck's Net. Weight (of the Truck itself):");
            double net_wei = Double.parseDouble(scanner.nextLine());
            System.out.println("Enter the Truck's Max Carry Weight:");
            double max_carry = Double.parseDouble(scanner.nextLine());
            System.out.println("Enter Truck's Required License:");
            String license = scanner.nextLine();

            String res = this.tru_ser.addTruck(loggedID, truck_num, model, net_wei, max_carry, license);
            if(res.equals("Success")){
                System.out.println("Successfully added Truck.\n");
            } else if(res.equals("Exception")){
                System.out.println("Failed to add Truck due to technical machine error.\n");
            }else { System.out.println(res + "\n"); }

            System.out.println();

        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }

    }




    private void deleteaTruck(long loggedID){
        System.out.println("   --------    Truck Deletion    --------\n");

        try {

            System.out.println("Let's Remove a Truck:");
            System.out.println("Enter Truck Number:");
            int truck_num = Integer.parseInt(scanner.nextLine());

            String res = this.tru_ser.removeTruck(loggedID, truck_num);
            if(res.equals("Success")){
                System.out.println("Successfully removed Truck.\n");
            } else if(res.equals("Exception")){
                System.out.println("Failed to remove Truck due to technical machine error.\n");
            }else { System.out.println(res + "\n"); }

            System.out.println();

        } catch (Exception e) {
            System.out.println("You've entered the wrong input type, going back a menu");
            return;
        }

    }















    private void showAllDrivers(long loggedID) {
        System.out.println();
        try {
            System.out.println(this.tra_ser.showAllDrivers(loggedID));
        } catch (Exception e) {
            System.out.println("Problem with showing all drivers");
        }
        System.out.println();
    }








}
