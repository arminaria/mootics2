package modules.importWizard;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import utils.OsCheck;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Backend {

    public RemoteWebDriver get(){
        try {
            URL resource = null;
            OsCheck.OSType ostype=OsCheck.getOperatingSystemType();
            switch (ostype) {
                case Windows:
                    resource = this.getClass().getResource("/driver/chromedriver.exe");
                    break;
                case MacOS:
                    resource = this.getClass().getResource("/driver/chromedrivermac");
                    break;
                case Linux:
                    resource = this.getClass().getResource("/driver/chromedriver");
                    break;
                case Other:
                    throw new RuntimeException("We do not support your OS");
            }

            String path = resource.getPath();

            DesiredCapabilities capability = new DesiredCapabilities();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized", "--ignore-certificate-errors", "--disable-images");
            capability.setCapability(ChromeOptions.CAPABILITY, options);
            capability.setBrowserName("chrome");
            capability.setCapability("chrome.binary", path);

            DriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(path))
                    .usingAnyFreePort().build();

            service.start();

            URL hubUrl = service.getUrl();

            return  new RemoteWebDriver(hubUrl, capability);
        } catch (IOException e) {
            throw new RuntimeException("could not start driver", e);
        }
    }
}
