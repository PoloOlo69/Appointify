import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface APO {

    ZoneId ZONE = ZoneId.of("Europe/Berlin");
    DateTimeFormatter DTFR = DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss'Z'");
    Function<String, String> getDate = s -> LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd.MM.yyyyHH:mm")).minusHours(2).format(APO.DTFR);
    Function<Instant, String> getInstDate = i -> LocalDateTime.ofInstant(i, ZONE).format(DTFR);
    BiFunction<Integer, Integer, LocalTime> time = LocalTime::of;
    Map<Weekday, BusinessHour> bierstadt = Map.of
            (
                    Weekday.MONDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.TUESDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.WEDNESDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.THURSDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.FRIDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.SATURDAY, new BusinessHour(time.apply(8, 45), time.apply(18, 0), time.apply(13, 25)),
                    Weekday.SUNDAY, new BusinessHour(time.apply(9, 45), time.apply(16, 0), time.apply(12, 55))
            );
    Map<Weekday, BusinessHour> rheinstrasse = Map.of
            (
                    Weekday.MONDAY, new BusinessHour(time.apply(7, 0), time.apply(19, 0), time.apply(12, 55)),
                    Weekday.TUESDAY, new BusinessHour(time.apply(7, 0), time.apply(19, 0), time.apply(12, 55)),
                    Weekday.WEDNESDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 30), time.apply(12, 55)),
                    Weekday.THURSDAY, new BusinessHour(time.apply(7, 0), time.apply(19, 0), time.apply(12, 55)),
                    Weekday.FRIDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 30), time.apply(12, 55)),
                    Weekday.SATURDAY, new BusinessHour(time.apply(8, 15), time.apply(18, 0), time.apply(12, 25)),
                    Weekday.SUNDAY, new BusinessHour(time.apply(7, 45), time.apply(20, 0), time.apply(11, 55))
            );
    Map<Weekday, BusinessHour> dürerplatz = Map.of
            (
                    Weekday.MONDAY, new BusinessHour(time.apply(8, 15), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.TUESDAY, new BusinessHour(time.apply(8, 15), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.WEDNESDAY, new BusinessHour(time.apply(8, 15), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.THURSDAY, new BusinessHour(time.apply(8, 15), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.FRIDAY, new BusinessHour(time.apply(8, 15), time.apply(18, 0), time.apply(12, 55)),
                    Weekday.SATURDAY, new BusinessHour(time.apply(8, 45), time.apply(13, 0), time.apply(13, 0)),
                    Weekday.SUNDAY, new BusinessHour(time.apply(0, 0), time.apply(0, 0), time.apply(0, 0))
            );
    Map<Weekday, BusinessHour> erbenheim = Map.of
            (
                    Weekday.MONDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 0), time.apply(13, 25)),
                    Weekday.TUESDAY, new BusinessHour(time.apply(7, 0), time.apply(19, 0), time.apply(13, 25)),
                    Weekday.WEDNESDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 30), time.apply(12, 55)),
                    Weekday.THURSDAY, new BusinessHour(time.apply(7, 0), time.apply(19, 0), time.apply(13, 25)),
                    Weekday.FRIDAY, new BusinessHour(time.apply(7, 0), time.apply(18, 30), time.apply(12, 55)),
                    Weekday.SATURDAY, new BusinessHour(time.apply(8, 50), time.apply(16, 0), time.apply(13, 25)),
                    Weekday.SUNDAY, new BusinessHour(time.apply(8, 45), time.apply(14, 0), time.apply(11, 30))
            );
    Map<String, Map<Weekday, BusinessHour>> catalogue = Map.of("Rheinstr.", rheinstrasse, "Bierstadt", bierstadt, "Erbenheim", erbenheim, "Dürerplatz", dürerplatz);
    Map<String, Location> dictionary = Map.of("Bierstadt", Location.BIERSTADT, "Rheinstr.", Location.RHEINSTRASSE, "Dürerplatz", Location.DÜRERPLATZ, "Erbenheim", Location.ERBENHEIM);
    Supplier<String> STAMP = () -> LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Z")).format(DTFR);
    Supplier<String> UID = () -> Instant.now().toString().concat(System.getProperty("user.name"));

    enum Location {
        BIERSTADT("Testzentrum Bierstadt: Rostocker Str. 5, 65191 Wiesbaden Bierstadt"),
        RHEINSTRASSE("Testzentrum Kreuz Apotheke: Oranienstr. 1, 65185 Wiesbaden"),
        ERBENHEIM("Testzentrum Erbenheim: Berliner Str. 277, 65205 Wiesbaden Erbenheim"),
        DÜRERPLATZ("Testzentrum Duerer Apotheke: Albrecht-Dürer-Straße 1, 65195 Wiesbaden");
        final String adress;

        Location(String address){
            this.adress = address;
        }
    }

    enum Weekday {SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY}

    record BusinessHour(LocalTime opens, LocalTime closes, LocalTime handover) {
    }

    record Appointment(String who, String title, String starts, String ends, Location where) {
        static int counter;

        public Appointment(String who, String title, String starts, String ends, Location where){
            this.who = who;
            this.title = title;
            this.starts = starts;
            this.ends = ends;
            this.where = where;
            counter++;
        }

        String toICS(){
            return ("BEGIN:VEVENT\r\n"+
                    "DTSTAMP:%s\r\n"+
                    "SUMMARY:%s\r\n"+
                    "UID:%s\r\n"+
                    "DTSTART:%s\r\n"+
                    "DTEND:%s\r\n"+
                    "LOCATION:%s\r\n"+
                    "END:VEVENT\r\n")
                    .formatted(STAMP.get(), title, who+"_"+LocalTime.now()+"_"+UUID.randomUUID(), starts, ends, where.adress);
        }

        int appointmentCount(){
            return counter;
        }
    }
}
