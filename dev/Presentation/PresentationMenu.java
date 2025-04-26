package Presentation;

import Service.MainService;
import Service.ProductService;
import Service.SaleService;
import Service.SupplyService;
import type.Position;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
                    AddSale();
                    break;
                case 4:
                    AddDiscount();
                    break;
                case 5:
                    AddNewCategory();
                    break;
                case 6:
                    AddToCategory();
                    break;
                case 7:
                    AddBadProduct();
                    break;
                case 8:
                    MoveProduct();
                    break;
                case 9:
                    Search();
                    break;
                case 0:
                    return;
                case 10:
                    InvintoryReport();
                    break;
                case 11:
                    MissingReport();
                    break;
                case 12:
                    BadReport();
                    break;


            }
        }
    }
    //VVVVVVVVV
    private int PMainMenu(){
        int choice =0;
        Scanner scanner = new Scanner(System.in);
        // Print menu
        System.out.println("==== Main Menu ====");
        System.out.println("1. Add New Product");
        System.out.println("2. Add Supply");
        System.out.println("3. Add Sale");
        System.out.println("4. Add Discount");
        System.out.println("5. Add New Category");
        System.out.println("6. Add To Category");
        System.out.println("7. Report Bad Product");
        System.out.println("8. Move Product");
        System.out.println("9. Search For Product");
        System.out.println("10. Get Current Inventory Report For Restock");
        System.out.println("11. Get Missing Report For Restock");
        System.out.println("12. Get Bad Product Report");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");

        choice = scanner.nextInt();
        return choice;
    }

    //VVVVVVVV
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

        ProductService newProd = new ProductService(name,manName,minAStore,minAStock,price,srShelf,srLane,wShelf,wLane);


        try {
            String message = om.writeValueAsString(newProd);
            String response = ms.AddProduct(message);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error converting product to JSON: " + e.getMessage());
        }


    }

    //VVVVVVVV
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

    //VVVVVVVVV
    private void AddNewCategory(){
        Scanner scanner = new Scanner(System.in);

        //get name
        System.out.println("Enter category name: ");
        String name = scanner.nextLine();

        String response = ms.AddNewCategory(name);
        System.out.println(response);
    }

    private void AddToCategory(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the name of the category: ");
        String catName = scanner.nextLine();


        System.out.println("To add product press 1 and to sub-category press 2");
        int pOrc = scanner.nextInt();
        scanner.nextLine();

        if(pOrc == 1){
            System.out.println("Enter product id:");
            int pId = scanner.nextInt();
            scanner.nextLine();
            if(pId < 0){
                System.out.println("Invalid id ");
                return;
            }

            String response = ms.AddToCategory(catName,pId);
            System.out.println(response);
        } else if (pOrc == 2) {
            System.out.println("Enter the name of the category: ");
            String subCatName = scanner.nextLine();

            String response = ms.AddToCategory(catName,subCatName);
            System.out.println(response);
        }
        else System.out.println("Invalid input");
    }

    //VVVVVVVVV
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

        System.out.println("new line");
        int newL = scanner.nextInt();
        scanner.nextLine();
        if(newL < 0){
            System.out.println("Invalid line ");
            return;
        }

        System.out.println("new shelf");
        int newS = scanner.nextInt();
        scanner.nextLine();
        if(newS < 0){
            System.out.println("Invalid shelf");
            return;
        }

        Position newp = new Position(newL,newS);
        boolean flag = true;
        if (sOrw == 2) {
            flag = false;
        } else if(sOrw != 1) {
            System.out.println("Invalid input");
        }


        String response = ms.MoveProduct(pId,flag,newp);
        System.out.println(response);
    }

    //VVVVVVVV
    private void AddDiscount(){
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("Enter percent of discount:");
        float percent = scanner.nextFloat();
        scanner.nextLine();
        if(percent < 0 || percent > 1){
            System.out.println("Invalid percent ");
            return;
        }

        System.out.print("Enter a expire date (yyyy-MM-dd): ");
        String input = scanner.nextLine();

        try{
            LocalDate date = LocalDate.parse(input, formatter);
            if(LocalDate.now().isAfter(date)){
                System.out.println("Date is in the past. Please enter a valid future date.");
                return;
            }

            System.out.println("To add to product press 1 and to add to category press 2");
            int pOrc = scanner.nextInt();
            scanner.nextLine();

            if(pOrc == 1){
                System.out.println("Enter product id:");
                int pId = scanner.nextInt();
                scanner.nextLine();
                if(pId < 0){
                    System.out.println("Invalid id ");
                    return;
                }

                String response = ms.AddDiscount(pId,percent,date);
                System.out.println(response);
            } else if (pOrc == 2) {
                System.out.println("Enter the name of the category: ");
                String CatName = scanner.nextLine();

                String response = ms.AddDiscount(CatName,percent,date);
                System.out.println(response);
            }
            else System.out.println("Invalid input");
        }catch (Exception e){
            System.out.println("Invalid date format!");
        }




    }

    //VVVVVVVV
    private void AddSale(){
        Scanner scanner = new Scanner(System.in);
        HashMap<Integer,Integer> product = new HashMap<>();
        int i = 1;

        while (true){
            System.out.println("Enter product " + i++ + " id or -1 to end the list : ");
            int pId = scanner.nextInt();
            if(pId < -1){
                System.out.println("Invalid id ");
                continue;
            }
            if (pId == -1)break;

            //get Quantity
            System.out.println("Enter quantity: ");
            int quantity = scanner.nextInt();
            if(quantity < 0){
                System.out.println("Invalid quantity");
                continue;
            }
            product.put(pId,quantity);
        }

        SaleService s = new SaleService(product);

        try {
            String message = om.writeValueAsString(s);
            String response = ms.AddSale(message);
            s = om.readValue(response,SaleService.class);
            System.out.println("=====Sale Report=====");
            System.out.println("Sale id: "+s.getSaleID());
            System.out.println("Sale price: " + s.getSalePrice());
        } catch (Exception e) {
            System.out.println("Error converting product to JSON: " + e.getMessage());
        }

    }

    //VVVVVVVV
    private void AddBadProduct(){
        Scanner scanner = new Scanner(System.in);

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

        String response = ms.AddBadProduct(pId,quantity);
        System.out.println(response);
    }

    //VVVVVVVV
    private void MissingReport(){
        System.out.println(ms.MissingReport());
    }

    //VVVVVVVV
    private void BadReport(){
        System.out.println( ms.BadReport());
    }

    //VVVVVVVV
    private void InvintoryReport(){
        System.out.println(ms.GetcurrentReport());
    }

    //VVVVVVVV
    private void Search(){
        Scanner scanner = new Scanner(System.in);
        //get id
        System.out.println("Enter product id: ");
        int pId = scanner.nextInt();
        if(pId < 0){
            System.out.println("Invalid id ");
            return;
        }

        String response = ms.Search(pId);
        try{
            ProductService p = om.readValue(response,ProductService.class);
            System.out.println("=====Product Report=====");
            System.out.println("Product name\t" +p.getproductName());
            System.out.println("Product id\t" +p.getproductId());
            System.out.println("Product price\t" +p.getproductPrice());
            System.out.println("Product quantity\t" +p.getQuantity());
            System.out.println("Product bad quantity\t" +p.getBadQuantity());


        }catch (Exception e){
            System.out.println(response+ e.getMessage());
        }
    }
}
