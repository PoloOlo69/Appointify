import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SchedulePreProcessor {
    final static String DAYS = "\\s(\\w*tag|Mittwoch)\\s";// Nur an tagen die mit g aufhören und Mittwoch.
    final static Predicate<String> UNWANTED = Predicate.not(s -> s.isBlank() || s.matches("([\\d\\d:\\d\\d\\s?]{7,})|(.*Arbeitsplan.*)"));

    public static String[] readClean(String src) throws IOException{
        StringBuffer sb = new StringBuffer();
        return Files.newBufferedReader(Path.of(src)).lines()
                    .filter(UNWANTED)
                    .map(s -> s.concat(System.lineSeparator()))
                    .collect(Collectors.joining())
                    .split(DAYS);
    }

}