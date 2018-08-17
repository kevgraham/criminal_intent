package kevinedwardgraham.com.criminal_intent;

import android.text.format.DateFormat;

import java.util.Date;

public class Utilities {

    private static final String DATE_FORMAT = "EEEE, MMMM dd, yyyy";

    /**
     * Formats Data into a clean String
     */
    public static String formatDate(Date date) {
        return DateFormat.format(DATE_FORMAT, date).toString();
    }
}
