package mmda.mmdamobilepayment.data.model;

import org.json.JSONException;
import org.json.JSONObject;


public class Payment {

    private String licenseNumber;
    private String paymentId;
    private int ticketId;
    private String paymentDate;
    private String cost;
    private String currency;

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String toJSON() {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("license_number", licenseNumber);
            jsonObject.put("payment_id", paymentId);
            jsonObject.put("ticket_id", ticketId);
            jsonObject.put("payment_date", paymentDate);
            jsonObject.put("cost", cost);
            jsonObject.put("currency", currency);

            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }
}
