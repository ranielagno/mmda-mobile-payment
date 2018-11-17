package mmda.mmdamobilepayment.register;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mmda.mmdamobilepayment.BuildConfig;
import mmda.mmdamobilepayment.R;
import mmda.mmdamobilepayment.data.model.Driver;
import mmda.mmdamobilepayment.drawer.NavigationDrawerActivity;
import mmda.mmdamobilepayment.login.LoginActivity;

import static mmda.mmdamobilepayment.data.model.DriverBuilder.driver;

public class RegisterActivity extends Activity {

    private EditText birthDateEt;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        birthDateEt = findViewById(R.id.birthDate);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(
                RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                birthDateEt.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    }

    public void showDatePickerDialog(View view) {
        datePickerDialog.show();
    }

    public void registerDriver(View view) {

        String licenseNumber = ((EditText) findViewById(R.id.licenseNumber)).getText().toString();
        String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString();
        String middleName = ((EditText) findViewById(R.id.middleName)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String plateNumber = ((EditText) findViewById(R.id.plateNumber)).getText().toString();
        String contactNumber = ((EditText) findViewById(R.id.contactNumber)).getText().toString();
        String address = ((EditText) findViewById(R.id.address)).getText().toString();
        String birthdate = birthDateEt.getText().toString();
        RadioGroup rg = findViewById(R.id.gender);
        RadioButton rb = findViewById(rg.getCheckedRadioButtonId());

        String gender = (String) rb.getText();

        Driver driver = driver(licenseNumber)
                .withName(firstName, middleName, lastName)
                .withPassword(password)
                .ownVehicleWithPlateNumber(plateNumber)
                .livesOnAddress(address)
                .withContactNumber(contactNumber)
                .hasBirthdayOf(birthdate)
                .hasGenderOf(gender)
                .create();

        RegisterDriverTask registerDriverTask = new RegisterDriverTask(this);
        registerDriverTask.execute(driver);

    }

    private void goToLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToNavigationDrawerActivity(String license_number, String password) {

        setTheme(R.style.ActionBarTheme);

        Intent intent = new Intent(this, NavigationDrawerActivity.class);
        intent.putExtra("license_number", license_number);
        intent.putExtra("password", password);
        startActivity(intent);

    }

    private static class RegisterDriverTask extends AsyncTask<Driver, Void, String> {

        private WeakReference<RegisterActivity> activityWeakReference;
        private RegisterActivity activity;

        private String license_number;
        private String password;

        public RegisterDriverTask(RegisterActivity activity) {
            this.activityWeakReference = new WeakReference<RegisterActivity>(activity);
            this.activity = activityWeakReference.get();
        }

        @Override
        protected String doInBackground(Driver... drivers) {

            String strURL = BuildConfig.ENDPOINT_URL + "/api/driver/create.php";

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
                writer.write(drivers[0].toJSON());
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

                    Log.d("test", result);

                    license_number = drivers[0].getLicenseNumber();
                    password = drivers[0].getPassword();

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

            if (s != null) {
                if (s.equals("Success")) {
                    Toast.makeText(activity, "Register Successful", Toast.LENGTH_LONG).show();
                    activity.goToNavigationDrawerActivity(license_number, password);
                } else {
                    Toast.makeText(activity, "Register Unsuccessful. " + s, Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
