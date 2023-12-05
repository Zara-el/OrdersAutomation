import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import dev.fuxing.airtable.AirtableApi;
import dev.fuxing.airtable.AirtableTable;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class getOrders {

    public static EdgeOptions options = new EdgeOptions();
    public static WebDriver driver = new EdgeDriver(options);
    AirtableApi api = new AirtableApi("keyLlxDa3uLQ8B7uD");
    AirtableTable table = api.base("appYoSKUjYL3bgvb3").table("tblQyACTbwUwc5JKh");
    List<List<String>> Orders = new ArrayList<>(table.list().size());
    ArrayList<String> orderDetails = new ArrayList<>();
    List<List<String>> failedOrders = new ArrayList<>();

    List<String> failedOrdersIDs = new ArrayList<>();
    List<List<String>> EnteredOrders = new ArrayList<>();
    List<String> enteredOrdersIDs = new ArrayList<>();
    List<List<String>> UIOrders = new ArrayList<>();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

    @Test
    void readData() throws IOException {
        try {
            String path = "C:\\Users\\Zara\\Downloads\\MAIN TABLE-Grid view.csv";
//            String path = "C:\\Users\\Zara\\Downloads\\Walmart-Grid view.csv";
            Reader reader = new FileReader(path);


            try (CSVReader csvreader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .build();) {

                Orders = Files.lines(Path.of(path)).skip(1) // (optional) skip a header line
                        .map(line -> Arrays.stream(line.split("\\s*,\\s*")) // split by comma and trim whitespaces
                                .collect(Collectors.toList()) // get list of columns
                        ) // Stream<List<String>>
                        .collect(Collectors.toList());
            }

            System.out.println("Total Orders to be entered: " + Orders.size());
        } catch (Exception e) {
            System.out.println("Unable to read data from the csv file " + e.getMessage());
        }
    }

    void setup() {
        try {
            //setup
            //Navigate to tireguru
            System.setProperty("webdriver.edge.driver", "C:\\Users\\Zara\\Documents\\Projects\\OrdersEntry\\src\\main\\resources\\msedgedriver.exe");
            options.addArguments("--remote-allow-origins=*", "ignore-certificate-errors");
            driver.manage().window().maximize();
        } catch (Exception e) {
            System.out.println("the setup has been unsuccessful " + e.getMessage());
        }
    }

    void login() {
        try {
            driver.get("https://www.tireguru.net/login.php");

            //Login to tireguru
            driver.findElement(By.xpath("//*[@id=\"bc_server\"]")).clear();
            driver.findElement(By.xpath("//*[@id=\"bc_server\"]")).sendKeys("TG2");
            driver.findElement(By.xpath("//*[@id=\"bc_logID\"]")).sendKeys("hamza");
            driver.findElement(By.xpath("//*[@id=\"bc_pass\"]")).sendKeys("hamza10");
            driver.findElement(By.xpath("(//button[@type='submit'])[1]")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='invoicing-menu']")));
        } catch (Exception e) {
            System.out.println("Unable to navigate and login to Tireguru " + e.getMessage());
        }
    }

    void navigateToPointOfSale() {
        try {
            //Navigate to Point of Sales in Tireguru
            Thread.sleep(1000);
            driver.switchTo().frame(driver.findElement(By.name("mainDiv")));
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@accesskey='x'])[1]")));
                driver.findElement(By.xpath("(//button[@accesskey='x'])[1]")).click();
            } catch (Exception e) {
                System.out.println("Popup did not show up");
            }
            Thread.sleep(1000);
            driver.switchTo().defaultContent();
            driver.findElement(By.xpath("//span[@class='invoicing-menu']")).click();
        } catch (Exception e) {
            System.out.println("Unable to navigate to Point of Sales in Tireguru " + e.getMessage());
        }
    }

    void orderLookup() {
        try {
            //Order lookup
            driver.findElement(By.xpath("//button[@accesskey='L']")).click();
            driver.switchTo().frame(driver.findElement(By.name("mainDiv")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='look_item_num']")));
        } catch (Exception e) {
            System.out.println("Unable to open order lookup screen " + e.getMessage());
        }
    }

    void customerSearch(String customerName) throws InterruptedException {
        try {
            //customer search screen
            //enter business name and search customers
            //select business
            driver.findElement(By.xpath("//input[@accesskey='B']")).sendKeys(customerName);
            driver.findElement(By.xpath("//button[@type='submit']")).click();
            driver.findElement(By.xpath("//button[@name='selbtn']")).click();

            //customer work order/quote list screen
            //click on new work order
            driver.findElement(By.xpath("(//div[@class='tg-my-4']/button[1])[1]")).click();
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("Unable to search for customer " + e.getMessage());
        }

    }

    void updateStatus(String status) throws InterruptedException {
        try {
            //work order screen
            //change status to Fedex/Ups and click on change status
            driver.switchTo().defaultContent();
            driver.switchTo().frame(driver.findElement(By.name("invoice_info")));
            Thread.sleep(3000);
            driver.findElement(By.xpath("(//i[@style='color:green']/..)[1]")).click();
            driver.switchTo().defaultContent();
            driver.switchTo().frame(driver.findElement(By.name("mainDiv")));
            Thread.sleep(3000);
            Select statusSelect = new Select(driver.findElement(By.xpath("//select[@name='statusID']")));
            statusSelect.selectByVisibleText(status);
            Thread.sleep(3000);
            driver.findElement(By.xpath("//button[@accesskey='c']")).click();
        } catch (Exception e) {
            System.out.println("Unable to change status " + e.getMessage());
        }

    }
//    void writeToFileFailedOrders() throws IOException {
//        try {
//            // Create new file
//            String path="C:\\Users\\Zara\\Documents\\failedToEnterOrders.txt";
//            File file = new File(path);
//
//            // If file doesn't exists, then create it
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            FileWriter fw = new FileWriter(file.getAbsoluteFile());
//            BufferedWriter bw = new BufferedWriter(fw);
//
//            // Write in file orderIDS and failure reason
//            ArrayList<String> failedOrdersIDs = new ArrayList<>();
//            ArrayList<String> failureReasons = new ArrayList<>();
//
//            for (String key : failedOrders.keySet()) {
//                failedOrdersIDs.add(key);
//            }
//
//            for (String value : failedOrders.values()) {
//                failureReasons.add(value);
//            }
//
//            for (int i = 0; i < failedOrdersIDs.size(); i++) {
//                bw.write("Failed to enter the following order: " + failedOrdersIDs.get(i) + "\n");
//            }
//
//            bw.write("\n\n\n");
//            bw.write("____________________________________________________________________________");
//
//            for (int i = 0; i < failedOrdersIDs.size(); i++) {
//                bw.write("Order ID: " + failedOrdersIDs.get(i) + "\n");
//                bw.write("FailureReason: " + failureReasons.get(i) + "\n\n\n");
//            }
//
//            // Close connection
//            bw.close();
//        } catch (Exception e) {
//            System.out.println("Unable to write to file the orders that failed " + e.getMessage());
//        }
//    }

    @Test
    void enterOrder() throws IOException, InterruptedException {

        setup();
        readData();

        for (int i = 0; i < Orders.size(); i++) {

            try {
                login();
                navigateToPointOfSale();
                orderLookup();

                //quick look screen
                //enter SKU and search
                //enter quantity and add to order
                driver.findElement(By.xpath("//input[@name='look_item_num']")).sendKeys(Orders.get(i).get(5));
                driver.findElement(By.xpath("//button[@accesskey='s']")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@type='text'])[2]")));
                String mystring = Orders.get(i).get(4);
                String quantityList[] = mystring.split("\\.");
                String quantity;
                if(Orders.get(i).get(6).contains("Set of 2"))
                    quantity = String.valueOf(Integer.parseInt(quantityList[0]) * 2);
                else if(Orders.get(i).get(6).contains("Set of 4"))
                    quantity = String.valueOf(Integer.parseInt(quantityList[0]) * 4);
                else
                    quantity = quantityList[0];
                driver.findElement(By.xpath("(//input[@type='text'])[2]")).sendKeys(quantity);
                driver.findElement(By.xpath("(//button[@accesskey='t'])[5]")).click();

                customerSearch("amazon");
//                customerSearch("walmart");
                updateStatus("Fedex/Ups");

                //add order_id as po number in po,ref,track
                driver.switchTo().defaultContent();
                driver.switchTo().frame(driver.findElement(By.name("invoice_info")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//i[@style='color:orange']/..)[3]")));
                driver.findElement(By.xpath("(//i[@style='color:orange']/..)[3]")).click();
                driver.switchTo().defaultContent();
                driver.switchTo().frame(driver.findElement(By.name("mainDiv")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='po']")));
                driver.findElement(By.xpath("//input[@name='po']")).sendKeys(Orders.get(i).get(0));
                driver.findElement(By.xpath("//button[@type='submit']")).click();

                //select item and override price
                driver.switchTo().defaultContent();
                driver.switchTo().frame(driver.findElement(By.name("invoice_items")));
                Thread.sleep(3000);
                driver.findElement(By.xpath("//table[@id = 'items_table']/tbody/tr[2]/td[1]")).click();
                driver.switchTo().defaultContent();
                driver.findElement(By.xpath("//button[@name = 'override']")).click();
                driver.switchTo().frame(driver.findElement(By.name("mainDiv")));
                Thread.sleep(3000);
                String TGCost;
                if(Orders.get(i).get(6).contains("Set of 2"))
                    TGCost = String.valueOf(Double.parseDouble(Orders.get(i).get(1)) / 2.00);
                else if(Orders.get(i).get(6).contains("Set of 4"))
                    TGCost = String.valueOf(Double.parseDouble(Orders.get(i).get(1)) / 4.00);
                else
                    TGCost = Orders.get(i).get(1);
                Thread.sleep(1000);
                driver.findElement(By.xpath("//input[@id = 'parts']")).sendKeys("");
                driver.findElement(By.xpath("//input[@id = 'parts']")).sendKeys(TGCost);
                driver.findElement(By.xpath("//textarea[@name = 'comment']")).sendKeys("GOS");
                driver.findElement(By.xpath("//button[@accesskey = 'u']")).click();

                //options to add shipping fee
                driver.switchTo().defaultContent();
                Thread.sleep(3000);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@accesskey = 'O']")));
                driver.findElement(By.xpath("//button[@accesskey = 'O']")).click();
                driver.switchTo().frame(driver.findElement(By.name("mainDiv")));
                Thread.sleep(3000);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@accesskey = 'Y']/../button[2]")));
                driver.findElement(By.xpath("//button[@accesskey = 'Y']/../button[2]")).click();
                driver.findElement(By.xpath("//*[@id=\"row_353\"]/td[2]")).click();
                NumberFormat format = NumberFormat.getCurrencyInstance();
                Number shippingFee = format.parse(Orders.get(i).get(2));
                driver.findElement(By.xpath("//input[@name = 'fee_amount']")).sendKeys(shippingFee.toString());
                driver.findElement(By.xpath("//textarea[@name = 'fee_reason']")).sendKeys("Shipping");
                Thread.sleep(3000);
                driver.findElement(By.xpath("//button[@accesskey = 'e']")).click();
                Thread.sleep(3000);

                //store work order no

                //store total value
                driver.switchTo().defaultContent();
                String totalValue = driver.findElement(By.xpath("//td[@id='grand_div']")).getText();
                List<String> list = new ArrayList<String>();
                list.add(Orders.get(i).get(0));
                list.add(totalValue);
                EnteredOrders.add(new ArrayList<>(list));
                list.clear();
                enteredOrdersIDs.add(Orders.get(i).get(0));

                //save & quit
                driver.switchTo().defaultContent();
                Thread.sleep(3000);
                driver.findElement(By.xpath("//button[@accesskey = 'Q']")).click();


            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Failed to enter the following order: " + Orders.get(i).get(0));
                List<String> list2 = new ArrayList<String>();
                list2.add(Orders.get(i).get(0));
                list2.add(e.getMessage());
                failedOrders.add(new ArrayList<>(list2));
                list2.clear();
                failedOrdersIDs.add(Orders.get(i).get(0));
            }
        }

        countValidate();
        getUIAmazonOrders();
        validateEnteredOrders();
        validateFailedOrders();

    }

    void countValidate() {
        try {
            System.out.println("Total orders to be ordered counted from the csv file given: " + Orders.size());
            System.out.println("Total orders entered: " + EnteredOrders.size());
            System.out.println("Total orders that failed: " + failedOrders.size());
            Assert.assertEquals(Orders.size(), (EnteredOrders.size() +  failedOrders.size()), "Entered Orders count + failed Orders count does not match with the count of total orders from the CSV file.");
        } catch (Exception e) {
            System.out.println("Count Validation failed");
        }
    }

    @Test
    void getOrders() throws IOException, InterruptedException {

        AirtableApi api = new AirtableApi("keyLlxDa3uLQ8B7uD");
        AirtableTable table = api.base("appYoSKUjYL3bgvb3").table("tblQyACTbwUwc5JKh");
        int maxResults = 300;
        AirtableTable.QuerySpec query = AirtableTable.QuerySpec.create();
        AirtableTable.PaginationList list = table.list(query.offset("0"));


        System.out.println(list.size());
        for(int i = 0; i < list.size(); i++) {
            orderDetails.add(table.list().get(i).getFieldString("Order_ID"));
            orderDetails.add(table.list().get(i).getFieldString("TG COST"));
            orderDetails.add(table.list().get(i).getFieldString("SHIPPING COST"));
            orderDetails.add(table.list().get(i).getFieldString("Status"));
            orderDetails.add(table.list().get(i).getFieldString("QUANTITY"));
            orderDetails.add(table.list().get(i).getFieldString("SKU"));
            orderDetails.add(table.list().get(i).getFieldString("Invoice TG"));

            String status = table.list().get(i).getFieldString("Status");
            if(status != null && status.equalsIgnoreCase("Wait For Price")) {
                System.out.println(orderDetails);
                Orders.add(new ArrayList<>(orderDetails));
            }
            orderDetails.clear();
        }
//        table.iterator().forEachRemaining(record -> {
//            String orderID = record.getFieldString("Order_ID");
//            String TGCost = record.getFieldString("TG COST");
//            String ShippingCost = record.getFieldString("SHIPPING COST");
//            String Status = record.getFieldString("Status");
//            String Quantity = record.getFieldString("QUANTITY");
//            String SKU = record.getFieldString("SKU");
//            String InvoiceTG = record.getFieldString("Invoice TG");
//            orderDetails.addAll(Arrays.asList(orderID, TGCost, ShippingCost, Status, Quantity, SKU, InvoiceTG));
//
//            System.out.println(orderDetails);
//            int outerIndex =0;
//            int innerIndex =0;
////            for (int i =0; i<orderDetails.size(); i++) {
////                Orders.get(outerIndex).add(innerIndex, orderDetails.get(i));
////                innerIndex++;
////            }
//
//            Orders.add(orderDetails);
//
////            Orders.get(i).add(orderDetails);
////            System.out.println(Orders);
//            orderDetails.clear();
//        });

    }


    @Test
    void getUIAmazonOrders() {
        try {
            setup();
            login();
            navigateToPointOfSale();

            //sort orders
            WebElement sortByDate = driver.findElement(By.xpath("(//a[@href=\"javascript:doSort('date', 'desc')\"])[2]"));
            sortByDate.click();

            //get full table: scroll to the last row
            WebElement element = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[last()]"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView();", element);

            Thread.sleep(2000);

            java.util.List<WebElement> firstSetOfRows = driver.findElements(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"line_1\"]"));
            java.util.List<WebElement> secondSetOfRows = driver.findElements(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"line_2\"]"));
            System.out.println(firstSetOfRows.size());
            System.out.println(secondSetOfRows.size());

            ArrayList<String> amazonOrders = new ArrayList<>();

            //iterating over the first set of rows
            for(int i = 0; i < secondSetOfRows.size(); i++) {
                String classAttribute = firstSetOfRows.get(i).getAttribute("class");
                if(firstSetOfRows.get(i).getAttribute("class").contentEquals("tg-table-spacer-row"))
                    i++;
                else {
                    String salesPersonName = null;
                    try {
                        WebElement salesPerson = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"" + classAttribute + "\"][" + i +"]/td[5]"));
                        salesPersonName = salesPerson.getAttribute("innerText");
                    } catch (Exception e) {
                        System.out.println("WebElement not found, passing to the next one");
                    }
                    if(salesPersonName != null && salesPersonName.contentEquals("Hamza Aitouny")) {
                        WebElement amazonOrderElement = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"" + classAttribute + "\"][" + i + "]"));
                        WebElement PoNumber = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"" + classAttribute + "\"][" + i + "]/td[11]"));
                        WebElement total = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"" + classAttribute + "\"][" + i + "]/td[10]"));
                        String totalValue = total.getAttribute("innerText").replaceAll("[$,]", "");
                        String PoNoInfo = PoNumber.getAttribute("innerText");
                        String PoNo = PoNoInfo.substring(0, Math.min(PoNoInfo.length(), 19));
                        amazonOrders.add(PoNo);
                        amazonOrders.add(totalValue);
                        UIOrders.add(new ArrayList<>(amazonOrders));
                        amazonOrders.clear();
                    }
                }
            }

            //iterating over the second set of rows
            for(int i = 0; i < secondSetOfRows.size(); i++) {
                String classAttribute = secondSetOfRows.get(i).getAttribute("class");
                if(firstSetOfRows.get(i).getAttribute("class").contentEquals("tg-table-spacer-row"))
                    i++;
                else {
                    String salesPersonName = null;
                    try {
                        WebElement salesPerson = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"" + classAttribute + "\"][" + i +"]/td[5]"));
                        salesPersonName = salesPerson.getAttribute("innerText");
                    } catch (Exception e) {
                    }
                    if(salesPersonName != null && salesPersonName.contentEquals("Hamza Aitouny")) {
                        WebElement amazonOrderElement = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"" + classAttribute + "\"][" + i + "]"));
                        WebElement PoNumber = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"" + classAttribute + "\"][" + i + "]/td[11]"));
                        WebElement total = driver.findElement(By.xpath("//table[@id=\"open_orders\"]/tbody/tr[@class=\"" + classAttribute + "\"][" + i + "]/td[10]"));
                        String totalValue = total.getAttribute("innerText").replaceAll("[$,]", "");
                        String PoNoInfo = PoNumber.getAttribute("innerText");
                        String PoNo = PoNoInfo.substring(0, Math.min(PoNoInfo.length(), 19));

                        amazonOrders.add(PoNo);
                        amazonOrders.add(totalValue);
                        UIOrders.add(new ArrayList<>(amazonOrders));
                        amazonOrders.clear();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Orders Validation failed" + e.getMessage());
        }
    }

    @Test
    void validateEnteredOrders() {

        getUIAmazonOrders();
        try {
            // Create new file
            String path="C:\\Users\\Zara\\Documents\\toBeReviewedOrders.txt";
            File file = new File(path);

            // If file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("Orders that need to be reviewed (wrong total on TireGuru): \n");
            for(int i = 0; i < EnteredOrders.size(); i++) {
                for (int k = 0; k < UIOrders.size(); k++) {
                    if (UIOrders.get(k).contains(EnteredOrders.get(i).get(0))) {
                        if (UIOrders.get(k).get(1).equalsIgnoreCase(EnteredOrders.get(i).get(1))) {
                            System.out.println(EnteredOrders.get(i).get(0) + " has been validated on Tireguru with the correct total");
                        } else if (!(UIOrders.get(k).get(1).equalsIgnoreCase(EnteredOrders.get(i).get(1)))) {
                            System.out.println(EnteredOrders.get(i).get(0) + " has been validated on Tireguru but with the wrong total. please review.");
                            bw.write("Please review the following order, it has the wrong total on Tireguru: " + EnteredOrders.get(i).get(0) + "\n");
                        }
                    }
                }
            }

            bw.write("-------------------------------------------------------------------\n\n\n");
            bw.write("Orders entered but could not be found on Tireguru, please enter them manually:\n");
            for (int i = 0; i < enteredOrdersIDs.size(); i++) {
                    int finalI = i;
                    boolean exists = UIOrders.stream().anyMatch(e -> e.contains(enteredOrdersIDs.get(finalI)));
                    if(!exists)
                        bw.write("Order entered but could not be found on Tireguru: " + enteredOrdersIDs.get(i) + "\n");
            }


//                    else {
//                        System.out.println(EnteredOrders.get(i).get(0) + " does not exist on Tireguru point of sale. please review");
//                        bw.write("Please review the following order, could not find it Tireguru: " + EnteredOrders.get(i).get(0) + "\n");
//                    }

            // Close connection
            bw.close();

//            for(int i = 0; i < EnteredOrders.size(); i++) {
//                for (int k = 0; k < UIOrders.size(); k++) {
//                    if (UIOrders.get(k).contains(EnteredOrders.get(i).get(0))) {
//
//                    }
//                }
//            }
        } catch (Exception e) {
            System.out.println("Orders Entered Validation failed" + e.getMessage());
        }
    }

    @Test
    void validateFailedOrders() {
        try {

            getUIAmazonOrders();
            // Create new file
            String path="C:\\Users\\Zara\\Documents\\failedToEnterOrders.txt";
            File file = new File(path);

            // If file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("Orders that failed but exist on Tireguru (need to be reviewed): \n");

            for(int i = 0; i < failedOrders.size(); i++) {
                for (int k = 0; k < UIOrders.size(); k++) {
                    if (UIOrders.get(k).contains(failedOrders.get(i).get(0))) {
                        System.out.println(failedOrders.get(i).get(0) + " should not exist on the Tireguru as it failed but it does. Please review");
                    }
                }
            }

            // Write in file orderIDS and failure reason

            bw.write("-------------------------------------------------------------------\n\n\n");
            bw.write("Failed orders list, please enter them manually: \n");
            for (int i = 0; i < failedOrdersIDs.size(); i++) {
                int finalI = i;
                boolean exists = UIOrders.stream().anyMatch(e -> e.contains(failedOrdersIDs.get(finalI)));
                if(!exists)
                    bw.write("Failed to enter the following order: " + failedOrdersIDs.get(i) + "\n");
            }

            bw.close();
        } catch (Exception e) {
            System.out.println("Orders Entered Validation failed" + e.getMessage());
        }
    }
}


