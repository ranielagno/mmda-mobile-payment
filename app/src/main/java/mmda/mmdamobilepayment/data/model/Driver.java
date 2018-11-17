package mmda.mmdamobilepayment.data.model;

import org.json.JSONException;
import org.json.JSONObject;


public class Driver {

    private String licenseNumber;
    private String plateNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String password;
    private String contactNumber;
    private String address;
    private String birthdate;
    private String gender;

    public Driver(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        gender = (gender.equals("M") || gender.equals("Male")) ? "M" : "F";
        this.gender = gender;
    }

    public void setDriverAttr() {

        licenseNumber = "XXX-XX-XXXXXX";
        plateNumber = "ABC-1024";
        firstName = "Pedro";
        middleName = "M";
        lastName = "Penduko";
        password = "123";
        contactNumber = "09123456789";
        address = "Dito sa bahay";
        birthdate = "1997-07-12";
        gender = "M";

    }

    public String toJSON() {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("license_number", licenseNumber);
            jsonObject.put("password", password);

            if (!plateNumber.isEmpty() && plateNumber != null)
                jsonObject.put("plate_number", plateNumber);

            if (!firstName.isEmpty() && firstName != null)
                jsonObject.put("first_name", firstName);

            if (!middleName.isEmpty() && middleName != null)
                jsonObject.put("middle_name", middleName);

            if (!lastName.isEmpty() && lastName != null)
                jsonObject.put("last_name", lastName);

            if (!contactNumber.isEmpty() && contactNumber != null)
                jsonObject.put("contact_number", contactNumber);

            if (!birthdate.isEmpty() && birthdate != null)
                jsonObject.put("birthday", birthdate);

            if (!address.isEmpty() && address != null)
                jsonObject.put("address", address);

            if (!gender.isEmpty() && gender != null)
                jsonObject.put("gender", gender);

            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

}
