
package assetgroup;

/*
 * @author Ben Gomes
 */
public class Checking extends AssetAccount {
    public static final String TypeCd = "CK";
    public static final String TypeCdDesc = "Checking";
    
    public Checking(String nm, double startbal) {
        super(Checking.TypeCd, nm, startbal);
    }
    
    public Checking(int a) {
        super(Checking.TypeCd, a);
    }
    
    @Override
    public String getAcctTypeCd() {
        return Checking.TypeCd;
    }
    
    @Override
    public String getAcctTypeDesc() {
        return Checking.TypeCdDesc;
    }
    
    @Override
    public void setIntAction(double rate) {
        super.setErrMsg("Illegal action: checking account doesn't earn interest");
        super.writelog("Interest requested on non-interest bearing account.");
    }
}
