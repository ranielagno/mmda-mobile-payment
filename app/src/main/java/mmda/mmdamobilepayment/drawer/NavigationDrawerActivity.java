package mmda.mmdamobilepayment.drawer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mmda.mmdamobilepayment.BuildConfig;
import mmda.mmdamobilepayment.R;
import mmda.mmdamobilepayment.data.model.Driver;
import mmda.mmdamobilepayment.login.LoginActivity;
import mmda.mmdamobilepayment.data.model.Payment;
import mmda.mmdamobilepayment.history.PaymentHistoryFragment;
import mmda.mmdamobilepayment.profile.ProfileFragment;
import mmda.mmdamobilepayment.data.model.Violation;
import mmda.mmdamobilepayment.violation.ViolationFragment;

import static mmda.mmdamobilepayment.data.model.DriverBuilder.driver;
import static mmda.mmdamobilepayment.data.model.ViolationBuilder.violation;

public class NavigationDrawerActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private final String clientId = BuildConfig.PAYPAL_CLIENT_ID;
    private final String clientSecret = BuildConfig.PAYPAL_SECRET_ID;

    private Driver driver;
    private ArrayList<Violation> unPaidViolations = new ArrayList<>();
    private ArrayList<Violation> paidViolations = new ArrayList<>();

    private Payment payment;

    private final PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment (ENVIRONMENT_NO_NETWORK).
            // When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId(clientId);


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            GetDriverDetails task = new GetDriverDetails(this);
            task.execute(extras.getString("license_number"));
        } else {
            Log.d("api", "Extras is null");
        }

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);


    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        if (position == 0) {
            fragment = ViolationFragment.newInstance();
        } else if (position == 1) {
            fragment = ProfileFragment.newInstance();
        } else if (position == 2) {
            fragment = PaymentHistoryFragment.newInstance();
        } else if (position == 3) {
            showLogoutDialog();
        }

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

    }

    private void showLogoutDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logOut();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void logOut() {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

            if (confirm != null) {
                try {
                    Log.i("payPal", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                    JSONObject payPalResponse = confirm.toJSONObject().getJSONObject("response");
                    String payment_id = payPalResponse.getString("id");
                    Log.d("payPal", "Payment Id: " + payment_id);

                    payment.setPaymentId(payment_id);

                    VerifyPaymentTask task = new VerifyPaymentTask(this);
                    task.execute(payment);

                } catch (JSONException e) {
                    Log.e("payPal", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("payPal", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("payPal", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                restoreActionBar();
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                restoreActionBar();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                restoreActionBar();
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                restoreActionBar();
                break;
        }
    }

    public void restoreActionBar() {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void payToPayPal(Violation violation) {

        String violationName = "Traffic Violation (" + violation.getViolationName() + ") Fine";
        Double penaltyFee = violation.getPenaltyFee();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("###,###.00");
        Date date = Calendar.getInstance().getTime();
        Log.d("Test", String.valueOf(penaltyFee));
        payment = new Payment();
        payment.setLicenseNumber(getIntent().getExtras().getString("license_number"));
        payment.setTicketId(violation.getTicketId());
        payment.setPaymentDate(dateFormatter.format(date));
        payment.setCost(decimalFormat.format(penaltyFee));
        payment.setCurrency("PHP");

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(penaltyFee), "PHP",
                violationName, PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);

        startActivityForResult(intent, 0);

    }

    public Driver getDriver() {
        return driver;
    }

    public ArrayList<Violation> getUnPaidViolations() {
        return unPaidViolations;
    }

    public ArrayList<Violation> getPaidViolations() {
        return paidViolations;
    }

    private void processJSONResponse(String json) {
        Log.d("api", json);

        try {

            JSONObject response = new JSONObject(json);
            JSONObject driver = response.getJSONObject("driver");
            JSONArray unpaid_violations = response.has("unpaid tickets") ?
                    response.getJSONArray("unpaid tickets") : new JSONArray();
            JSONArray paid_violations = response.has("paid tickets") ?
                    response.getJSONArray("paid tickets") : new JSONArray();

            getDriverFromResponse(driver);
            getUnpaidViolationsFromResponse(unpaid_violations);
            getPaidViolationsFromResponse(paid_violations);

            onNavigationDrawerItemSelected(0);
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDriverFromResponse(JSONObject driverResponse) throws JSONException {

        Bundle extras = getIntent().getExtras();
        String password = "";

        if (extras != null) {
            password = extras.getString("password");
        }

        String license_number = driverResponse.getString("license_number");
        String plate_number = driverResponse.getString("plate_number");
        String first_name = driverResponse.getString("first_name");
        String middle_name = driverResponse.getString("middle_name");
        String last_name = driverResponse.getString("last_name");
        String address = driverResponse.getString("address");
        String contact_number = driverResponse.getString("contact_number");
        String birthday = driverResponse.getString("birthday");
        String gender = driverResponse.getString("gender");

        driver = driver(license_number)
                .withName(first_name, middle_name, last_name)
                .ownVehicleWithPlateNumber(plate_number)
                .withPassword(password)
                .livesOnAddress(address)
                .withContactNumber(contact_number)
                .hasBirthdayOf(birthday)
                .hasGenderOf(gender)
                .create();

        Log.d("api", "Driver:" + driver.getLicenseNumber());
    }

    private void getUnpaidViolationsFromResponse(JSONArray unpaid_violations) throws JSONException {

        if (unPaidViolations.size() > 0)
            unPaidViolations.clear();

        Log.d("api", "Unpaid:" + unpaid_violations.length());

        for (int i = 0; i < unpaid_violations.length(); i++) {
            JSONObject unpaid_violation = (JSONObject) unpaid_violations.get(i);

            int ticket_id = unpaid_violation.getInt("ticket_id");
            String violationName = unpaid_violation.getString("violation");
            String penaltyStr = unpaid_violation.getString("penalty");
            Double penaltyFee = (penaltyStr.isEmpty()) ? 0.00 : Double.valueOf(penaltyStr.substring(1));
            String ticketDate = unpaid_violation.getString("ticket_date");
            String code = unpaid_violation.getString("code");
            String offenseNumber = unpaid_violation.getString("offense");

            Violation ticket = violation(ticket_id)
                    .withViolationName(violationName)
                    .withPenaltyFee(penaltyFee)
                    .withCode(code)
                    .withOffenseNumber(offenseNumber)
                    .withDateOfViolation(ticketDate)
                    .create();

            unPaidViolations.add(ticket);
        }

        Log.d("api", "Unpaid:" + unPaidViolations.toString());

    }

    private void getPaidViolationsFromResponse(JSONArray paid_violations) throws JSONException {

        if (paidViolations.size() > 0)
            paidViolations.clear();

        Log.d("api", "Paid:" + paid_violations.length());

        for (int i = 0; i < paid_violations.length(); i++) {
            JSONObject paid_violation = (JSONObject) paid_violations.get(i);

            int ticket_id = paid_violation.getInt("ticket_id");
            String violationName = paid_violation.getString("violation");
            String penaltyStr = paid_violation.getString("penalty");
            Double penaltyFee = (penaltyStr.isEmpty()) ? 0.00 : Double.valueOf(penaltyStr.substring(1));
            String ticketDate = paid_violation.getString("ticket_date");
            String code = paid_violation.getString("code");
            String offenseNumber = paid_violation.getString("offense");
            String transactionNumber = paid_violation.getString("transaction_number");
            String transactionDate = paid_violation.getString("transaction_date");

            Violation ticket = violation(ticket_id)
                    .withViolationName(violationName)
                    .withPenaltyFee(penaltyFee)
                    .withCode(code)
                    .withOffenseNumber(offenseNumber)
                    .withDateOfViolation(ticketDate)
                    .withDateOfPayment(transactionDate)
                    .hasTransactionNumberOf(transactionNumber)
                    .isPenaltyFeePaid(true)
                    .create();

            paidViolations.add(ticket);
        }

        Log.d("api", "Paid:" + paidViolations.toString());

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((NavigationDrawerActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private static class GetDriverDetails extends AsyncTask<String, Void, String> {

        private WeakReference<NavigationDrawerActivity> activityReference;
        private NavigationDrawerActivity activity;

        public GetDriverDetails(NavigationDrawerActivity context) {

            this.activityReference = new WeakReference<>(context);

            // get a reference to the activity if it is still there
            activity = activityReference.get();
        }

        @Override
        protected String doInBackground(String... strings) {

            String license_number = strings[0];

            try {

                String get_data = URLEncoder.encode("license_number", "UTF-8") + "=" +
                        URLEncoder.encode(license_number, "UTF-8");
                String strURL = BuildConfig.ENDPOINT_URL + "/api/driver/read.php?" + get_data;

                Log.d("api", strURL);

                URL url = new URL(strURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int status = urlConnection.getResponseCode();

                if (status == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                    String line = "";
                    StringBuilder sb = new StringBuilder();

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    String result = sb.toString();

                    return result;
                } else {
                    Log.d("api", "Response code:" + status);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.isEmpty()) {
                activity.processJSONResponse(s);
            } else {
                Log.d("api", "No response");
            }

        }
    }

    private static class VerifyPaymentTask extends AsyncTask<Payment, Void, String> {

        private WeakReference<NavigationDrawerActivity> activityReference;
        private NavigationDrawerActivity activity;
        private ProgressDialog progressDialog;

        public VerifyPaymentTask(NavigationDrawerActivity context) {

            this.activityReference = new WeakReference<>(context);

            // get a reference to the activity if it is still there
            activity = activityReference.get();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Verifying payment. Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Payment... payments) {

            String strURL = BuildConfig.ENDPOINT_URL + "/api/payment/";

            try {

                URL url = new URL(strURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(urlConnection.getOutputStream()));
                writer.write(payments[0].toJSON());
                Log.d("test", payments[0].toJSON());
                writer.close();

                int status = urlConnection.getResponseCode();
                Log.d("test", String.valueOf(status));

                if (status == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));

                    String line = "";
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    reader.close();
                    String result = sb.toString();
                    Log.d("test", "hello");
                    Log.d("test", result);

                    return result;

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.isEmpty()) {

                Log.d("test", s);

                activity.processJSONResponse(s);

                progressDialog.dismiss();

                Toast.makeText(activity, "Payment Successful!", Toast.LENGTH_LONG).show();

            }
        }
    }

}
