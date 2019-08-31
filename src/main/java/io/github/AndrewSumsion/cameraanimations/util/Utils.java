package io.github.AndrewSumsion.cameraanimations.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static int timeToTicks(String timeString) {
        Pattern timePattern = Pattern.compile("^((\\d*)m)?((\\d*)(s)?)?$");
        Matcher matcher = timePattern.matcher(timeString);
        if (!matcher.find()) {
            throw new NumberFormatException();
        }
        String minutesString = matcher.group(2);
        int minutes;
        if (minutesString == null || minutesString.equals("")) {
            minutes = 0;
        } else {
            minutes = Integer.parseInt(minutesString);
        }
        String secondsString = matcher.group(4);
        int seconds;
        if (secondsString == null || secondsString.equals("")) {
            seconds = 0;
        } else {
            seconds = Integer.parseInt(secondsString);
        }
        return seconds * 20 + minutes * 1200;
    }
}
