import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schedule implements APO {
    static Pattern locationPattern = Pattern.compile("(Bierstadt|Rheinstr\\.|Dürerplatz|Erbenheim)");
    static Pattern contentPattern = Pattern.compile("(Bierstadt|Rheinstr\\.|Dürerplatz|Erbenheim|Container)\\s([ZäöüÄÖÜßA-Za-z]*[-\\d]?[ZäöüÄÖÜßA-Za-z]*)\\s?(\\d\\d:\\d\\d)?");

    public static @org.jetbrains.annotations.NotNull List<APO.Appointment> schedule(Weekday weekday, String raw) throws Exception{
        String date = getDate(raw);

        List<APO.Appointment> appointments = new LinkedList<>();
        Matcher content = contentPattern.matcher(raw);
        Matcher location = locationPattern.matcher(raw);
        while(content.find())
        {
            LocalTime t1, t2;
            String place, starts, ends;
            String name = content.group(2);
            switch (content.group(1))
            {
                case "Container" ->
                {
                    location.find();
                    place = location.group();
                    t1 = catalogue.get(place).get(weekday).opens();
                    t2 = catalogue.get(place).get(weekday).handover();
                }

                case "Bierstadt", "Dürerplatz", "Rheinstr.", "Erbenheim" ->
                {
                    place = content.group(1);
                    t1 = catalogue.get(place).get(weekday).handover();
                    t2 = catalogue.get(place).get(weekday).closes();
                }

                default ->
                {
                    place = location.group();
                    t1 = catalogue.get(place).get(weekday).handover();
                    t2 = catalogue.get(place).get(weekday).closes();
                }
            }
            starts = getDate.apply(date.concat(String.valueOf(t1)));
            ends = getDate.apply(date.concat(String.valueOf(t2)));
            appointments.add(new Appointment(name, "Arbeiten ".concat(place), starts, ends, dictionary.get(place)));
        }
        return appointments;
    }

    private static @org.jetbrains.annotations.NotNull String getDate(String raw) throws Exception{
        Matcher dateM = Pattern.compile("(\\d\\d\\.\\d\\d\\.)").matcher(raw);
        if(dateM.find())
        {
            return dateM.group().concat("2022");
        }
        else
        {
            throw new Exception("NO DATE PROVIDED");
        }
    }
}