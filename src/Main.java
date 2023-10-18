import java.util.*;
import java.io.*;

public class Main {
    /* Helper Functions */

    // Function to read in regular customer file
    public static Customer[] readRegularCustomerFile(Scanner fileScanner, Customer[] regularCustomers){
        int i = 0;
        while(fileScanner.hasNextLine()){
            if(fileScanner.hasNext()){
                // Read in the customer information
                String guestID = fileScanner.next();
                String firstName = fileScanner.next();
                String lastName = fileScanner.next();
                Double amountSpent = fileScanner.nextDouble();

                Customer newCustomer = new Customer(firstName, lastName, guestID, amountSpent); // Create a new customer object

                // Increase the size of the regularCustomers array if needed
                if (i >= regularCustomers.length) {
                    regularCustomers = Arrays.copyOf(regularCustomers, i + 1); // Increase the size by 1
                }

                regularCustomers[i] = newCustomer;
                i++;
            }
        }
        return regularCustomers;
    }

    // Function to read in preferred customer file
    public static Customer[] readPreferredCustomerFile(Scanner fileScanner, Customer[] preferredCustomers){
        boolean isGold = false;
        int i = 0;
        while(fileScanner.hasNextLine()){
            if(fileScanner.hasNext()){
                String guestID = fileScanner.next();
                String firstName = fileScanner.next();
                String lastName = fileScanner.next();
                Double amountSpent = fileScanner.nextDouble();
                String next = fileScanner.next();

                if(preferredCustomers == null){
                    preferredCustomers = new Customer[1];
                }
                if(i >= preferredCustomers.length){
                    preferredCustomers = Arrays.copyOf(preferredCustomers, i + 1); // Increase the size by 1
                }
                // Check if the customer is gold
                if(next.contains("%")){
                    isGold = true;
                    next = next.replace("%", "");
                }
                if(isGold){
                    Gold newCustomer = new Gold(firstName, lastName, guestID, amountSpent, Integer.parseInt(next)); // Create a new gold customer object
                    preferredCustomers[i] = newCustomer;
                }
                else{
                    Platinum newCustomer = new Platinum(firstName, lastName, guestID, amountSpent, Integer.parseInt(next)); // Create a new platinum customer object
                    preferredCustomers[i] = newCustomer;
                }

                i++;
                isGold = false;
            }
        }
        return preferredCustomers;
    }

    // Function to read in order file
    public static void readOrderFile(Scanner filScanner, Customer[] regularCustomers, Customer[] preferredCustomers){
        int lineNumber = 0;
        while(filScanner.hasNextLine()){
            if(filScanner.hasNext()){
                String line = filScanner.nextLine();
                Scanner lineScanner = new Scanner(line); // Create a new scanner to read the line

                lineNumber++;
                if(lineScanner.hasNext()){
                    String guestID = lineScanner.next();
                    if(!checkIDCustomer(guestID, regularCustomers) && !checkIDPreferred(guestID, preferredCustomers)){
                        System.out.println("Error on line " + lineNumber + ": Guest ID " + guestID + " not found.");
                    }
                }
                else if(lineScanner.hasNext()){
                    Character size = lineScanner.next().charAt(0);
                    if(!checkSize(size)){
                        System.out.println("Error on line " + lineNumber + ": Invalid size " + size + ".");
                    }
                }
                else if(lineScanner.hasNextDouble()){
                    Double amount = lineScanner.nextDouble();
                    if(amount < 0){
                        System.out.println("Error on line " + lineNumber + ": Invalid amount " + amount + ".");
                    }
                }
                else if(lineScanner.hasNextInt()){
                    int quantity = lineScanner.nextInt();
                    if(quantity < 0){
                        System.out.println("Error on line " + lineNumber + ": Invalid quantity " + quantity + ".");
                    }
                }
                else{
                    System.out.println("Error on line " + lineNumber + ": Invalid input.");
                }

                lineScanner.close();
            }
        }
    }

