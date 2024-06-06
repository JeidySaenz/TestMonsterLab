
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyTest {
    private WebDriver driver;
            @Before
            public void setUp() {
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                driver.manage().window().maximize();
                driver.get("https://www.google.com/");
            }

            @Test
            public void testCarouselElements() {
                List<WebElement> carousels = driver.findElements(By.cssSelector(".carousel-container .carousel"));

                // Verificar el número de elementos en cada carrusel y si son únicos
                for (WebElement carousel : carousels) {
                    List<WebElement> elements = carousel.findElements(By.tagName("a"));

                    // Verificar que hay exactamente 15 elementos
                    Assert.assertEquals(15, elements.size());

                    // Verificar que los elementos sean únicos
                    Set<String> hrefs = new HashSet<>();
                    for (WebElement element : elements) {
                        hrefs.add(element.getAttribute("href"));
                    }
                    Assert.assertEquals("No hay 15 elementos únicos en el carrusel.", 15, hrefs.size());
                }
            }

    @Test
    public void testApiCall() {
        // Hacer clic en el enlace "pro-firmy"
        driver.findElement(By.linkText("pro-firmy")).click();

        // Esperar un momento para que la página cargue y se realice la llamada a la API
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Realizar la llamada a la API al endpoint /graphql
        String apiUrl = "https://www.mall.cz/graphql";
        String response = getApiResponse(apiUrl);

        // Verificar que la llamada sea un POST
        Assert.assertTrue("La llamada no es un POST.", response.contains("\"requestMethod\":\"POST\""));

        // Verificar que el código de estado sea 200
        Assert.assertTrue("El código de estado no es 200.", response.contains("\"statusCode\":200"));

        // Verificar que la respuesta incluya el dato "hasLazyLoadFilters": false
        Assert.assertTrue("La respuesta no incluye \"hasLazyLoadFilters\": false.", response.contains("\"hasLazyLoadFilters\":false"));
    }

    private String getApiResponse(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            int responseCode = connection.getResponseCode();
            Assert.assertEquals("Error en la conexión a la API.", HttpURLConnection.HTTP_OK, responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testSection() {
        // Hacer clic en el enlace "pro-firmy"
        driver.findElement(By.cssSelector("a[href='/pro-firmy']")).click();

        // Desplazarse hasta la sección con el título "Jakými výhodami MALL Pro usnadňuje podnikání?"
        WebElement section = driver.findElement(By.xpath("//h2[contains(text(),'Jakými výhodami MALL Pro usnadňuje podnikání?')]"));

        // Verificar que la sección sea visible
        Assert.assertTrue("La sección no es visible.", section.isDisplayed());

        // Verificar que el título sea un elemento h2
        Assert.assertEquals("El título no es un elemento h2.", "h2", section.getTagName());

        // Verificar que la sección tenga seis cajas
        int boxCount = driver.findElements(By.cssSelector(".section-boxes .box")).size();
        Assert.assertEquals("La sección no tiene seis cajas.", 6, boxCount);
    }


    @After
    public void tearDown() {
        driver.quit();
    }
}
