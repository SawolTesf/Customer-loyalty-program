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
    public static Customer[][] readOrderFile(Scanner filScanner, Customer[] regularCustomers, Customer[] preferredCustomers){

        int lineNumber = 0; // Keep track of the line number for when an error occurs

        // Variable declarations for customer information
        String guestID = "";
        Character size = ' ';
        String type = "";
        float amount = (float)0.0;
        int quantity = 0;
        int discount = 0;

        boolean isUpgraded = false; // Boolean to check if a customer is upgraded to gold or platinum

        // Create customer objects
        Customer customer = null;

        // Create new customer objects for when a customer is upgraded
        Gold newCustomerGold = null;
        Platinum newCustomerPlat = null;

        while(filScanner.hasNextLine()){
            if(filScanner.hasNext()){
                String line = filScanner.nextLine();
                if(line.isEmpty()){
                    continue;
                }
                Scanner lineScanner = new Scanner(line); // Create a new scanner to read the line

                lineNumber++;
                try{
                    int guestIDInt = lineScanner.nextInt(); // Store the guest ID in an integer
                    guestID = Integer.toString(guestIDInt); // Convert the guest ID to a string
                    // Check if the guest ID exists in the regular customer arrays
                    if(checkIDCustomer(guestID, regularCustomers)){
                        customer = getRegular(guestID, regularCustomers);
                    }
                    // Check if the guest ID exists in the preferred customer array
                    else if(checkIDPreferred(guestID, preferredCustomers)){
                        if(getPreferred(guestID, preferredCustomers) instanceof Gold){
                            customer = (Gold)getPreferred(guestID, preferredCustomers);
                        }
                        else{
                            customer = (Platinum)getPreferred(guestID, preferredCustomers);
                        }
                    }
                    // If the guest ID does not exist in either array, print an error message and continue
                    else{
                        System.out.println("Error on line " + lineNumber + ": Guest ID " + guestID + " not found.");
                        continue;
                    }                
                    // Store the drink size in a character
                    size = lineScanner.next().charAt(0);
                    if(!checkSize(size)){
                        System.out.println("Error on line " + lineNumber + ": Invalid size " + size + ".");
                        continue;
                    }
                    // Store the drink type in a string
                    type = lineScanner.next();
                    if(!checkDrinkType(type)){
                        System.out.println("Error on line " + lineNumber + ": Invalid drink type " + type + ".");
                        continue;
                    }
                    // Store the drink amount in a double
                    amount = lineScanner.nextFloat();
                    if(amount < 0){
                        System.out.println("Error on line " + lineNumber + ": Invalid amount " + amount + ".");
                        continue;
                    }
                    // Store the drink quantity in an integer
                    quantity = lineScanner.nextInt();
                    if(quantity < 0){
                        System.out.println("Error on line " + lineNumber + ": Invalid quantity " + quantity + ".");
                    }
                }
                catch(InputMismatchException e){
                    System.out.println("Error on line " + lineNumber + ": Invalid input.");
                    continue;
                }
                if(lineScanner.hasNext()){
                    System.out.println("Error on line " + lineNumber + ": Too many inputs.");
                    continue;
                }

                float cost = calcCost(size, type, amount, quantity); // Calculate the cost of the drink

                if(customer instanceof Gold){
                    // Calculate the new total spent after applying the current discount
                    double newTotalSpent = customer.getAmount() + cost - (cost * ((Gold)customer).getDiscount() / 100);
                    
                    // Check if the new total spent reaches the thresholds for increasing discount or promoting to Platinum
                    if(newTotalSpent >= 200){
                        // Promote the customer to Platinum status
                        newCustomerPlat = new Platinum(customer.getFirst(), customer.getLast(), customer.getID(), newTotalSpent, 0);
                        preferredCustomers = replaceCustomer(newCustomerPlat, preferredCustomers);
                    } else if(newTotalSpent >= 150){
                        // Increase the discount to 15%
                        ((Gold)customer).setDiscount(15);
                    } else if(newTotalSpent >= 100){
                        // Increase the discount to 10%
                        ((Gold)customer).setDiscount(10);
                    }
                    // If the new total spent does not reach any thresholds, then just update the total spent
                    else{
                        customer.setAmount(newTotalSpent);
                        preferredCustomers = replaceCustomer(customer, preferredCustomers);
                    }
                } 
                else if(customer instanceof Platinum){
                    // Calculate the new total spent and bonus bucks
                    double newTotalSpent = customer.getAmount() + cost;
                    int newBonusBucks = ((Platinum)customer).getBonusBucks() + (int)((newTotalSpent - 200) / 5);
                    
                    // Update the total spent and bonus bucks
                    customer.setAmount(newTotalSpent);
                    ((Platinum)customer).setBonusBucks(newBonusBucks);
                }
                
                // If the customer is neither gold nor platinum, then its just a regular customer
                else{
                    if(cost + customer.getAmount() >= 50 && cost + customer.getAmount() < 100){
                        isUpgraded = true;
                        discount = 5;
                        // Apply the 5 percent discount to the cost
                        cost = cost - (cost * (float)0.05);

                        // change the regular customer to a gold customer
                        newCustomerGold = new Gold(customer.getFirst(), customer.getLast(), customer.getID(), customer.getAmount() + cost, discount);
                    }
                    else if(cost + customer.getAmount() >= 100 && cost + customer.getAmount() < 150){
                        isUpgraded = true;
                        discount = 10;
                        // Apply the 10 percent discount to the cost
                        cost = cost - (cost * (float)0.10);

                        // change the regular customer to a gold customer
                        newCustomerGold = new Gold(customer.getFirst(), customer.getLast(), customer.getID(), customer.getAmount() + cost, discount);
                    }
                    else if(cost + customer.getAmount() >= 150){
                        isUpgraded = true;
                        discount = 15;
                        // Apply the 15 percent discount to the cost
                        cost = cost - (cost * (float)0.15);

                        // change the regular customer to a gold customer
                        newCustomerGold = new Gold(customer.getFirst(), customer.getLast(), customer.getID(), customer.getAmount() + cost, discount);
                    }
                    else{
                        // If the customer is not upgraded, then just add the cost to the amount spent
                        customer.setAmount(customer.getAmount() + cost);
                        regularCustomers = replaceCustomer(customer, regularCustomers);
                    }

                    if(isUpgraded){
                        // remove the regular customer from the regular customer array
                        regularCustomers = removeCustomer(guestID, regularCustomers);
                        // add the gold customer to the preferred customer array
                        preferredCustomers = addCustomer(newCustomerGold, preferredCustomers);
                    }

                }

                isUpgraded = false;

                lineScanner.close();
            }
        }
        Customer[][] result = new Customer[2][];
        result[0] = regularCustomers;
        result[1] = preferredCustomers;
        return result;
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
    // Function to check if the drink size is valid
    public static Boolean checkSize(Character size){
        Boolean isFound = false;
        if(size.equals('S') || size.equals('M') || size.equals('L')){
            isFound = true;
        }
        return isFound;
    }
    // Function to check if the drink type is valid
    public static Boolean checkDrinkType(String type){
        Boolean isFound = false;
        if(type.equalsIgnoreCase("Soda") || type.equalsIgnoreCase("Tea") || type.equalsIgnoreCase("Punch")){
            isFound = true;
        }
        return isFound;
    }

    // Function to calculate the cost of the drink
    public static float calcCost(Character size, String type, float amount, int quantity){
        float drinkPrice = (float)0.0, pricePerOz = (float)0.0, cupPrice = (float)0.0, cylinderSurfaceArea = (float)0.0;
        int drinkOunce = 0;

        if(type.equalsIgnoreCase("Soda")){
            pricePerOz = (float)0.20;
        }
        else if(type.equalsIgnoreCase("Tea")){
            pricePerOz = (float)0.12;
        }
        else if(type.equalsIgnoreCase("Punch")){
            pricePerOz = (float)0.15;
        }

        if (size.compareTo('S') == 0 || size.compareTo('s') == 0) {
            drinkOunce = 12;
            cylinderSurfaceArea = (float)(4.0 * 4.5 * Math.PI); // 4.0 is the diameter and 4.5 is the height for a small cup
        } 
        else if (size.compareTo('M') == 0 || size.compareTo('m') == 0) {
            drinkOunce = 20;
            cylinderSurfaceArea = (float)(4.5 * 5.75 * Math.PI); // 4.5 is the diameter and 5.75 is the height for a medium cup
        } 
        else if (size.compareTo('L') == 0 || size.compareTo('l') == 0) {
            drinkOunce = 32;
            cylinderSurfaceArea = (float)(5.5 * 7.0 * Math.PI);
        }

        drinkPrice = pricePerOz * drinkOunce; // Calculate the price of the drink
        
        // Calculate the price of the cup
        cupPrice = cylinderSurfaceArea * amount;

        float totalDrinkPrice = cupPrice + drinkPrice;

        // Finally, the price is price per drink multiplied by the quantity
        return totalDrinkPrice * quantity;
    }
    // Function to get the regular customer object
    public static Customer getRegular(String guestID, Customer[] regularCustomers){
        Customer customer = null;
        for(int i = 0; i < regularCustomers.length; i++){
            if(regularCustomers[i].getID().equals(guestID)){
                customer = regularCustomers[i];
                break;
            }
        }
        return customer;
    }
    // Function to get the preferred customer object
    public static Customer getPreferred(String guestID, Customer[] preferredCustomers){
        Customer customer = null;
        for(int i = 0; i < preferredCustomers.length; i++){
            if(preferredCustomers[i].getID().equals(guestID)){
                customer = preferredCustomers[i];
                break;
            }
        }
        return customer;
    }
    // Function to remove a customer from customer arrays. This works for both regular and preferred customers
    public static Customer[] removeCustomer(String guestID, Customer[] customers){
        Customer[] newCustomers = new Customer[customers.length - 1];
        int j = 0;
        for(int i = 0; i < customers.length; i++){
            if(!customers[i].getID().equals(guestID)){
                newCustomers[j] = customers[i];
                j++;
            }
        }
        return newCustomers;
    }
    // Function to add a customer to the preferred customer array
    public static Customer[] addCustomer(Customer customer, Customer[] preferredCustomers){
        // if the preferred customer array does not exist, then create a new array
        if(preferredCustomers == null){
            preferredCustomers = new Customer[1];
        }
        Customer[] newCustomers = new Customer[preferredCustomers.length + 1];
        for(int i = 0; i < preferredCustomers.length; i++){
            newCustomers[i] = preferredCustomers[i];
        }
        newCustomers[preferredCustomers.length] = customer;
        return newCustomers;
    }
    // Function to replace a customer in the customer arrays

    public static Customer[] replaceCustomer(Customer newCustomer, Customer[] customers){
        for(int i = 0; i < customers.length; i++){
            if(customers[i].getID().equals(newCustomer.getID())){
                customers[i] = newCustomer;
                break;
            }
        }
        return customers;
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
            File regularFile = new File(regularCustomerFileName);
            inputStream = new FileInputStream(regularFile);
            fileScanner = new Scanner(inputStream);
            regularCustomers = readRegularCustomerFile(fileScanner, regularCustomers);
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
            
            Customer[][] updatedCustomers = readOrderFile(fileScanner, regularCustomers, preferredCustomers);

            // Output the arrays to their respective files
            PrintWriter regularWriter = new PrintWriter("customer.dat");
            PrintWriter preferredWriter = new PrintWriter("preferred.dat");

            // Output the regular customers
            for(int i = 0; i < updatedCustomers[0].length; i++){
                Customer customer = updatedCustomers[0][i];
                regularWriter.println(customer.getID() + " " + customer.getFirst() + " " + customer.getLast() + " " + String.format("%.2f", customer.getAmount()));
            }

            // Output the preferred customers
            for(int i = 0; i < updatedCustomers[1].length; i++){
                Customer customer = updatedCustomers[1][i];
                if(customer instanceof Gold){
                    Gold goldCustomer = (Gold)customer;
                    preferredWriter.println(goldCustomer.getID() + " " + goldCustomer.getFirst() + " " + goldCustomer.getLast() + " " + String.format("%.2f", goldCustomer.getAmount()) + " " + goldCustomer.getDiscount() + "%");
                } 
                else if(customer instanceof Platinum){
                    Platinum platinumCustomer = (Platinum)customer;
                    preferredWriter.println(platinumCustomer.getID() + " " + platinumCustomer.getFirst() + " " + platinumCustomer.getLast() + " " + String.format("%.2f", platinumCustomer.getAmount()) + " " + platinumCustomer.getBonusBucks());
                }
            }
            regularWriter.close();
            preferredWriter.close();
        }
        catch(FileNotFoundException e){
            System.out.println("Order file not found.");
        }

        scnr.close();
        fileScanner.close();
        inputStream.close();
    }
}