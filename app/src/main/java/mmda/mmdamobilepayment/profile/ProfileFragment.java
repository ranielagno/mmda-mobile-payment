package mmda.mmdamobilepayment.profile;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import mmda.mmdamobilepayment.BuildConfig;
import mmda.mmdamobilepayment.drawer.NavigationDrawerActivity;
import mmda.mmdamobilepayment.R;
import mmda.mmdamobilepayment.data.model.Driver;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private boolean isEditMode;
    private Driver driver;

    private EditText passwordEt;
    private EditText addressEt;
    private EditText birthDateEt;
    private EditText contactNumberEt;

    private RadioGroup rg;
    private RadioButton radioMale;
    private RadioButton radioFemale;

    private NavigationDrawerActivity context;

    public static Fragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.setVerticalScrollBarEnabled(false);

        EditText licenseNumberEt = view.findViewById(R.id.licenseNumber);
        EditText firstNameEt = view.findViewById(R.id.firstName);
        EditText middleNameEt = view.findViewById(R.id.middleName);
        EditText lastNameEt = view.findViewById(R.id.lastName);
        EditText plateNumberEt = view.findViewById(R.id.plateNumber);
        passwordEt = view.findViewById(R.id.password);
        addressEt = view.findViewById(R.id.address);
        birthDateEt = view.findViewById(R.id.birthDate);
        contactNumberEt = view.findViewById(R.id.contactNumber);

        rg = view.findViewById(R.id.gender);
        radioMale = view.findViewById(R.id.radioMale);
        radioFemale = view.findViewById(R.id.radioFemale);

        context = (NavigationDrawerActivity) view.getContext();
        driver = context.getDriver();

        if (driver != null) {
            licenseNumberEt.setText(driver.getLicenseNumber());
            firstNameEt.setText(driver.getFirstName());
            middleNameEt.setText(driver.getMiddleName());
            lastNameEt.setText(driver.getLastName());
            plateNumberEt.setText(driver.getPlateNumber());
            passwordEt.setText(driver.getPassword());
            addressEt.setText(driver.getAddress());
            birthDateEt.setText(driver.getBirthdate());
            contactNumberEt.setText(driver.getContactNumber());

            boolean isMale = driver.getGender().equals("M");
            radioMale.setChecked(isMale);
            radioFemale.setChecked(!isMale);

        }

        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDriverProfile(v);
            }
        });

        setupDateTimePicker(view.getContext(), birthDateEt);
    }

    public void setupDateTimePicker(Context context, final EditText birthDateEt) {

        try {

            final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar calendar = Calendar.getInstance();

            String strBirthdate = birthDateEt.getText().toString();

            if (!strBirthdate.isEmpty())
                calendar.setTime(dateFormatter.parse(strBirthdate));
            else
                calendar.getTime();

            final DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    birthDateEt.setText(dateFormatter.format(newDate.getTime()));
                }
            }, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            birthDateEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditMode) {
                        datePickerDialog.show();
                    }
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void editDriverProfile(View view) {

        ArrayList<String> notEditable = new ArrayList<>(Arrays.asList("licenseNumber",
                "plateNumber", "firstName", "middleName", "lastName", "birthDate"));

        Button button = (Button) view;
        String buttonText = button.getText().toString();
        isEditMode = buttonText.equals("Edit");

        String newButtonText = (isEditMode) ? "Save" : "Edit";
        button.setText(newButtonText);

        LinearLayout parentLinearLayout = (LinearLayout) view.getParent();
        LinearLayout inputLinearLayout = (LinearLayout) parentLinearLayout.getChildAt(1);

        for (int i = 0; i < inputLinearLayout.getChildCount(); i++) {

            if (inputLinearLayout.getChildAt(i) instanceof EditText) {

                EditText editText = (EditText) inputLinearLayout.getChildAt(i);
                String idName = view.getResources().getResourceEntryName(editText.getId());
                if (!notEditable.contains(idName)) {
                    editText.setFocusableInTouchMode(isEditMode);
                    editText.setFocusable(isEditMode);
                }
            }
        }

        RadioButton radioMale = inputLinearLayout.findViewById(R.id.radioMale);
        RadioButton radioFemale = inputLinearLayout.findViewById(R.id.radioFemale);

        radioMale.setClickable(isEditMode);
        radioFemale.setClickable(isEditMode);

        if (!isEditMode) {

            driver.setPassword(passwordEt.getText().toString());
            driver.setAddress(addressEt.getText().toString());
            driver.setBirthdate(birthDateEt.getText().toString());
            driver.setContactNumber(contactNumberEt.getText().toString());
            driver.setGender((rg.getCheckedRadioButtonId() == radioMale.getId()) ? "M" : "F");

            UpdateDriverInfoTask task = new UpdateDriverInfoTask(context);
            task.execute(driver);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        /*
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private static class UpdateDriverInfoTask extends AsyncTask<Driver, Void, String> {

        private WeakReference<NavigationDrawerActivity> activityWeakReference;
        private NavigationDrawerActivity activity;

        public UpdateDriverInfoTask(NavigationDrawerActivity activity) {
            this.activityWeakReference = new WeakReference<NavigationDrawerActivity>(activity);
            this.activity = activityWeakReference.get();
        }

        @Override
        protected String doInBackground(Driver... drivers) {

            String strURL = BuildConfig.ENDPOINT_URL + "/api/driver/update.php";

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
                    Toast.makeText(activity, "Update Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "Update Unsuccessful. " + s, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
