package units;

import java.time.LocalTime;
import java.util.Comparator;

public class Picker {
    public static LocalTime START_TIME; // Start of the day. Created only for `pickingEndTime`.

    public final String pickerName;
    private LocalTime pickingEndTime;

    public Picker(String pickerName) {
        this.pickerName = pickerName;
        this.pickingEndTime = START_TIME;
    }

    public void setPickingEndTime(LocalTime pickingEndTime) { // If the picker got the task, set the end time of the task.
        this.pickingEndTime = pickingEndTime;
    }

    public LocalTime getPickingEndTime() {
        return pickingEndTime;
    }

    @Override
    public String toString() {
        return pickerName + " = " + pickingEndTime;
    }

    // Sort the pickers by the end time of the work.
    // If a free one appears, he appears at the top of the list.
    // (The end time is earlier than the time on the `Global Timer`)
    public static class ComparePicker implements Comparator<Picker> {
        @Override
        public int compare(Picker o1, Picker o2) {
            return o1.pickingEndTime.compareTo(o2.pickingEndTime);
        }
    }
}
