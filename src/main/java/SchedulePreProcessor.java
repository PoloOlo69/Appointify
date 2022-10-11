import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

public class SchedulePreProcessor {
    final static String CONTAINER_R = "(?<=\\bContainer ).*?(?=\\bContainer\\b|\\d\\d:\\d\\d \\d\\d:|\\z)";

    // RETURNS A PATTERN MATCHING EVERYTHING ENCLOSED BY THE INPUT
    final static BiFunction<String, String, String> regexEnclosedBy = (fst, snd) -> "(?<=\\b%s\\b).*?(?=\\b%s\\b|\\z)";

    final static Pattern UNWANTED = Pattern.compile("^(\\d\\d:\\d\\d\\s){3,}", Pattern.MULTILINE);
    final static Predicate<String> CONTENT = Predicate.not(s -> s.isBlank() || s.matches("([\\d:\\s]+)|(.*Arbeitsplan.*)"));
    final static String WEEKDAY_R = "\\s(Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\s";

    // MATCHES EVERYTHING PRECEDING 'Container ' FOLLOWED BY 'Container', MULTIPLE 'hh:mm ' OR THE END OF INPUT
    static Pattern p = Pattern.compile(CONTAINER_R, Pattern.DOTALL);
    public static String[] readClean(String src) throws IOException{
        var m = p.matcher(Arrays.stream(Files.readString(Path.of(src)).split(WEEKDAY_R)).collect(Collectors.joining()));
        var y = Calendar.getInstance();
        y.set(2022, 10, 03);
        System.out.println("WEEKOFYEAR:"+y.get(Calendar.WEEK_OF_YEAR));
        System.out.println(m.results().map(MatchResult::group).toList());
        return Files.readString(Path.of(src)).split(WEEKDAY_R);
    }

}