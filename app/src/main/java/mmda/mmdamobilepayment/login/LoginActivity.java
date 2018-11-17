package mmda.mmdamobilepayment.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import mmda.mmdamobilepayment.BuildConfig;
import mmda.mmdamobilepayment.drawer.NavigationDrawerActivity;
import mmda.mmdamobilepayment.R;
import mmda.mmdamobilepayment.register.RegisterActivity;


public class LoginActivity extends Activity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }


    public void attemptLogin(View view) {

        EditText licenseNumberEt = findViewById(R.id.licenseNumber);
        EditText passwordEt = findViewById(R.id.password);

        if (isInputValid(licenseNumberEt, passwordEt)) {
            UserLoginTask userLoginTask = new UserLoginTask(this);
            userLoginTask.execute(licenseNumberEt.getText().toString(),
                    passwordEt.getText().toString());
        } else {
            Toast.makeText(this, "Driver's License or Password is empty.",
                    Toast.LENGTH_LONG).show();
        }

    }

    public Boolean isInputValid(EditText licenseNumberEt, EditText passwordEt) {

        return (licenseNumberEt.getText() != null &&
                !licenseNumberEt.getText().toString().isEmpty() &&
                passwordEt.getText() != null &&
                !passwordEt.getText().toString().isEmpty());

    }

    public void goToNavigationDrawerActivity(String license_number, String password) {

        setTheme(R.style.ActionBarTheme);

        Intent intent = new Intent(this, NavigationDrawerActivity.class);
        intent.putExtra("license_number", license_number);
        intent.putExtra("password", password);
        startActivity(intent);

    }

    public void goToRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public static class UserLoginTask extends AsyncTask<String, Void, String> {

        private WeakReference<LoginActivity> activityReference;
        private LoginActivity activity;
        private ProgressDialog progressDialog;

        private String license_number;
        private String password;

        UserLoginTask(LoginActivity context) {
            this.activityReference = new WeakReference<>(context);

            // get a reference to the activity if it is still there
            activity = activityReference.get();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Attempting to login. Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            license_number = params[0];
            password = params[1];

            try {

                String attemptLoginData = URLEncoder.encode("license_number", "UTF-8") + "=" +
                        URLEncoder.encode(license_number, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");

                String strURL = BuildConfig.ENDPOINT_URL + "/api/login/?" + attemptLoginData;

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

                    return sb.toString();

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
        protected void onPostExecute(String result) {

            if (result != null && !result.isEmpty()) {

                try {
                    Log.d("test", result);
                    JSONObject response = new JSONObject(result);

                    if (response.getString("message").equals("success")) {

                        activity.goToNavigationDrawerActivity(license_number, password);

                        Toast.makeText(activity, "Login Success!",
                                Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(activity, "Login Failed. " +
                                        "Your license number or password is wrong.",
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(activity, "Oops! There might be a problem. Please contact support."
                        , Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
        }
    }
}

