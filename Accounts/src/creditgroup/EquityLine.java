
package creditgroup;

/*
 * @author Ben Gomes
 */
public class EquityLine extends CreditAccount {
    public static final String TypeCd = "EQ";
    public static final String TypeCdDesc = "Equity Line";
    private double creditRatio = 0.8;
    private double equityValue;
    
    public EquityLine(String name, double startValue) {
        super(EquityLine.TypeCd, name, 0.0);
        if (super.getErrMsg().isEmpty()) {
            this.equityValue = startValue;
            super.setLimit(this.equityValue * this.creditRatio);
        }
    }
    
    public EquityLine(int a) {
        super(EquityLine.TypeCd, a);
        if (super.getErrMsg().isEmpty()) {
        this.equityValue = super.getLimit() / this.creditRatio;
        } else {
            this.equityValue = 0;
        }
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
        return this.equityValue;
    }
}
