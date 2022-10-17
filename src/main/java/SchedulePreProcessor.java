import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

public class SchedulePreProcessor {

    // MATCHES EVERYTHING PRECEDING ANY 'WEEKDAY' FOLLOWED BY 'WEEKDAY', MULTIPLE 'hh:mm ' OR THE END OF INPUT
    public static final String WEEKDAY_R =
                    "\\b(?<=Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\b"
                    + ".*?" +
                    "(?=\\b(?:Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\b|[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}|[0-9]{2}:[0-9]{2} [0-9]{2}:|\\z)";

    public static List<String> readClean(String path) throws IOException{

        Pattern pattern = Pattern.compile(WEEKDAY_R, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(Files.readString(Path.of(path)));

        return matcher.results().map(MatchResult::group).toList();
    }
}