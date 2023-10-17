import java.util.*;
import java.io.*;

public class Main {

    // Method to read in regular customer file
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
    public static void main(String[] args) throws Exception {
        // Variable declarations
        Scanner scnr = new Scanner(System.in);
        Scanner fileScanner = null;
        String regularCustomerFile, preferredCustomerFile, orderFile;
        InputStream inputStream = null;

        // Create arrays to store customers
        Customer[] regularCustomers = new Customer[1];
        Customer[] preferredCustomers = null;

        // Get file names from user
        System.out.print("Enter the name of the regular customer file: ");
        regularCustomerFile = scnr.next();

        System.out.print("Enter the name of the preferred customer file: ");
        preferredCustomerFile = scnr.next();

        System.out.print("Enter the name of the order file: ");
        orderFile = scnr.next();

        try{
            // Read in the regular customer file
            File regualrFile = new File(regularCustomerFile);
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

            File preferredFile = new File(preferredCustomerFile);
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
                System.out.println("Preferred customer file not found.");
            }


        }
        catch(FileNotFoundException e){
            System.out.println("File not found.");
        }


        scnr.close();
        fileScanner.close();
        inputStream.close();
    }
}