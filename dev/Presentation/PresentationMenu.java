package Presentation;

import Service.MainService;
import Service.ProductService;
import Service.SupplyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import type.Position;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;




public class PresentationMenu {
    private MainService ms;
    private ObjectMapper om;


    public PresentationMenu(){
        ms = new MainService();
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }

    public void Menu(){
        int choice =0;
        while (true){
            choice = PMainMenu();

            ///// Handle menu choice
            switch (choice){
                case 1://Add new Product
                    AddProduct();
                    break ;
                case 2:
                    AddSupply();
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    AddNewCategory();
                    break;
                case 7:
                    AddToCategory();
                    break;
                case 8:
                    break;
                case 9:
                    return;
                case 0:
                    break;
                case 10:
                    break;
                case 11:
                    break;



            }
        }
    }

    private int PMainMenu(){
        int choice =0;
        Scanner scanner = new Scanner(System.in);
        // Print menu
        System.out.println("==== Main Menu ====");
        System.out.println("1. Add New Product");
        System.out.println("2. Add Supply");
        System.out.println("3. Add Sale");
        System.out.println("5. Add Discount");
        System.out.println("6. Add New Category");
        System.out.println("7. Add To Category");
        System.out.println("8. Report Bad Product");
        System.out.println("9. Move Product");
        System.out.println("10. Get Bad Product Report");
        System.out.println("11. Get Missing Report For Restock");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");

        choice = scanner.nextInt();
        return choice;
    }

    private void AddProduct(){
        Scanner scanner = new Scanner(System.in);

        //get name
        System.out.println("Enter product name: ");
        String name = scanner.nextLine();

        //get manufacturer name
        System.out.println("Enter manufacturer name: ");
        String manName = scanner.nextLine();

        //get price
        System.out.println("Enter price: ");
        float price = scanner.nextFloat();
        if(price < 0){
            System.out.println("Invalid price ");
            return;
        }


        System.out.println("Minimal Amount:\n");
        //get the minimal amount in store
        System.out.println("Enter minimal amount in store: ");
        int minAStore = scanner.nextInt();
        if(minAStore < 0){
            System.out.println("Invalid minimal amount ");
            return;
        }

        //get the minimal amount in stock
        System.out.println("Enter minimal amount in stock: ");
        int minAStock = scanner.nextInt();
        if(minAStock < 0){
            System.out.println("Invalid minimal amount ");
            return;
        }

        System.out.println("Position On Shelf:");
        //get the minimal amount in store
        System.out.println("Enter store lane: ");
        int srLane = scanner.nextInt();
        if(srLane < 0){
            System.out.println("Invalid lane ");
            return;
        }

        System.out.println("Enter store shelf: ");
        int srShelf = scanner.nextInt();
        if(srShelf < 0){
            System.out.println("Invalid shelf ");
            return;
        }
        Position sp = new Position(srLane,srShelf);

        System.out.println("Enter warehouse lane: ");
        int wLane = scanner.nextInt();
        if(wLane < 0){
            System.out.println("Invalid shelf ");
            return;
        }

        System.out.println("Enter warehouse shelf: ");
        int wShelf = scanner.nextInt();
        if(wShelf < 0){
            System.out.println("Invalid shelf ");
            return;
        }
        scanner.nextLine();//clean the input buffer

        Position wp = new Position(wLane,wShelf);

        ProductService newProd = new ProductService(name,manName,minAStore,minAStock,price,sp,wp);


        try {
            String message = om.writeValueAsString(newProd);
            String response = ms.AddProduct(message);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error converting product to JSON: " + e.getMessage());
        }


    }

    private void AddSupply(){
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //get id
        System.out.println("Enter product id: ");
        int pId = scanner.nextInt();
        if(pId < 0){
            System.out.println("Invalid id ");
            return;
        }

        //get Quantity
        System.out.println("Enter quantity: ");
        int quantity = scanner.nextInt();
        if(quantity < 0){
            System.out.println("Invalid quantity");
            return;
        }
        scanner.nextLine();//clean the input buffer


        //expire date
        System.out.print("Enter a expire date (yyyy-MM-dd): ");
        String input = scanner.nextLine();
        try {
            LocalDate date = LocalDate.parse(input, formatter);
            if(LocalDate.now().isAfter(date)){
                System.out.println("Date is in the past. Please enter a valid future date.");
                return;
            }
            SupplyService newSup = new SupplyService(pId,quantity,date);

            String response = ms.AddSupply(newSup.getProductID(),newSup.getQuantityWarehouse(),newSup.getExpireDate());
            System.out.println(response);

        //}catch (JsonProcessingException e) {
           // System.out.println("Failed to convert supply to JSON: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid date format!");

        }
        return;

    }

    private void AddNewCategory(){
        Scanner scanner = new Scanner(System.in);

        //get name
        System.out.println("Enter category name: ");
        String name = scanner.nextLine();

        String response = ms.AddNewCategory(name);
        System.out.println(response);
    }

    private void AddToCategory(){

    }

    private void MoveProduct(){
        Scanner scanner = new Scanner(System.in);

        //get id
        System.out.println("Enter product id: ");
        int pId = scanner.nextInt();
        if(pId < 0){
            System.out.println("Invalid id ");
            return;
        }

        System.out.println("To move in store press 1 and to move in warehouse press 2");
        int sOrw = scanner.nextInt();
        scanner.nextLine();

        if(sOrw == 1){

        } else if (sOrw == 2) {

        }else {
            System.out.println("Invalid input");
        }

    }
}
