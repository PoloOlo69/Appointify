import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SchedulePreProcessor {
    final static String DAYS = "(\\s(\\w*tag)|\\s(Mittwoch))\\s";// Nur an tagen die mit g aufhören und Mittwoch.
    final static Predicate<String> UNWANTED = Predicate.not(s -> s.matches("([\\d\\d:\\d\\d\\s]{7,})|(.*Arbeitsplan.*)") || s.isBlank());

    public static String[] readClean(String src) throws IOException{
        StringBuffer sb = new StringBuffer();
        Files.newBufferedReader(Path.of(src)).lines()
                .filter(UNWANTED)
                .forEach(s -> {
                    sb.append(s).append(System.lineSeparator());
                });
        return sb.toString().split(DAYS);
    }
}
