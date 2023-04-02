import org.json.JSONArray;
import org.json.JSONObject;
import units.Order;
import units.Picker;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Start {
    private static String STORE_PATH;
    private static String ORDERS_PATH;
    private static LocalTime pickingStartTime;
    private static LocalTime pickingEndTime;

    private static final List<Picker> pickers = new LinkedList<>();
    private static final List<Order> orders = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        STORE_PATH = args[0];
        ORDERS_PATH = args[1];

        // More information in the description of the functions.
        readStoreInfo();
        readOrdersInfo();

        // Checking whether our timer has reached the end of the working day.
        // (One minute of time is added after each iteration)
        // For optimization: If the orders ended earlier, can finish.
        while (!pickingStartTime.equals(pickingEndTime) && orders.size() > 0) {

            // Checks if the first picker in the sorted list is free.
            // Free - this means that the end date of the last order according to the timer has already passed.
            while (pickers.get(0).getPickingEndTime().isBefore(pickingStartTime) ||
                   pickers.get(0).getPickingEndTime().equals(pickingStartTime)) {
                if(orders.size() == 0) break; // Trigger if the orders ended before the deliverers.

                // We check whether it makes sense to take the first sorted order.
                if(orders.get(0).canGetOrder(pickingStartTime)) {
                    // The order came up.
                    // So we change the information about the end of the working time to the picker.
                    pickers.get(0).setPickingEndTime(pickingStartTime.plusMinutes(orders.get(0).pickingTime.toMinutes()));

                    System.out.println(pickers.get(0).pickerName + " " + orders.get(0).orderId + " " + pickingStartTime);

                    orders.remove(0); // And remove order (Since it is already running)
                }
                else orders.remove(0); // We only delete the order if it does not fit in time.

                // Sort the pickers by the end time of the work.
                // If a free one appears, he appears at the top of the list.
                // (The end time is earlier than the time on the `Global Timer`)
                pickers.sort(new Picker.ComparePicker());
            }
            pickingStartTime = pickingStartTime.plusMinutes(1); // Add one minute to the world time.
        }
    }
    // There was an implicit assignment of the order to the deliverer.
    // In fact, there is no assignment, and we simply forget the order, and the deliverer "rests" for a while.


    private static void readStoreInfo() throws IOException {
        JSONObject object = new JSONObject(JSONReader.readJSON(STORE_PATH)); // reading JSON "store.json"

        // getting info about StartTime and EndTime
        pickingStartTime = LocalTime.parse(object.getString("pickingStartTime"));
        pickingEndTime = LocalTime.parse(object.getString("pickingEndTime"));
        Picker.START_TIME = pickingStartTime.minusMinutes(1);

        // Counting the number of pickers and creating `Picker` objects.
        JSONArray array = object.getJSONArray("pickers");
        for(int i = 0; i < array.length(); i++) {
            pickers.add(new Picker(array.getString(i)));
        }
        // There is no need to sort, since everyone has the same start time.
    }

    private static void readOrdersInfo() throws IOException {
        JSONArray array = new JSONArray(JSONReader.readJSON(ORDERS_PATH)); // reading JSON "orders.json"

        // Process information about each order. Creating `Order` objects.
        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            // Collecting info
            String orderId = object.getString("orderId");
            BigDecimal orderValue = object.getBigDecimal("orderValue");
            Duration pickingTime = Duration.parse(object.getString("pickingTime"));
            LocalTime completeBy = LocalTime.parse(object.getString("completeBy"));

            Order order = new Order(orderId, orderValue, pickingTime, completeBy);
            // Add info
            orders.add(order);
        }

        // Sorting from the most expensive to the cheapest order.
        // (In the logic-bomb test, it helped to win 1 more order) =)
        orders.sort(new Order.CompareOrderValue());
        Collections.reverse(orders);

        // Sorting orders according to the principle:
        // The earlier the deadline for `sending minus filling out the order` ends, the higher the order is.
        orders.sort(new Order.CompareOrder());
    }
}
