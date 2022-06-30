
package webdriver.tests.puff;

import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import webdriver.pages.e2.LoginPage;
import webdriver.pages.e2.profile.ProfilePage;
import webdriver.tests.AbstractBaseIT;

public class PuffTest extends AbstractBaseIT {

	@Rule
	public TestRule watcher = new TestWatcher(){
		protected void starting(Description description){
			System.out.println("\n\n-----------------------------------------------------");
			System.out.println("**TEST NAME: " + description.getMethodName());
		}
	};

	@Test
	public void loginPageLoads() {
		
		// -------------------------------
		// Assertion Login Page loads
		// -------------------------------
		LoginPage loginPage = new LoginPage(driver);
		assertTrue("No Name Field",loginPage.isUserNameFieldPresent());
	}

	@Test
	public void openProfilePageTest() {
		
		// -------------------------------
		// Login as a traveler and navigate to the Profile Page
		// -------------------------------
		LoginPage loginPage = new LoginPage(driver);
		loginPage.setUsernameAndPassword("SE-585866322518925655","Password#1");
		sleep(1000);
		driver.findElement(By.id("Submit")).click();
		sleep(5000);
		driver.findElement(By.id("privacyActAccept")).click();
		sleep(5000);
		ProfilePage profilePage = new ProfilePage(driver);
		profilePage.clickProfile();

		// -------------------------------
		// Assertion verifying the profile page is loaded
		// -------------------------------
		boolean edit_profile = driver.findElementsByLinkText("Edit Profile").size() != 0;
		assertTrue("Profile page is not displayed",edit_profile);
	}
}
