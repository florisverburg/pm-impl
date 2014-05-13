package views;

import org.junit.Test;
import play.test.TestBrowser;
import play.libs.F.Callback;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.testServer;
import static play.test.Helpers.running;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Floris on 12/05/14.
 */
public class MenuClickTests {

    @Test
    public void clickHomeTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
            browser.goTo("http://localhost:3333");
            browser.$("#navbarToggle").click();
            browser.$("#linkHome").click();
            assertThat(browser.url()).isEqualTo("http://localhost:3333/");
            assertThat(browser.title()).isEqualTo("Welcome to APMatch");
            }
        });
    }

    @Test
    public void clickAboutTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
            browser.goTo("http://localhost:3333");
            browser.$("#navbarToggle").click();
            browser.$("#linkAbout").click();
            assertThat(browser.url()).isEqualTo("http://localhost:3333/about");
            assertThat(browser.title()).isEqualTo("About - APMatch");
            }
        });
    }

    @Test
    public void clickContactTest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                browser.$("#navbarToggle").click();
                browser.$("#linkContact").click();
                assertThat(browser.url()).isEqualTo("http://localhost:3333/contact");
                assertThat(browser.title()).isEqualTo("Contact - APMatch");
            }
        });
    }

}
