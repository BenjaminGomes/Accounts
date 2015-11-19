
package assetgroup;

/*
 * @author Ben Gomes
 */
public class Savings extends AssetAccount {
    public static final String TypeCd = "SV";
    public static final String TypeCdDesc = "Savings";
    
    public Savings(String nm, double startVal) {
        super(Savings.TypeCd, nm, startVal);
    }
    
    public Savings(int a) {
        super(Savings.TypeCd, a);
    }
    
    @Override
    public String getAcctTypeCd() {
        return Savings.TypeCd;
    }
    
    @Override
    public String getAcctTypeDesc() {
        return Savings.TypeCdDesc;
    }
}
