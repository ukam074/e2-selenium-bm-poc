
package webdriver.user.service;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import webdriver.user.E2User;

/**
 * This class provides E2 user specific methods for Web-based CRUD operations,
 * as well as methods for retrieving user specific demographics.
 *
 * @author Roberto Marquez
 */
public interface UserService {
  String CLONE_SE_DASH_PREFIX = "SE-";
  String CLONE_SE_DOT_PREFIX = "SE.";

  String cloneUser(String travelerLogname) throws Exception;

  /**
   * @param sourceUser    The source user to copy fields from the E2 database into a
   *                      new (cloned) E2 user.
   * @param cloneUsername The new E2 user that contains the source user's copied
   *                      information.
   *                      <p>
   *                      Any non-null fields in the E2User object overwrite the
   *                      E2 user information from the database.
   * @return The response (OUT parameter) from the database.
   * @throws Exception
   */
  String cloneUser(E2User sourceUser, String cloneUsername) throws Exception;

  String cloneUser(E2User sourceUser, String cloneUsername, String cloneFirstName, String
      cloneLastName) throws Exception;

  String cloneApprover(E2User sourceUser, String cloneUsername) throws Exception;

  /**
   * Delete the user with the specified username ("logname" in the database).
   *
   * @param username  The logname of the user
   * @return A success or failure message returned by the database procedure
   */
  String deleteUser(String username);

  /**
   * Retrieves the E2 user for the given user name,
   * with only the first and last name populated from the database.
   *
   * @param username
   * @return an E2 user
   */
  E2User retrieveUser(String username) throws Exception;

  /**
   * @return a email address using the provided user
   */
  default String testingEmail(String username) {
    String email = username + "@cwt-multipass.com";

    return email;
  }

  default String uniqueUsername() {
    Random randomNumber = new Random(System.nanoTime());
    long timestamp = randomNumber.nextLong();
    String username = CLONE_SE_DASH_PREFIX + timestamp;

    return username;
  }

  default String uniqueUsername(int usernameLength) {
    return RandomStringUtils
        .random(usernameLength, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789").toUpperCase();
  }
  /**
   * Returns a 9 character randomized username for Delphi compliance
   * @return The 9 character username
   */
  default String uniqueShortUsername() {
    String username = uniqueUsername();
    username = username.substring(0, Math.min(username.length(), 9));

    return username;
  }


  /**
   * Clones the specified user.
   *
   * @param travelerUserName the E2 logname for the source user to be cloned
   */
  default String setUpClonedUser(String travelerUserName) throws Exception{
    String clonedUserName = uniqueUsername();
    E2User originalTraveler = retrieveUser(travelerUserName);
    if (!StringUtils.equals("OK", cloneUser(originalTraveler, clonedUserName))) {
      fail("Failed to clone user!");
    }
    System.out.println("using cloned user: " + clonedUserName);
    return clonedUserName;
  }

  String retrieveLatestNewUserLink(String emailAddress);// throws Exception;

  void unlockUser(String logname) throws Exception;

  List<String> getUserPermissions(String userName) throws SQLException;

  int deleteUserPermissions(List<String> perms, String userName) throws SQLException;

  int insertUserPermissions(List<String> perms, String userName) throws SQLException;
}
