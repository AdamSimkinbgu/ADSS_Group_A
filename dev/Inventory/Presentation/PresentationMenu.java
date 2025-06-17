package Inventory.Presentation;

import Inventory.DTO.*;
import Inventory.Service.*;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.Enums.InitializeState;
import com.fasterxml.jackson.core.type.TypeReference;
import Inventory.type.Position;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class PresentationMenu {
    private static Scanner scanner = new Scanner(System.in);

    private MainService ms;
    private ObjectMapper om;

    public PresentationMenu() {
        ms = MainService.GetInstance();
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }

    public boolean Integration() {
        return ms.SetIntegrationService();
    }

    public void Initialize(InitializeState input) {
        ms.Initialize(input);
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
                case 14:
                    DeleteRecurringOrder();
                    break;

            }
        }
    }

    // VVVVVVVVV
    private int PMainMenu() {
        int choice;

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
        System.out.println("9. Search For Product Or Category");
        System.out.println("10. Get Current Inventory Report For Restock");
        System.out.println("11. Get Bad Product Report");
        System.out.println("12. Get Missing Report For Restock And Order Missing Product");
        System.out.println("13. Add A Recurring Order");
        System.out.println("14. Delete Recurring Order");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");

        choice = scanner.nextInt();
        return choice;
    }

    // todo check
    private void AddProduct() {
        StringBuilder table = new StringBuilder();
        ArrayList<ProductDTO> ls;
        String name = "", manName = "";

        String msg = ms.GetProductLst();
        try {
            ls = om.readValue(msg, new TypeReference<ArrayList<ProductDTO>>() {
            });
        } catch (Exception e) {
            System.out.println("Error reading product list: " + e.getMessage());
            return;
        }
        if (ls.isEmpty()) {
            System.out.println("No products in catalog");
            return;
        }
        System.out.println("=====Product List=====");
        System.out.println("------------------------------------------------");
        System.out.printf("| %-25s | %-8s | %-7s |%n", "Product Name", "ID", "Manufacturer");
        System.out.println("------------------------------------------------");
        for (ProductDTO p : ls) {
            System.out.printf("| %-25s | %-8d | %-7s |%n",
                    p.getproductName(),
                    p.getproductId(),
                    p.getmanufacturerName());
        }
        System.out.println("------------------------------------------------");
        // chose product
        System.out.println("Enter product id:");
        int pid;
        boolean flag = true;
        do {
            pid = scanner.nextInt();
            for (ProductDTO p : ls) {
                if (p.getproductId() == pid) {
                    flag = false;
                    name = p.getproductName();
                    manName = p.getmanufacturerName();
                }
            }
            if (flag) {
                System.out.println("Invalid ID");
                flag = false;
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

        // get name
        System.out.println("Enter category name: ");
        scanner.nextLine(); // clear the input buffer
        String name = scanner.nextLine();

        String response = ms.AddNewCategory(name);
        System.out.println(response);
    }

    private void AddToCategory() {

        System.out.println("Enter the name of the category: ");
        scanner.nextLine(); // clear the input buffer
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
            // scanner.nextLine(); // clear the input buffer
            String subCatName = scanner.nextLine();

            String response = ms.AddToCategory(catName, subCatName);
            System.out.println(response);
        } else
            System.out.println("Invalid input");
    }

    // VVVVVVVVV
    private void MoveProduct() {

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
            for (Integer key : s.getProducts().keySet()) {
                System.out.println("Product id: " + key + ", Quantity: " + s.getProducts().get(key));
            }

            System.out.println("Sale price: " + s.getSalePrice());
        } catch (Exception e) {
            System.out.println("Error converting product to JSON: " + e.getMessage());
        }

    }

    // VVVVVVVV
    private void AddBadProduct() {

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
        } while (input != 0);
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

        System.out.println("To search category by name press 1 and to search product by id press 2");
        int choice = scanner.nextInt();
        if (choice != 1 && choice != 2) {
            System.out.println("Invalid choice");
            return;
        } else if (choice == 1) {

            // get category name
            System.out.println("Enter category name: ");
            String catName = scanner.next();
            String response = ms.Search(catName);
            try {
                ArrayList<ProductService> ls = om.readValue(response, new TypeReference<ArrayList<ProductService>>() {
                });
                System.out.println("=====Category Report=====");
                System.out.println("------------------------------------------------");
                System.out.printf("| %-15s | %-8s | %-7s | %-8s | %-8s |%n",
                        "Product Name", "ID", "Price", "Quantity", "Bad Qty");
                System.out.println("------------------------------------------------");
                for (ProductService p : ls) {
                    System.out.printf("| %-15s | %-8d | %7.2f | %8d | %8d |%n",
                            p.getproductName(),
                            p.getproductId(),
                            p.getproductPrice(),
                            p.getQuantity(),
                            p.getBadQuantity());
                }
                System.out.println("------------------------------------------------");
            } catch (Exception e) {
                System.out.println(response + e.getMessage());
            }
            return;

        } else {

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
    }

    private void OrderMissing() {
        String msg = ms.AddMissingOrder();
        System.out.println(msg);
    }

    private void AddRecurringOrder() {

        int pId = 0;

        HashMap<Integer, Integer> order = new HashMap<>();

        // get the product list
        InventoryReport();
        while (pId != -1) {
            System.out.println("Enter product ID (to end enter -1): ");
            pId = scanner.nextInt();

            if (pId < -1)
                System.out.println("Invalid product ID.");
            else if (pId != -1) {
                System.out.println("Enter quantity");
                int quantity = scanner.nextInt();
                order.put(pId, quantity);
            }
        }
        System.out
                .println("Enter a day of the week (Sunday, Monday, Tuesday, wednesday, thursday, Friday, Saturday): ");
        String dayInput = scanner.next();
        DayOfWeek day;
        try {
            day = DayOfWeek.valueOf(dayInput.toUpperCase());
            if (day == null) {
                System.out.println("Invalid day of the week.");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid day of the week.");
            return;
        }

        String msg = ms.AddRecurringOrder(order, day);
        System.out.println(msg);
    }

    private void DeleteRecurringOrder() {
        System.out.println(ms.GetRecurringOrders());
        System.out.println("Enter the order ID to delete: ");
        int orderId = scanner.nextInt();
        if (orderId < 0) {
            System.out.println("Invalid order ID.");
            return;
        }

        String response = ms.DeleteRecurringOrder(orderId);
        System.out.println(response);
    }

}
