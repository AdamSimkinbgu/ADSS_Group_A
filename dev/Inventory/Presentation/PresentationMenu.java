package Inventory.Presentation;

import Inventory.DTO.*;
import Inventory.Service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import Inventory.type.Position;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class PresentationMenu {
    private MainService ms;
    private ObjectMapper om;

    public PresentationMenu() {
        ms = MainService.GetInstance();
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }

    public boolean Integration(){
        return ms.SetIntegrationService();
    }

    public void Menu() {
        int choice;
        while (true) {
            choice = PMainMenu();

            ///// Handle menu choice
            switch (choice) {
                case 1:// Add new Product
                    AddProduct();
                    break;
                case 2:
                    MoveOrder();
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
                    InventoryReport();
                    break;
                case 11:
                    BadReport();
                    break;
                case 12:
                    MissingReport();
                    break;
                case 13:
                    AddRecurringOrder();
                    break;

            }
        }
    }

    // VVVVVVVVV
    private int PMainMenu() {
        int choice;
        Scanner scanner = new Scanner(System.in);
        // Print menu
        System.out.println("==== Main Menu ====");
        System.out.println("1. Add New Product");
        System.out.println("2. Move Delivered Supplies to WareHose");
        System.out.println("3. Add Sale");
        System.out.println("4. Add Discount");
        System.out.println("5. Add New Category");
        System.out.println("6. Add To Category");
        System.out.println("7. Report Bad Product");
        System.out.println("8. Move Product");
        System.out.println("9. Search For Product");
        System.out.println("10. Get Current Inventory Report For Restock");
        System.out.println("11. Get Bad Product Report");
        System.out.println("12. Get Missing Report For Restock And Order Missing Product");
        System.out.println("13. Add A Recurring Order");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");

        choice = scanner.nextInt();
        return choice;
    }

    // todo check
    private void AddProduct() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder table = new StringBuilder();
        ArrayList<ProductDTO> ls;
        String name = "", manName = "";

        String msg = ms.GetProductLst();
        // print the list
        try {
            ls = om.readValue(msg, new TypeReference<ArrayList<ProductDTO>>() {
            });
            table.append(String.format("%-15s %-10s %-10s%n", "Id", "Name", "Manufacturer"));
            for (ProductDTO p : ls) {
                table.append(String.format("%-15d %-10s %-10s%n", p.getproductId(), p.getproductName(),
                        p.getmanufacturerName()));
            }
            System.out.println(table.toString());
        } catch (Exception e) {
            System.out.println();
            return;
        }

        // chose product
        System.out.println("Enter product id:");
        int pid = scanner.nextInt();
        boolean flag = true;
        do {
            for (ProductDTO p : ls) {
                if (p.getproductId() == pid)
                    flag = false;
                name = p.getproductName();
                manName = p.getmanufacturerName();
            }
            if (flag) {
                System.out.println("Invalid ID");
            }
        } while (flag);

        // get price
        System.out.println("Enter price: ");
        float price = scanner.nextFloat();
        if (price < 0) {
            System.out.println("Invalid price ");
            return;
        }

        System.out.println("Minimal Amount:\n");
        // get the minimal amount in store
        System.out.println("Enter minimal amount in store: ");
        int minAStore = scanner.nextInt();
        if (minAStore < 0) {
            System.out.println("Invalid minimal amount ");
            return;
        }

        // get the minimal amount in stock
        System.out.println("Enter minimal amount in stock: ");
        int minAStock = scanner.nextInt();
        if (minAStock < 0) {
            System.out.println("Invalid minimal amount ");
            return;
        }

        System.out.println("Position On Shelf:");
        // get the minimal amount in store
        System.out.println("Enter store lane: ");
        int srLane = scanner.nextInt();
        if (srLane < 0) {
            System.out.println("Invalid lane ");
            return;
        }

        System.out.println("Enter store shelf: ");
        int srShelf = scanner.nextInt();
        if (srShelf < 0) {
            System.out.println("Invalid shelf ");
            return;
        }
        Position sp = new Position(srLane, srShelf);

        System.out.println("Enter warehouse lane: ");
        int wLane = scanner.nextInt();
        if (wLane < 0) {
            System.out.println("Invalid shelf ");
            return;
        }

        System.out.println("Enter warehouse shelf: ");
        int wShelf = scanner.nextInt();
        if (wShelf < 0) {
            System.out.println("Invalid shelf ");
            return;
        }
        scanner.nextLine();// clean the input buffer

        Position wp = new Position(wLane, wShelf);

        ProductDTO newProd = new ProductDTO(pid, name, manName, minAStore, minAStock, price, wp, sp);

        try {
            String message = om.writeValueAsString(newProd);
            String response = ms.AddProduct(message);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error converting product to JSON: " + e.getMessage());
        }

    }

    // todo check
    private void MoveOrder() {
        try {
            String msg = ms.MoveOrder();
            System.out.println(msg);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    // VVVVVVVVV
    private void AddNewCategory() {
        Scanner scanner = new Scanner(System.in);

        // get name
        System.out.println("Enter category name: ");
        String name = scanner.nextLine();

        String response = ms.AddNewCategory(name);
        System.out.println(response);
    }

    private void AddToCategory() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the name of the category: ");
        String catName = scanner.nextLine();

        System.out.println("To add product press 1 and to sub-category press 2");
        int pOrc = scanner.nextInt();
        scanner.nextLine();

        if (pOrc == 1) {
            System.out.println("Enter product id:");
            int pId = scanner.nextInt();
            scanner.nextLine();
            if (pId < 0) {
                System.out.println("Invalid id ");
                return;
            }

            String response = ms.AddToCategory(catName, pId);
            System.out.println(response);
        } else if (pOrc == 2) {
            System.out.println("Enter the name of the category: ");
            String subCatName = scanner.nextLine();

            String response = ms.AddToCategory(catName, subCatName);
            System.out.println(response);
        } else
            System.out.println("Invalid input");
    }

    // VVVVVVVVV
    private void MoveProduct() {
        Scanner scanner = new Scanner(System.in);

        // get id
        System.out.println("Enter product id: ");
        int pId = scanner.nextInt();
        if (pId < 0) {
            System.out.println("Invalid id ");
            return;
        }

        System.out.println("To move in store press 1 and to move in warehouse press 2");
        int sOrw = scanner.nextInt();
        scanner.nextLine();

        System.out.println("new line");
        int newL = scanner.nextInt();
        scanner.nextLine();
        if (newL < 0) {
            System.out.println("Invalid line ");
            return;
        }

        System.out.println("new shelf");
        int newS = scanner.nextInt();
        scanner.nextLine();
        if (newS < 0) {
            System.out.println("Invalid shelf");
            return;
        }

        Position newp = new Position(newL, newS);
        boolean flag = true;
        if (sOrw == 2) {
            flag = false;
        } else if (sOrw != 1) {
            System.out.println("Invalid input");
        }

        String response = ms.MoveProduct(pId, flag, newp);
        System.out.println(response);
    }

    // VVVVVVVV
    private void AddDiscount() {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("Enter percent of discount:");
        float percent = scanner.nextFloat();
        scanner.nextLine();
        if (percent < 0 || percent > 1) {
            System.out.println("Invalid percent ");
            return;
        }

        System.out.print("Enter a expire date (yyyy-MM-dd): ");
        String input = scanner.nextLine();

        try {
            LocalDate date = LocalDate.parse(input, formatter);
            if (LocalDate.now().isAfter(date)) {
                System.out.println("Date is in the past. Please enter a valid future date.");
                return;
            }

            System.out.println("To add to product press 1 and to add to category press 2");
            int pOrc = scanner.nextInt();
            scanner.nextLine();

            if (pOrc == 1) {
                System.out.println("Enter product id:");
                int pId = scanner.nextInt();
                scanner.nextLine();
                if (pId < 0) {
                    System.out.println("Invalid id ");
                    return;
                }

                DiscountDTO d = new DiscountDTO(percent, date, pId);

                String json = om.writeValueAsString(d);
                String response = ms.AddDiscount(json);
                System.out.println(response);
            } else if (pOrc == 2) {
                System.out.println("Enter the name of the category: ");
                String CatName = scanner.nextLine();

                DiscountDTO d = new DiscountDTO(percent, date, CatName);
                String json = om.writeValueAsString(d);

                String response = ms.AddDiscount(json);
                System.out.println(response);
            } else
                System.out.println("Invalid input");
        } catch (Exception e) {
            System.out.println("Invalid date format!");
        }

    }

    // VVVVVVVV
    private void AddSale() {
        Scanner scanner = new Scanner(System.in);
        HashMap<Integer, Integer> product = new HashMap<>();
        int i = 1;

        while (true) {
            System.out.println("Enter product " + i++ + " id or -1 to end the list : ");
            int pId = scanner.nextInt();
            if (pId < -1) {
                System.out.println("Invalid id ");
                continue;
            }
            if (pId == -1)
                break;

            // get Quantity
            System.out.println("Enter quantity: ");
            int quantity = scanner.nextInt();
            if (quantity < 0) {
                System.out.println("Invalid quantity");
                continue;
            }
            product.put(pId, quantity);
        }

        SaleDTO s = new SaleDTO(product);

        try {
            String message = om.writeValueAsString(s);
            String response = ms.AddSale(message);
            s = om.readValue(response, SaleDTO.class);
            System.out.println("=====Sale Report=====");
            System.out.println("Sale id: " + s.getId());
            System.out.println("Sale price: " + s.getSalePrice());
        } catch (Exception e) {
            System.out.println("Error converting product to JSON: " + e.getMessage());
        }

    }

    // VVVVVVVV
    private void AddBadProduct() {
        Scanner scanner = new Scanner(System.in);

        // get id
        System.out.println("Enter product id: ");
        int pId = scanner.nextInt();
        if (pId < 0) {
            System.out.println("Invalid id ");
            return;
        }

        // get Quantity
        System.out.println("Enter quantity: ");
        int quantity = scanner.nextInt();
        if (quantity < 0) {
            System.out.println("Invalid quantity");
            return;
        }
        scanner.nextLine();// clean the input buffer

        String response = ms.AddBadProduct(pId, quantity);
        System.out.println(response);
    }

    // todo check
    private void MissingReport() {
        Scanner scanner = new Scanner(System.in);
        int input;
        System.out.println(ms.MissingReport());

        do {
            System.out.println(("Do you want to order all the missing inventory (1 for yes,0 for no)"));

            input = scanner.nextInt();
            if (input == 1) {
                OrderMissing();
                break;
            } else if (input != 0) {
                System.out.println("Invalid input.");
            }
        }while (input != 0);
    }

    // VVVVVVVV
    private void BadReport() {
        System.out.println(ms.BadReport());
    }

    // VVVVVVVV
    private void InventoryReport() {
        System.out.println(ms.GetCurrentReport());
    }

    // VVVVVVVV
    private void Search() {
        Scanner scanner = new Scanner(System.in);
        // get id
        System.out.println("Enter product id: ");
        int pId = scanner.nextInt();
        if (pId < 0) {
            System.out.println("Invalid id ");
            return;
        }

        String response = ms.Search(pId);
        try {
            ProductService p = om.readValue(response, ProductService.class);
            System.out.println("=====Product Report=====");
            System.out.println("Product name\t" + p.getproductName());
            System.out.println("Product id\t" + p.getproductId());
            System.out.println("Product price\t" + p.getproductPrice());
            System.out.println("Product quantity\t" + p.getQuantity());
            System.out.println("Product bad quantity\t" + p.getBadQuantity());

        } catch (Exception e) {
            System.out.println(response + e.getMessage());
        }
    }



    private void OrderMissing() {
        String msg = ms.AddMissingOrder();
        System.out.println(msg);
    }

    private void AddRecurringOrder() {
        Scanner scanner = new Scanner(System.in);
        int pId = 0;

        HashMap<Integer,Integer> order = new HashMap<>();

        //get the product list
        while (pId != -1) {
            System.out.println("Enter product ID (to end enter -1): ");
            pId = scanner.nextInt();

            if(pId< -1)System.out.println("Invalid product ID.");
            else if (pId != -1) {
                System.out.println("Enter quantity");
                int quantity = scanner.nextInt();
                order.put(pId,quantity);
            }
        }
        System.out.println("Enter a day of the week by number (1-7)");
        int day = scanner.nextInt();

        String msg = ms.AddRecurringOrder(order, day);
        System.out.println(msg);
    }

    private void DeleteRecurringOrder(){

    }


}
