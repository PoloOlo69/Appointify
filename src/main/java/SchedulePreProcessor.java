import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class SchedulePreProcessor {
    // Matches everything preceding a weekday followed by a weekday, unnecessary information, or the end of the input
    private static final String WEEKDAY_REGEX =
            "(?<=^(?:Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\b)"
              + ".*?" +
            "(?=^(?:Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\b|[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}|[0-9]{2}:[0-9]{2} [0-9]{2}:|\\z)";

    public static List<String> readClean(String path) throws IOException{
        var WEEKDAY_PATTERN = Pattern.compile(WEEKDAY_REGEX, Pattern.DOTALL|Pattern.MULTILINE);
        var matcher = WEEKDAY_PATTERN.matcher(Files.readString(Path.of(path)));
        return matcher.results().map(MatchResult::group).toList();
    }
}