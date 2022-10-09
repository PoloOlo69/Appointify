import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SchedulePreProcessor {
    final static String DAYS = "\\s(Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\s";
    final static Pattern UNWANTED = Pattern.compile("^(\\d\\d:\\d\\d\\s){3,}", Pattern.MULTILINE);
    final static Predicate<String> CONTENT = Predicate.not(s -> s.isBlank() || s.matches("([\\d:\\s]+)|(.*Arbeitsplan.*)"));
    // (?<=\bContainer\b ).*?(?=\bContainer\b|\d\d:\d\d \d\d:\d\d ) MATCHES ONLY RELEVANT DATA BUT THE LAST BLOCK OF RELEVANT DATA
    Pattern p = Pattern.compile("(?<=\\bContainer\\b )(.*?)(?: ?\\bContainer\\b ?|\\d\\d:\\d\\d \\d\\d:\\d\\d|$|\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d)", java.util.regex.Pattern.DOTALL);
    public static String[] readClean(String src) throws IOException{
        //Files.readString(Path.of())
        return Files.newBufferedReader(Path.of(src))
                    .lines()
                    .filter(CONTENT)
                    .map(s -> s.concat(System.lineSeparator()))
                    .collect(Collectors.joining())
                    .split(DAYS);
    }

}