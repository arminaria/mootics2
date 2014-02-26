package modules.importWizard.controller;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import controller.DBController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import model.Data;
import model.Material;
import modules.importWizard.Backend;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CategoryUtil;
import utils.RegexUtils;

import java.util.ArrayList;
import java.util.List;

public class MaterialImportController {

    public TextArea output;
    Logger log = LoggerFactory.getLogger(MaterialImportController.class);

    public void start() {
        output.appendText("Browser is starting, please wait ...\n");
        new Thread(createWorker()).start();
    }

    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                Backend backend = new Backend();
                final RemoteWebDriver d = backend.get();
                d.get("https://www.isis.tu-berlin.de/2.0/");

                output.appendText("Login in the browser\n");
                d.executeScript("document.getElementById(\"head\").insertAdjacentHTML('beforebegin', " +
                        "\"<center><h1 style='background:#f00'>PLEASE LOGIN TO CONTINUE</h1></center>\")");

                WebDriverWait wait = new WebDriverWait(d, 30);
                wait.ignoring(ElementNotFoundException.class);

                wait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return d.getCurrentUrl().contains("/my/");
                    }
                });
                output.appendText("Navigate to the Course\n");
                d.executeScript("document.getElementById(\"head\").insertAdjacentHTML('beforebegin', " +
                        "\"<center><h1 style='background:#f00'>Please navigate to the course....</h1></center>\")");
                wait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return d.getCurrentUrl().contains("/course/");
                    }
                });
                DBController db = DBController.getInstance();
                db.start();

                output.appendText("Reading data....\n");
                d.executeScript("document.getElementById(\"head\").insertAdjacentHTML('beforebegin', " +
                        "\"<center><h1 style='background:#f00'>Reading Data....</h1></center>\")");


                List<WebElement> sections = d.findElements(By.xpath("//li[contains(@class,'section')]"));
                for (WebElement section : sections) {
                    String id = section.getAttribute("id");
                    output.appendText("----" + id + "----\n\n");
                    if (id.equals("section-0")) {
                        List<WebElement> links = section.findElements(By.tagName("a"));
                        for (WebElement link : links) {
                            if (link.getAttribute("href").contains("id=")) {
                                Material material = getMatrialFrom(link, id, null);
                                material.setCategory(CategoryUtil.getCategory(material.getName()));
                                log.info(material.toString());
                                output.appendText("found: " + material + "\n");
                                db.insert(material);
                            }
                        }
                    } else {
                        if (section.findElements(By.tagName("li")).size() == 0) continue;
                        List<WebElement> items = section.findElement(By.tagName("ul")).findElements(By.tagName("li"));
                        String category = "";
                        for (WebElement item : items) {
                            if (item.getAttribute("class").contains("label")) {
                                category = item.getText().replace(":", "").trim();
                                continue;
                            }
                            WebElement link = item.findElement(By.tagName("a"));
                            Material material = getMatrialFrom(link, id, category);
                            String c = CategoryUtil.getCategory(material.getName());
                            material.setCategory(c);
                            if (c.equals("others")
                                    && !category.equals("")) {
                                material.setCategory(category);
                            }
                            log.info(material.toString());
                            output.appendText("found: " + material + "\n");

                            material.setData(new ArrayList<Data>());
                            db.insert(material);
                        }
                    }

                }

                db.commit();
                try {
                    d.quit();
                    d.close();
                } catch (SessionNotFoundException ignored) {
                }

                Platform.runLater(
                        new Runnable() {
                            public void run() {
                                new WizardController().changeStep(2);
                            }
                        }
                );

                return true;
            }

        };

    }

    private Material getMatrialFrom(WebElement link, String section, String category) {
        Material material = new Material();

        String url = link.getAttribute("href");
        String[] nameSplit = link.getText().split("\n");
        String name = nameSplit[0].trim();
        String type = nameSplit.length >= 2 ? nameSplit[1].trim() : "";

        material.setMaterialId(RegexUtils.getIdFromString(url));
        material.setName(name);
        material.setSection(section);
        material.setType(type);
        material.setUrl(url);
        material.setCategory(category);
        return material;
    }

}
