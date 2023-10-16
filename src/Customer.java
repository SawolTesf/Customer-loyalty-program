// Base class

public class Customer {
    protected String firstName;
    protected String lastName;
    protected String GuestID;
    protected Double AmountSpent;

    Customer(String fn, String ln, String gi, Double as){
        firstName = fn;
        lastName = ln;
        GuestID = gi;
        AmountSpent = as;
    }

    public String getFirst(){
        return firstName;
    }
    public void setFirst(String fn){
        firstName = fn;
    }

    public String getLast(){
        return lastName;
    }
    public void setLast(String ln){
        lastName = ln;
    }

    public String getID(){
        return GuestID;
    }
    public void setID(String gi){
        GuestID = gi;
    }
    
    public Double getAmount(){
        return AmountSpent;
    }
    public void setAmount(Double as){
        AmountSpent = as;
    }
}