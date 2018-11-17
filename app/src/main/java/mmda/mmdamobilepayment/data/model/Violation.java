package mmda.mmdamobilepayment.data.model;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Violation implements Comparable<Violation> {

    private int ticketId;
    private String violationName;
    private Double penaltyFee;
    private String code;
    private String offenseNumber;
    private String dateOfViolation;
    private Boolean isPaid = false;
    private String dateOfPayment;
    private String transactionNumber;

    public Violation(int ticket_id) {
        this.ticketId = ticket_id;
    }

    public String getViolationName() {
        return violationName;
    }

    public void setViolationName(String violationName) {
        this.violationName = violationName.trim();
    }

    public Double getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(Double penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOffenseNumber() {
        return offenseNumber;
    }

    public void setOffenseNumber(String offenseNumber) {
        this.offenseNumber = offenseNumber;
    }

    public String getDateOfViolation() {
        return dateOfViolation;
    }

    public void setDateOfViolation(String dateOfViolation) {
        this.dateOfViolation = dateOfViolation;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public String getDateOfPayment() {
        return (isPaid) ? dateOfPayment : null;
    }

    public void setDateOfPayment(String dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public String getTransactionNumber() {
        return (isPaid) ? transactionNumber : "";
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public int getTicketId() {
        return ticketId;
    }

    @Override
    public int compareTo(@NonNull Violation o) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date d1 = null, d2 = null;

        try {
            if (isPaid) {
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                d1 = dateFormatter.parse(this.dateOfPayment);
                d2 = dateFormatter.parse(o.getDateOfPayment());
            } else {
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                d1 = dateFormatter.parse(this.dateOfViolation);
                d2 = dateFormatter.parse(o.getDateOfViolation());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d1 != null && d2 != null) {
            if (d1.after(d2)) {
                return -1;
            } else {
                return 1;
            }
        }

        return 0;
    }
}
