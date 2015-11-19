
package creditgroup;

/*
 * @author Ben Gomes
 */
public class CreditCard extends CreditAccount {
    public static final String TypeCd = "CC";
    public static final String TypeCdDesc = "Credit Card";
    
    public CreditCard(String name, double startValue) {
        super(CreditCard.TypeCd, name, startValue);
    }
    
    public CreditCard(int a) {
        super(CreditCard.TypeCd, a);
    }
    
    @Override
    public String getAcctTypeCd() {
        return CreditCard.TypeCd;
    }
    
    @Override
    public String getAcctTypeDesc() {
        return CreditCard.TypeCdDesc;
    } 
    
    @Override
    public double getEquityValue() {
        return 0.0;
    }
}
