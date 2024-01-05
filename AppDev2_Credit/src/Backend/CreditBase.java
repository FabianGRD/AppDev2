
package Backend;

public class CreditBase {
    public int Id;
    public String Name;

    public int CreditSum;
    public int CustomerId;
    public int InterestRateId;
    public int OriginId;



    public String toString(){
        return "" + Id +
                " " + Name +
                " " + CreditSum;
    }
}
