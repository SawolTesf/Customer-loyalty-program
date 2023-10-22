// Base class

public class Customer {
    protected String firstName;
    protected String lastName;
    protected String GuestID;
    protected float AmountSpent;

    Customer(String fn, String ln, String gi, float as){
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
    
    public float getAmount(){
        return AmountSpent;
    }
    public void setAmount(float as){
        AmountSpent = as;
    }
}