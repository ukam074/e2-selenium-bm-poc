package webdriver.user;

public class E2User {

  public static final String TEST_USER_PASSWORD = "Password#1";

  /**
   * Use the default constructor to initialize the user with a default password, zip code and
   * primare phone number.
   */
  public E2User() {
    // this is the password used by most E2 webdriver.tests, so it is set as the default
    setPassword("Password#1");

    // apply other default values
    setZipCode("78228");
    setPrimaryPhoneNumber("2102263232");
  }

  public E2User(String username, String firstName, String lastName, String primaryEmail) {
    this();
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.primaryEmail = primaryEmail;
  }

  private String addressLine1;

  private String addressLine2;

  private String firstName;

  private String middleInitial;

  private String lastName;

  private String newUserLink;

  private String password;

  private String primaryPhoneNumber;

  private String primaryEmail;

  private String username;

  private String workPhone;

  private String city;

  private String state;

  private String zipCode;

  private String suffix;

  public String getAddressLine1() {
    return addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getMiddleInitial() {
    return middleInitial;
  }

  public String getLastName() {
    return lastName;
  }

  public String getNewUserLink() {
    return newUserLink;
  }

  public String getPassword() {
    return password;
  }

  public String getPrimaryEmail() {
    return primaryEmail;
  }

  public String getPrimaryPhoneNumber() {
    return primaryPhoneNumber;
  }

  public String getUsername() {
    return username;
  }

  public String getWorkPhone() {
    return workPhone;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setMiddleInitial(String middleInitial) {
    this.middleInitial = middleInitial;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setNewUserLink(String newUserLink) {
    this.newUserLink = newUserLink;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPrimaryEmail(String primaryEmail) {
    this.primaryEmail = primaryEmail;
  }

  public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
    this.primaryPhoneNumber = primaryPhoneNumber;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setWorkPhone(String workPhone) {
    this.workPhone = workPhone;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public String toString() {
    return username;
  }

}
