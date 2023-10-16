// Derived Class
public class Gold extends Customer{
    protected int discount;

    Gold(String fn, String ln, String gi, double as, int d){
        super(fn, ln, gi, as);
        discount = d;
    }

    public int getDiscount(){
        return discount;
    }
    public void setDiscount(int d){
        discount = d;
    }
}