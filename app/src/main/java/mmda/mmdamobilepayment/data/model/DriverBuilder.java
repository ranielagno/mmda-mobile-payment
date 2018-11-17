package mmda.mmdamobilepayment.data.model;

public class DriverBuilder {

    private final Driver driver;

    private DriverBuilder(String licenseNumber) {
        driver = new Driver(licenseNumber);
    }

    public static DriverBuilder driver(String licenseNumber) {
        return new DriverBuilder(licenseNumber);
    }

    public DriverBuilder withName(String firstName, String middleName, String lastName) {

        driver.setFirstName(firstName);
        driver.setMiddleName(middleName);
        driver.setLastName(lastName);

        return this;
    }

    public DriverBuilder ownVehicleWithPlateNumber(String plateNumber) {
        driver.setPlateNumber(plateNumber);
        return this;
    }

    public DriverBuilder withPassword(String password) {
        driver.setPassword(password);
        return this;
    }

    public DriverBuilder withContactNumber(String contactNumber) {
        driver.setContactNumber(contactNumber);
        return this;
    }

    public DriverBuilder livesOnAddress(String address) {
        driver.setAddress(address);
        return this;
    }

    public DriverBuilder hasBirthdayOf(String birthdate) {
        driver.setBirthdate(birthdate);
        return this;
    }

    public DriverBuilder hasGenderOf(String gender) {
        driver.setGender(gender);
        return this;
    }

    public Driver create() {
        return driver;
    }

}