    // Function to check if the guest ID exists in the customer arrays
    public static Boolean checkIDCustomer(String guestID, Customer[] regularCustomers){
        Boolean isFound = false;
        for(int i = 0; i < regularCustomers.length; i++){
            Customer customer = regularCustomers[i];
            if(customer.getID().equals(guestID)){
                isFound = true;
                break;
            }
        }
        return isFound;
    }
    // Function to check if the guest ID exists in the customer arrays
    public static Boolean checkIDPreferred(String guestID, Customer[] preferredCustomers){
        Boolean isFound = false;
        for(int i = 0; i < preferredCustomers.length; i++){
            Customer customer = preferredCustomers[i];
            if(customer.getID().equals(guestID)){
                isFound = true;
                break;
            }
        }
        return isFound;
    }
    public static Boolean checkSize(Character size){
        Boolean isFound = false;
        if(size.equals('S') || size.equals('M') || size.equals('L')){
            isFound = true;
        }
        return isFound;
    }

    /* End of helper functions */
    public static void main(String[] args) throws Exception {
        // Variable declarations
        Scanner scnr = new Scanner(System.in);
        Scanner fileScanner = null;
        String regularCustomerFileName, preferredCustomerFileName, orderFileName;
        InputStream inputStream = null;

        // Create arrays to store customers
        Customer[] regularCustomers = new Customer[1];
        Customer[] preferredCustomers = null;

        // Get file names from user
        System.out.print("Enter the name of the regular customer file: ");
        regularCustomerFileName = scnr.next();

        System.out.print("Enter the name of the preferred customer file: ");
        preferredCustomerFileName = scnr.next();

        System.out.print("Enter the name of the order file: ");
        orderFileName = scnr.next();

        try{
            // Read in the regular customer file
            File regualrFile = new File(regularCustomerFileName);
            inputStream = new FileInputStream(regualrFile);
            fileScanner = new Scanner(inputStream);
            regularCustomers = readRegularCustomerFile(fileScanner, regularCustomers);

            // Test the contents of the array
            System.out.println("Regular Customers:");
            for(int i = 0; i < regularCustomers.length; i++){
                Customer customer = regularCustomers[i];
                System.out.println(customer.getFirst() + " " + customer.getLast() + " " + customer.getID() + " " + customer.getAmount());
            }
            System.out.println("Length: " + regularCustomers.length);
            
        }
        catch(FileNotFoundException e){
            System.out.println("Regular customer file not found.");
        }
        // Read in the preferred customer file
        File preferredFile = new File(preferredCustomerFileName);

        // Check if the preferred customer file exists
        if(preferredFile.exists() && preferredFile.length() > 0){
            try{
                preferredCustomers = new Customer[0]; // Initialize the array to avoid null pointer exceptions
                // Read in the preferred customer file
                inputStream = new FileInputStream(preferredFile);
                fileScanner = new Scanner(inputStream);
                preferredCustomers = readPreferredCustomerFile(fileScanner, preferredCustomers);

                // Test the contents of the array
                System.out.println("Preferred Customers:");
                for(int i = 0; i < preferredCustomers.length; i++){
                    Customer customer = preferredCustomers[i];

                    // Check if the customer is gold or platinum
                    if(customer instanceof Gold){
                        Gold goldCustomer = (Gold) customer;
                        System.out.println("Customer type: Gold" + " " + goldCustomer.getFirst() + " " + goldCustomer.getLast() + " " + goldCustomer.getID() + " " + goldCustomer.getAmount() + " " + goldCustomer.getDiscount() + "%");
                    }
                    else{
                        Platinum platinumCustomer = (Platinum) customer;
                        System.out.println("Customer type: Platinum" + " " + platinumCustomer.getFirst() + " " + platinumCustomer.getLast() + " " + platinumCustomer.getID() + " " + platinumCustomer.getAmount() + " " + platinumCustomer.getBonusBucks());
                    }
                }
                System.out.println("Length: " + preferredCustomers.length);

            }
            catch(FileNotFoundException e){
                System.out.println("Preferred customer file not found.");
            }
        }
        else{
            System.out.println("Error: Preferred customer file does not exist or is empty.");
        }

        // Read in the order file
        File orderFile = new File(orderFileName);
        try{
            inputStream = new FileInputStream(orderFile);
            fileScanner = new Scanner(inputStream);
            readOrderFile(fileScanner, regularCustomers, preferredCustomers);
        }
        catch(FileNotFoundException e){
            System.out.println("Order file not found.");
        }

        scnr.close();
        fileScanner.close();
        inputStream.close();
    }
}