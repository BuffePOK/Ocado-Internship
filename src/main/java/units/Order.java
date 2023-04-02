package units;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;

public class Order {
    public final String orderId;
    public final BigDecimal orderValue;
    public final Duration pickingTime;
    public final LocalTime completeBy;

    public final LocalTime maxTimeToGetOrder; // The deadline when an order can be processed

    public Order(String orderId, BigDecimal orderValue, Duration pickingTime, LocalTime completeBy) {
        this.orderId = orderId;
        this.orderValue = orderValue;
        this.pickingTime = pickingTime;
        this.completeBy = completeBy;

        this.maxTimeToGetOrder = completeBy.minusMinutes(pickingTime.toMinutes());
    }


    public boolean canGetOrder(LocalTime time) { // Will we be able to fill out the order in time.
        return time.isBefore(this.maxTimeToGetOrder) || time.equals(this.maxTimeToGetOrder);
    }

    @Override
    public String toString() {
        return orderId + ":" + pickingTime + ". " + completeBy;
    }

    // Sorting from the most expensive to the cheapest order.
    public static class CompareOrderValue implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            return o1.orderValue.compareTo(o2.orderValue);
        }
    }

    // Sorting orders according to the principle:
    // The earlier the deadline for `sending minus filling out the order` ends, the higher the order is.
    public static class CompareOrder implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            return o1.maxTimeToGetOrder.compareTo(o2.maxTimeToGetOrder);
        }
    }
}
