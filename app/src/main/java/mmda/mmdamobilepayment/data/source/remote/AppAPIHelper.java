package mmda.mmdamobilepayment.data.source.remote;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import mmda.mmdamobilepayment.BasePresenter;
import mmda.mmdamobilepayment.BuildConfig;
import mmda.mmdamobilepayment.data.model.Driver;
import mmda.mmdamobilepayment.data.model.Payment;

/**
 * Created by Raniel on 11/17/2018.
 */

public class AppAPIHelper extends AsyncTask<Object, Void, String> {

    private final BasePresenter presenter;

    public AppAPIHelper(BasePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected String doInBackground(Object... objects) {

        String purpose = (String) objects[0];
        String postData = "";
        StringBuilder stringURL = new StringBuilder(BuildConfig.ENDPOINT_URL);

        Driver driver = (objects[1] instanceof Driver) ? (Driver) objects[1] : null;
        Payment payment = (objects[1] instanceof Payment) ? (Payment) objects[1] : null;

        try {

            if (purpose.equals("Login")) {

                postData = encodeUrl(driver.getLicenseNumber(), driver.getPassword());
                stringURL.append("/api/login/");

            } else if (purpose.equals("Register")) {

                postData = driver.toString();
                stringURL.append("/api/driver/create.php");

            } else if (purpose.equals("GetDriverDetails")) {

                postData = encodeUrl(driver.getLicenseNumber(), driver.getPassword());
                stringURL.append("/api/driver/read.php");

            } else if (purpose.equals("VerifyPayment")) {

                postData = payment.toJSON();
                stringURL.append("/api/payment/");

            }

            Log.d("api", stringURL.toString());

            URL url = new URL(stringURL.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(urlConnection.getOutputStream()));
            writer.write(postData);
            writer.close();

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

    private String encodeUrl(String licenseNumber, String password) throws UnsupportedEncodingException {
        return URLEncoder.encode("license_number", "UTF-8") + "=" +
                URLEncoder.encode(licenseNumber, "UTF-8") + "&" +
                URLEncoder.encode("password", "UTF-8") + "=" +
                URLEncoder.encode(password, "UTF-8");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s != null && !s.isEmpty()) {
            presenter.processResponse(s);
        } else {
            Log.d("api", "No response");
        }

    }
}
