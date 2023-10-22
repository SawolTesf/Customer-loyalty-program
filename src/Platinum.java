// Derived class
public class Platinum extends Customer{
    int bonusBucks;

    public Platinum(String fn, String ln, String gi, float as, int bb){
        super(fn, ln, gi, as);
        bonusBucks = bb;
    }

    public int getBonusBucks(){
        return bonusBucks;
    }
    public void setBonusBucks(int bb){
        bonusBucks = bb;
    }
}