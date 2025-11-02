import co.verisoft.fw.extentreport.ExtentReport;
import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import co.verisoft.fw.selenium.junit.extensions.DriverInjectionExtension;
import co.verisoft.fw.selenium.junit.extensions.ScreenShotExtension;
import co.verisoft.fw.selenium.junit.extensions.SeleniumLogExtesion;
import config.TryAI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtentReport
@ExtendWith({DriverInjectionExtension.class, SeleniumLogExtesion.class, ScreenShotExtension.class})
public class BaseTest {

}
