
package creditgroup;

import acctinterface.Account;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

/*
 * @author Ben Gomes
 */
public abstract class CreditAccount implements Account {
    // Global Variables
    private String accountName, typeCode;
    private int accountNo;
    private double creditLimit, balanceDue;
    private String actionMessage, errorMessage;
    NumberFormat c = NumberFormat.getCurrencyInstance();
    
    public CreditAccount(String tc, String nm, double cl) {
        this.typeCode = tc;
        this.accountNo = 0;
        this.creditLimit = 0;
        this.balanceDue = 0;
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
                this.creditLimit = cl;
                this.accountName = nm;
                writestatus();
                if (this.errorMessage.isEmpty()) {
                   actionMessage = this.typeCode + " Account " + this.accountNo + " opened.";
                   writelog(actionMessage);
                }   
                if (!this.errorMessage.isEmpty()) {
                    this.creditLimit = 0;
                    this.accountNo = -1;
                }
            } catch (Exception e) {
                errorMessage = "Fatal error in constructor: " + e.getMessage();
                this.accountNo = -1;
            }
        } // end of while
    } // end of constructor
    
    public CreditAccount(String tc, int a) {
        errorMessage = "";
        actionMessage = "";
        this.typeCode = tc;
        this.creditLimit = 0;
        this.balanceDue = 0;
        this.accountNo = a;
        
        try {
            BufferedReader in = new BufferedReader(
                                new FileReader(this.typeCode + this.accountNo + ".txt"));
            this.accountName = in.readLine();
            this.creditLimit = Double.parseDouble(in.readLine());
            this.balanceDue = Double.parseDouble(in.readLine());
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
            out.println(this.creditLimit);
            out.println(this.balanceDue);
            out.close();
        } catch (IOException e) {
            errorMessage = "Error writing status file for acct: " + this.accountNo;
        } catch (Exception e) {
            errorMessage = "General error in acct: " + this.accountNo + " " +
                              e.getMessage();
        }
    }
    
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
        return this.balanceDue;
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
    
    public double getLimit() {
        return this.creditLimit;
    }
    
    protected void setLimit(double cl) {
        this.creditLimit = cl;
        writestatus();
        if (this.errorMessage.isEmpty()) {
            actionMessage = this.typeCode + " Credit Limit updated to: " +
                    this.creditLimit;
            writelog(actionMessage);
        }
    }
    
    public double getAvailCr() {
        return (this.creditLimit - this.balanceDue);
    }
    
    @Override
    public void setCharge(double amt, String desc) {
        errorMessage = "";
        actionMessage = "";
        
        if (this.accountNo <= 0) {
            errorMessage = "Charge attempt on non-active account.";
            return;
        }
        
        if (amt <= 0) {
           actionMessage = "Charge of " + c.format(amt) + " for " + desc +
                    " declined - illegal amount.";
           writelog(actionMessage);
        } else if((this.balanceDue + amt) > this.creditLimit) {
           actionMessage = "Charge of " + c.format(amt) + " for " + desc +
                    " declined - over limit!"; 
           writelog(actionMessage);
        } else {
           this.balanceDue += amt;
           writestatus();
           if (this.errorMessage.isEmpty()) {
               actionMessage = "Charge of " + c.format(amt) + " for " + desc +
                        " posted.";
               writelog(actionMessage);
           }
        }
    } //end of setcharge
    
    @Override
    public void setPayment(double amt) {
        errorMessage = "";
        actionMessage = "";
        
        if (this.accountNo <= 0) {
            errorMessage = "Charge attempt on non-active account.";
            return;
        }
        
        if (amt <= 0) {
            actionMessage = "Payment of " + c.format(amt) + " declined - illegal amount.";
            writelog(actionMessage); 
        } else {
            this.balanceDue -= amt;
            writestatus();
            if (this.errorMessage.isEmpty()) {
                actionMessage = "Payment of " + c.format(amt) + " posted.";
                writelog(actionMessage);
            }
        }
   } //end of payment
    
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
   public void setIntAction(double ir) {
       NumberFormat p = NumberFormat.getPercentInstance();
       p.setMaximumFractionDigits(3);
       
       errorMessage = "";
       actionMessage = "";
       double interestCharged;
       
       if (this.accountNo <= 0) {
           errorMessage = "Interest Charge attempt on non-active account.";
           return;
       }
       
       if (ir <= 0) {
           actionMessage = "Interest rate of " + p.format(ir) + " declined - illegal amount.";
           writelog(actionMessage); 
       } else {
           interestCharged= this.balanceDue * ir/12.00;
           setCharge(interestCharged,"Interest charged.");
           //The writestatus() and writelog() methods will be performed in the setCharge() method
           
       } //end of interest charge method
   }
   @Override
   public abstract String getAcctTypeCd();
   
   @Override
   public abstract String getAcctTypeDesc();
   
   public abstract double getEquityValue();
}
