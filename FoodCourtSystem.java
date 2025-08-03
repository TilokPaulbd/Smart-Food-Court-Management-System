import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class FoodCourtSystem {
    private static ArrayList<FoodItem>menu=new ArrayList<>();
    private static ArrayList<Order>orderHistory=new ArrayList<>();
    private static Queue<Student>studentQueue=new LinkedList<>();

    private static final String MENU_FILE="menu.txt";
    private static final String ORDERS_FILE="orders.txt";






    public static void displayMenu(){
        System.out.println("\nCurrent menu :");
        System.out.printf("%-5s %-20s %-12s   %-5s\n", "ID", "Name", "Price", "Qty");
        for (FoodItem foodItem : menu) {
            System.out.printf("%-5s %-20s %-8.2fTaka   %-5d\n", foodItem.getId(), foodItem.getName(), foodItem.getPrice(), foodItem.getQuantity());
        }
    }







    public static void addToOrder(Student student,Scanner scanner){
        displayMenu();
        
        try {
            
            System.out.print("Enter Food Number to order: ");
            String id = scanner.next();
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            FoodItem selectedItem = null;

            for (FoodItem item : menu) {
                if (item.getId().equals(id) && item.getQuantity() >= quantity) {
                    selectedItem = item;
                    break;
                }
            }

            if (selectedItem != null) {
                student.addToOrder(selectedItem, quantity);
                selectedItem.setQuantity(selectedItem.getQuantity() - quantity);
                System.out.println("Item added to order");
            } else {
                System.out.println("Item not available or insufficient quantity .");
            }

        } catch (Exception e) {
            System.out.println("Invalid input ");
            scanner.nextLine();
        }
        
    }




    public static void viewCurrentOrder(Student student){
        Order currentOrder=student.getCurrentOrder();

        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            System.out.println("No items in current order.");
        } else {
            System.out.println("\nCurrent Order:");
            System.out.println("Items:");
            for (OrderItem item : currentOrder.getItems()) {
                System.out.printf("  %-20s x%-5d %-8.2fTaka\n", 
                    item.getFoodItem().getName(), item.getQuantity(), item.getTotalAmount());
            }
            System.out.println("Total Amount: Taka" + String.format("%.2f", currentOrder.getTotalAmount()));
        }
       
    }





    public static void checkOut(Student student){
        Order currentOrder=student.getCurrentOrder();

        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
        System.out.println("No items to checkout.");
        return;
        }

        System.out.println("\nFinal Order:");
        viewCurrentOrder(student);
        System.out.println("Proceeding to payment...");
        currentOrder.markAsPaid();
        orderHistory.add(currentOrder);
        student.checkOut();
        System.out.println("Payment successful! Thank you for your order.");
    
    }








    public static void saveData(){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(MENU_FILE))){
            for (FoodItem foodItem : menu) {
                writer.write(foodItem.toString());
                writer.newLine();
                
            }
            writer.close();

            System.out.println("Menu saved successfully with " + menu.size() + " items.");

        } catch (Exception e) {
           System.out.println("Error saving menu: " + e.getMessage());
        }





        try (BufferedWriter writer=new BufferedWriter(new FileWriter(ORDERS_FILE))){

            for (Order order : orderHistory) {
                writer.write(order.toString());
                writer.newLine();
            }
            writer.close();

            System.out.println("Order history saved successfully with " + orderHistory.size() + " orders.");


        } catch (Exception e) {
         System.out.println("Error saving orders: " + e.getMessage());

        }
    }








    public static void loadData(){

        menu.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    menu.add(FoodItem.fromString(line));
                }
            }
            System.out.println("Menu loaded successfully with " + menu.size() + " items.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }





        orderHistory.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    orderHistory.add(Order.fromString(line, menu));
                }
            }

            System.out.println("Order history loaded successfully with " + orderHistory.size() + " orders.");
        }
        catch (Exception e) {
            System.out.println("No existing orders file found. Starting with empty order history.");
        }


    }




    private static void processCustomerQueue(Scanner scanner) {
        while (!studentQueue.isEmpty()) {
            Student student= studentQueue.poll();
            System.out.println("\nNow serving: " + student.getName());
            customerMenu(student, scanner);
        }
        System.out.println("No more customers in queue.");
    }





    private static void customerMenu(Student student, Scanner scanner) {
        while (true) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. View Menu");
            System.out.println("2. Add to Order");
            System.out.println("3. View Current Order");
            System.out.println("4. Checkout");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: student.displayMenu(); break;
                    case 2: addToOrder(student, scanner); break;
                    case 3: viewCurrentOrder(student); break;
                    case 4: checkOut(student); return;
                    case 5: return;
                    default: System.out.println("Invalid choice! Please enter 1-5.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number (1-5).");
                scanner.nextLine();
            }
        }
    }




    
    private static void adminMenu(Admin admin, Scanner scanner) {
        while (true) {
            admin.displayMenu();
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: admin.addFoodItem(scanner, menu); break;
                    case 2:admin.viewAllItems(menu);admin.inreaseItemQuantity(scanner, menu); break;
                    case 3: admin.viewAllItems(menu); break;
                    case 4: admin.viewOrderHistory(orderHistory); break;
                    case 5: return;
                    default: System.out.println("Invalid choice! Please enter 1-5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number (1-5).");
                scanner.nextLine();
            }
        }
    }







    private static void studentLogin(Scanner scanner) {
        System.out.print("Enter student ID: ");
        String id = scanner.next();
        System.out.print("Enter your name: ");
        String name = scanner.next();

        Student customer = new Student(id, name);
        studentQueue.add(customer);
        System.out.println("You are in the queue. Please wait for your turn...");
        processCustomerQueue(scanner);
    }










    public static void adminLogin(Scanner scanner){

        Console console =System.console();
        
        System.out.print("Enter admin ID: ");
        String id = scanner.next();
        System.out.print("Enter admin name: ");
        String name = scanner.next();
        System.out.print("Enter admin password: ");
        
        char[] passwordChar=console.readPassword();
        String password = new String(passwordChar);

        if (Admin.checkPassword(password)) {
            Admin admin = new Admin(id, name);
            adminMenu(admin, scanner);
        } else {
            System.out.println("Invalid password! Access denied.");
        }
    }










    public static void main(String[] args) {
        loadData();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to Food Court Management System");
            System.out.println("1. Admin Login");
            System.out.println("2. Customer Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: adminLogin(scanner); break;
                    case 2: studentLogin(scanner); break;
                    case 3: saveData(); System.exit(0); break;
                    default: System.out.println("Invalid choice! Please enter 1-3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number (1-3).");
                scanner.nextLine();
            }
        }
    }

}
