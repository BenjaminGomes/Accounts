
package acctinterface;

import java.util.ArrayList;

/*
 * @author Ben Gomes
 */
public interface Account {
    int getAcctNo();
    
    String getAcctName();
    String getAcctTypeCd();
    String getAcctTypeDesc();
    
    double getBalance();
    
    String getErrMsg();
    String getActionMsg();
    
    void setCharge(double amt, String desc);
    void setPayment(double amt);
    void setIntAction(double rate);
    ArrayList<String> getLog();
}

