
package assetgroup;

import acctinterface.Account;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/*
 * @author Ben Gomes
 */
public abstract class AssetAccount implements Account {
    private String accountName, typeCode;
    private int accountNo;
    private double balance;
    private String actionMessage, errorMessage;
    
    public AssetAccount(String tc, String nm, double startbal) {
        // create new Asset account by finding unused account number
        this.accountNo = 0;
        this.actionMessage = "";
        this.errorMessage = "";
        
        while (this.accountNo == 0) {
            try {
                this.accountNo = (int) (Math.random() * 1000000);
                BufferedReader in = new BufferedReader(
                                     new FileReader(tc + this.accountNo + ".txt"));
                in.close();
                this.accountNo = 0;
            } catch (IOException e) {
                //'good' result: account does not yot exist....
                this.typeCode = tc;
                this.balance = startbal;
                this.accountName = nm;
                writestatus();
                if (this.errorMessage.isEmpty()) {
                   actionMessage = this.typeCode + " Account " + this.accountNo + " opened.";
                   writelog(actionMessage);
                }   
                if (!this.errorMessage.isEmpty()) {
                    this.balance = 0;
                    this.accountNo = -1;
                }
            } catch (Exception e) {
                errorMessage = "Fatal error in constructor: " + e.getMessage();
                this.accountNo = -1;
            }
        }
    } // end of constructor
    
    public AssetAccount(String tc, int a) {
        errorMessage = "";
        actionMessage = "";
        this.typeCode = tc;
        this.balance = 0;
        this.accountNo = a;
        
        try {
            BufferedReader in = new BufferedReader(
                                new FileReader(this.typeCode + this.accountNo + ".txt"));
            this.accountName = in.readLine();
            this.balance = Double.parseDouble(in.readLine());
            in.close();
            actionMessage = "Account " + a + " re-opened.";
        } catch (Exception e) {
            errorMessage = "Error re-opening account: " + a + " " + e.getMessage();
            this.accountNo = -1;
        }
    }
    
    private void writestatus() {
        try {
            PrintWriter out = new PrintWriter(
                new FileWriter(this.typeCode + this.accountNo + ".txt"));
            out.println(this.accountName);
            out.println(this.balance);
            out.close();
        } catch (IOException e) {
            errorMessage = "Error writing status file for account: " + this.accountNo;
        } catch(Exception e) {
            errorMessage = "General error in status update: " + e.getMessage();
        }
    } //end of writestatus
    
    protected void writelog(String msg) {
        try {
            Calendar cal = Calendar.getInstance();
            DateFormat df = DateFormat.getDateTimeInstance();
            String ts = df.format(cal.getTime());
            PrintWriter out = new PrintWriter(
                new FileWriter(this.typeCode + "L" + this.accountNo + ".txt",true));
            out.println(msg + "\t" + ts);
            out.close();
        } catch (IOException e) {
            errorMessage = "Error writing log file: " + e.getMessage();
        } catch (Exception e) {
            errorMessage = "General error in write log: " + e.getMessage();
        }
    } //end of writelog
    
    @Override
    public String getAcctName() {
        return this.accountName;
    }
    
    @Override
    public int getAcctNo() {
        return this.accountNo;
    }
    
    @Override
    public double getBalance() {
        return this.balance;
    }
    
    @Override
    public String getErrMsg() {
        return this.errorMessage;
    }
    
    protected void setErrMsg(String msg) {
        this.errorMessage = msg;
    }
    
    @Override
    public String getActionMsg() {
        return this.actionMessage;
    }
    @Override
    public void setCharge(double amt, String desc) {
        this.errorMessage = "";
        this.actionMessage = "";
        if (this.accountNo <= 0) {
            this.errorMessage = "Charge attempted on unopened account.";
            return;
        }
        if (amt <= 0) {
            this.errorMessage = "Illegal charge amount of: " + amt + 
                          " must be a positive value.";
            writelog(this.errorMessage);
        } else if (amt > this.balance) {
            this.actionMessage = "Deduction of " + amt + " for " + desc + 
                          " declined - insufficient funds.";
            writelog(this.actionMessage);
        } else {
            this.balance -= amt;
            this.actionMessage = "Deduction of " + amt + " for " + desc;
            writestatus();
            if (this.errorMessage.isEmpty()) {
                writelog (this.actionMessage);
            }
        }
    } // end of setCharge
    @Override
    public void setPayment(double amt) {
        this.errorMessage = "";
        this.actionMessage = "";
        if (this.accountNo <= 0) {
            this.errorMessage = "Payment attempted on unopened account.";
            return;
        }
        if (amt <= 0) {
            this.errorMessage = "Illegal payment amount of: " + amt + 
                          " must be a positive value.";
            writelog(this.errorMessage);
        } else {
            this.balance += amt;
            this.actionMessage = "Deposit of " + amt;
            writestatus();
            if (this.errorMessage.isEmpty()) {
                writelog (this.actionMessage);
            }
        }
    } // end of pmt
    
    @Override
    public void setIntAction(double rate) {
        double intearned;
        if (rate <= 0 || rate > 1.0) {
            this.errorMessage = "Illegal Rate: no interest Action";
            writelog(this.errorMessage);
        } else {
            intearned = this.balance * (rate / 12.0);
            this.balance += intearned;
            writestatus();
            this.actionMessage = "Interest earned: " + intearned + " on balance of " + 
                          (this.balance - intearned) + " at annual rate of: " +
                          rate;
            writelog(this.actionMessage);
        }
    } // end of setIntAction
    
    @Override 
    public ArrayList<String> getLog() {
       ArrayList<String> h = new ArrayList<>();
       errorMessage = "";
       actionMessage = "";
       String t;
       
       if (this.accountNo <= 0) {
            errorMessage = "Charge attempt on non-active account.";
            return h;
        }
       
       try {
           BufferedReader in = new BufferedReader(
                new FileReader(this.typeCode + "L" + this.accountNo + ".txt"));
           t = in.readLine();
           
           while (t != null) {
              h.add(t);
              t = in.readLine();
           }
           in.close();
           actionMessage = "History returned for account: " + this.accountNo;
       } catch (Exception e) {
           errorMessage = "Error reading log file: " + e.getMessage();
       }
       return h;
   }
    @Override
    public abstract String getAcctTypeCd();
    @Override
    public abstract String getAcctTypeDesc();
}
