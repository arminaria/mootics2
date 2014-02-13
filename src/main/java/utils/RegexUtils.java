package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Armin on 06.02.14.
 */
public class RegexUtils {

    private static Logger log = LoggerFactory.getLogger(RegexUtils.class);

    public static String getIdFromString(String s) {
        Matcher matcher;
        matcher = Pattern.compile("id=([0-9]+)").matcher(s);
        boolean b = matcher.find();
        if (!b){
            return null;
        }
        return matcher.group(1);
    }

    public static String getActionFrom(String action) {
        Matcher matcher = Pattern.compile(".* (.*) .*").matcher(action);
        matcher.find();
        return matcher.group(1);
    }
}
