import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ScheduleTest {

    public static final String DST = "C:\\dev\\out\\arbeitsplan_2.txt";

    @Test
    void schedule() throws Exception{
        test1909();
    }

    @Test
    void test1909() throws Exception{
        String s_String = Files.readString(Path.of("C:\\dev\\out\\arbeitsplan_2.txt"));

        final String WEEKDAY_R = "\\s(Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\s";
        final String CONTAINER_R = "(?<=\\bContainer\\b )(.*?)(?=\\bContainer\\b |\\z|\\d\\d:\\d\\d \\d\\d:\\d\\d |\\d\\d\\.\\d\\d\\.)";

        var res = Arrays.stream(s_String.split(WEEKDAY_R)).toList();
        var sub = Schedule.schedule(APO.Weekday.MONDAY, res.get(1)).toArray();
        var ref = List.of(
                new APO.Appointment("Mareike", "Arbeiten Rheinstr.", at(9, 19, 7, 0), at(9, 19, 12, 55), APO.Location.RHEINSTRASSE),
                new APO.Appointment("Eugen", "Arbeiten Rheinstr.", at(9, 19, 12, 55), at(9, 19, 19, 0), APO.Location.RHEINSTRASSE),
                new APO.Appointment("Hafida", "Arbeiten Dürerplatz", at(9, 19, 8, 15), at(9, 19, 12, 55), APO.Location.DÜRERPLATZ),
                new APO.Appointment("Ruth", "Arbeiten Dürerplatz", at(9, 19, 12, 55), at(9, 19, 18, 0), APO.Location.DÜRERPLATZ),
                new APO.Appointment("Francisco", "Arbeiten Bierstadt", at(9, 19, 7, 0), at(9, 19, 12, 55), APO.Location.BIERSTADT),
                new APO.Appointment("Paul", "Arbeiten Bierstadt", at(9, 19, 12, 55), at(9, 19, 18, 0), APO.Location.BIERSTADT),
                new APO.Appointment("Jamila", "Arbeiten Erbenheim", at(9, 19, 7, 0), at(9, 19, 13, 25), APO.Location.ERBENHEIM),
                new APO.Appointment("Vanja", "Arbeiten Erbenheim", at(9, 19, 13, 25), at(9, 19, 18, 0), APO.Location.ERBENHEIM)
        ).toArray();
        assertArrayEquals(ref, sub, "Monday 19.09. wrong");
    }
    @Test
    void show() throws IOException{
        String s_String = Files.readString(Path.of("C:\\dev\\out\\arbeitsplan_2.txt"));

        final String WEEKDAY_R = "\\s(Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\s";
        final String CONTAINER_R = "(?<=\\bContainer\\b ).*?(?=\\bContainer\\b |\\z|\\d\\d:\\d\\d \\d\\d:\\d\\d |\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d)";
        var x = Pattern.compile("(?<=\\bContainer\\b ).*?(?=\\bContainer\\b |\\z|\\d\\d:\\d\\d \\d\\d:\\d\\d |\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d)", Pattern.DOTALL);

        var res = Arrays.stream(s_String.split(WEEKDAY_R)).collect(Collectors.joining());

        var m = x.matcher(res);

        while(m.find())
        {
            System.out.println(m.group()+"\n\n");
        }
        System.out.println(
                "\n*********"
        );
    }

    @Test
    void test2(){
        try
        {
            String s_String = Files.readString(Path.of("C:\\dev\\out\\arbeitsplan_2.txt"));

            final String WEEKDAY_R = "\\s(Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\s";
            final String CONTAINER_R = "(?<=\\bContainer\\b )(.*?)(?=\\bContainer\\b |\\z|\\d\\d:\\d\\d \\d\\d:\\d\\d |\\d\\d\\.\\d\\d\\.)";
            System.out.println(

                    Arrays.stream(s_String.split(WEEKDAY_R)).toList()
            );
        }catch(IOException e)
        {
            throw new RuntimeException(e);
        }

    }
    static String at(int M, int d, int h, int m){
        return LocalDateTime.of(2022, M, d, h, m, 0).minusHours(2).format(DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss'Z'"));
    }

}