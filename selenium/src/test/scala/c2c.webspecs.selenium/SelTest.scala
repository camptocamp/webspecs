package selenium

import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriverBackedSelenium
import com.thoughtworks.selenium.Selenium
object SelTest {
  def main(args: Array[String]): Unit = {
    // You may use any WebDriver implementation. Firefox is used here as an example
    val driver = new FirefoxDriver();

    // A "base url", used by selenium to resolve relative URLs
    val baseUrl = "http://www.geocat.ch";

    // Create the Selenium implementation
    val selenium: Selenium = new WebDriverBackedSelenium(driver, baseUrl);

    // Perform actions with selenium
    selenium.open("geonetwork/srv/en/main.home");
    selenium.click("css=strong");

    //Finally, close the browser. Call stop on the WebDriverBackedSelenium instance
    //instead of calling driver.quit(). Otherwise, the JVM will continue running after
    //the browser has been closed.
    selenium.stop();
  }
}