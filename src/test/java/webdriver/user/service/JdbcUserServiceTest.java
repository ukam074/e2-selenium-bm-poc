
package webdriver.user.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import webdriver.tests.AbstractBaseIT;

@RunWith(Parameterized.class)
public class JdbcUserServiceTest extends AbstractBaseIT
{
	private boolean expectedResult;
	
	private String emailLike;
	
	public JdbcUserServiceTest(String emailLike, boolean expectedResult) 
	{
		this.emailLike = emailLike;
		this.expectedResult = expectedResult;
	}
	
	//TODO: can this be used to have a data provider per method
	//		and not by class constructor?
	//
	//			https://dzone.com/articles/alternative-junit
	@Parameters(name="{index} - emailLike: {0}")
	public static Collection<Object[]> data() throws Exception
	{
		List data = new ArrayList();
		
		String passEmail = "d"; // this assumes at least one email address in the new user link table starts with the letter 'd'
		String failEmail = String.valueOf(Calendar.getInstance().getTimeInMillis());
		
		//                emailLike,  shouldPass		
		Object [] row1 = {passEmail,  true};
		Object [] row2 = {failEmail,  false};
		data.add(row1);
		data.add(row2);	
		
		return data;
	}	
	
	@Test
	/**
	 * This test that an URL is available for the given email address.
	 * 
	 * To run just this command from the command line issue this:
	 * 
	 * 		$ mvn -P QA,CHROME -Dit.test=CPETestsIT#retreiveLatestNewUserLink integration-test
	 * 
	 * @throws Exception
	 */
	public void retreiveLatestNewUserLink()
	{
		// look for any email address that starts with 'd'
//		String emailLike = "d";
//		String emailLike = "destrada@cwtsatotravel.com";
		
		String link = "";
		
		try
		{
			link = userService.retrieveLatestNewUserLink(emailLike);
		}
		catch(IllegalArgumentException e)
		{
			logger.log(Level.INFO, "No records found: "  + emailLike, e);
		}
				
		assertTrue( link.contains("http") == expectedResult );
	}	
}
