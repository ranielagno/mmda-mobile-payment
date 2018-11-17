package mmda.mmdamobilepayment.data.model;

/**
 * Created by Raniel on 10/26/2018.
 */

public class ViolationBuilder {

    private final Violation violation;

    private ViolationBuilder(int ticketId) {
        violation = new Violation(ticketId);
    }

    public static ViolationBuilder violation(int ticketId) {
        return new ViolationBuilder(ticketId);
    }

    public ViolationBuilder withViolationName(String violationName) {
        violation.setViolationName(violationName);
        return this;
    }

    public ViolationBuilder withPenaltyFee(Double penaltyFee) {
        violation.setPenaltyFee(penaltyFee);
        return this;
    }

    public ViolationBuilder withCode(String code) {
        violation.setCode(code);
        return this;
    }

    public ViolationBuilder withOffenseNumber(String offenseNumber) {
        violation.setOffenseNumber(offenseNumber);
        return this;
    }

    public ViolationBuilder withDateOfViolation(String dateOfViolation) {
        violation.setDateOfViolation(dateOfViolation);
        return this;
    }

    public ViolationBuilder withDateOfPayment(String dateOfPayment) {
        violation.setDateOfPayment(dateOfPayment);
        return this;
    }

    public ViolationBuilder isPenaltyFeePaid(Boolean isPaid) {
        violation.setPaid(isPaid);
        return this;
    }

    public ViolationBuilder hasTransactionNumberOf(String transactionNumber) {
        violation.setTransactionNumber(transactionNumber);
        return this;
    }

    public Violation create() {
        return violation;
    }
}
