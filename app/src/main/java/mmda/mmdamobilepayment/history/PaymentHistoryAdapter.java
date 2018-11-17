package mmda.mmdamobilepayment.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import mmda.mmdamobilepayment.R;
import mmda.mmdamobilepayment.data.model.Violation;


public class PaymentHistoryAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Violation> violations;

    public PaymentHistoryAdapter(Context context, ArrayList<Violation> violations) {
        this.context = context;
        this.violations = violations;
    }

    @Override
    public int getCount() {
        return violations.size();
    }

    @Override
    public Object getItem(int position) {
        return violations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.payment_history_card_view,
                    parent, false);
        }

        final Violation violation = (Violation) this.getItem(position);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        DecimalFormat df = new DecimalFormat("###,###.00");

        String violationName = violation.getViolationName();
        String code = violation.getCode();
        String offense = "Offense: " + violation.getOffenseNumber();
        String penaltyFee = context.getString(R.string.penalty_fee) + " Php " + df.format(violation.getPenaltyFee());

        String dateOfViolation = violation.getDateOfViolation();
        String formattedDateOfViolation = context.getString(R.string.date_of_violation) + " " + dateOfViolation;
        //        + ((dateOfViolation != null) ? formatter.format(dateOfViolation.getTime()) : "");

        String transactionNumber = context.getString(R.string.transaction_number) + " "
                + violation.getTransactionNumber();

        String dateOfPayment = violation.getDateOfPayment();
        String formattedDateOfPayment = context.getString(R.string.date_of_payment) + " " + dateOfPayment;
        //        + ((dateOfPayment != null) ? formatter.format(dateOfPayment.getTime()) : "");

        TextView violationNameTxt = convertView.findViewById(R.id.violationName);
        TextView codeTxt = convertView.findViewById(R.id.code);
        TextView offenseTxt = convertView.findViewById(R.id.offense);
        TextView penaltyFeeTxt = convertView.findViewById(R.id.penaltyFee);
        TextView dateOfViolationTxt = convertView.findViewById(R.id.dateOfViolation);
        TextView transactionNumberTxt = convertView.findViewById(R.id.transactionNumber);
        TextView dateOfPaymentTxt = convertView.findViewById(R.id.dateOfPayment);

        violationNameTxt.setText(violationName);
        codeTxt.setText(code);
        offenseTxt.setText(offense);
        penaltyFeeTxt.setText(penaltyFee);
        dateOfViolationTxt.setText(formattedDateOfViolation);
        transactionNumberTxt.setText(transactionNumber);
        dateOfPaymentTxt.setText(formattedDateOfPayment);

        return convertView;

    }

}
