package mmda.mmdamobilepayment.violation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import mmda.mmdamobilepayment.data.model.Violation;
import mmda.mmdamobilepayment.drawer.NavigationDrawerActivity;
import mmda.mmdamobilepayment.R;


public class ViolationAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Violation> violations;

    public ViolationAdapter(Context context, ArrayList<Violation> violations) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.violation_card_view, parent,
                    false);
        }

        final Violation violation = (Violation) this.getItem(position);

        DecimalFormat df = new DecimalFormat("###,###.00");

        String violationName = violation.getViolationName();
        String penaltyFee = context.getString(R.string.penalty_fee) + " Php " + df.format(violation.getPenaltyFee());
        String code = violation.getCode();
        String offense = "Offense: " + violation.getOffenseNumber();
        String dateOfViolation = violation.getDateOfViolation();
        String formattedDateOfViolation = context.getString(R.string.date_of_violation) + " " + dateOfViolation;
         //      + ((dateOfViolation != null) ? formatter.format(dateOfViolation.getTime()) : "");

        TextView violationNameTxt = convertView.findViewById(R.id.violationName);
        TextView codeTxt = convertView.findViewById(R.id.code);
        TextView offenseTxt = convertView.findViewById(R.id.offense);
        TextView penaltyFeeTxt = convertView.findViewById(R.id.penaltyFee);
        TextView dateOfViolationTxt = convertView.findViewById(R.id.dateOfViolation);

        violationNameTxt.setText(violationName);
        codeTxt.setText(code);
        offenseTxt.setText(offense);
        penaltyFeeTxt.setText(penaltyFee);
        dateOfViolationTxt.setText(formattedDateOfViolation);

        Button paymentButton = convertView.findViewById(R.id.paymentButton);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof NavigationDrawerActivity) {
                    ((NavigationDrawerActivity) context).payToPayPal(violation);
                }
            }
        });

        return convertView;

    }

}
