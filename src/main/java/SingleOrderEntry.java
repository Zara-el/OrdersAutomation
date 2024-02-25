import org.junit.Test;

import java.io.IOException;

public class SingleOrderEntry {
    static OrderEntry orderEntry = new OrderEntry();
    @Test
    public static void singleOrderEntry(String[] args) {
        try {
            enterAmazonSingleOrder(args[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public static void enterAmazonSingleOrder(String recordID) throws IOException, InterruptedException {
        orderEntry.enterSingleOrder("Amazon", "Airtable",  recordID);
    }
}
