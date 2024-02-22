import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;

public class Main {

    static OrderEntry orderEntry = new OrderEntry();
    @Test
    public static void main(String[] args) {
        try {
            enterWalmartOrdersFromAirtable();
            enterAmazonOrdersFromAirtable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public static void enterAmazonOrdersFromAirtable() throws IOException, InterruptedException {
        orderEntry.enterOrder("Amazon", "Airtable");
    }

    @Test
    public static void enterWalmartOrdersFromAirtable() throws IOException, InterruptedException {
        orderEntry.enterOrder("Walmart", "Airtable");
    }

    @Test
    public void enterAmazonOrdersFromCSV() throws IOException, InterruptedException {
        orderEntry.enterOrder("Amazon", "CSV");
    }

    @Test
    public void enterWalmartOrdersFromCSV() throws IOException, InterruptedException {
        orderEntry.enterOrder("Walmart", "CSV");
    }
}