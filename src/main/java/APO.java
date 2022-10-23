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


    enum Weekday {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    }
    enum Location {
        BIERSTADT("Testzentrum Bierstadt: Rostocker Str. 5, 65191 Wiesbaden Bierstadt"),
        RHEINSTRASSE("Testzentrum Kreuz Apotheke: Oranienstr. 1, 65185 Wiesbaden"),
        ERBENHEIM("Testzentrum Erbenheim: Berliner Str. 277, 65205 Wiesbaden Erbenheim"),
        DÜRERPLATZ("Testzentrum Duerer Apotheke: Albrecht-Dürer-Straße 1, 65195 Wiesbaden");
        final String adressOf;
        Location(String address){
            this.adressOf = address;
        }
    }

    record BusinessHour(LocalTime opens, LocalTime closes, LocalTime handover) {}

    DateTimeFormatter DTFR = DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss'Z'");
    Function<String, String> getFormattedDateTime = s -> LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd.MM.yyyyHH:mm")).minusHours(2).format(DTFR);
    Supplier<String> STAMP = () -> LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Z")).format(DTFR);
    record Appointment(String who, String title, String description, String starts, String ends, String address) {
        String toICS() {
            return ("BEGIN:VEVENT\r\n" +
                    "DTSTAMP:%s\r\n" +
                    "UID:%s\r\n" +
                    "SUMMARY:%s\r\n" +
                    "DTSTART:%s\r\n" +
                    "DTEND:%s\r\n" +
                    "DESCRIPTION:%s\r\n" +
                    "LOCATION:%s\r\n" +
                    "END:VEVENT\r\n")
                    .formatted(STAMP.get(), who + "_" + UUID.randomUUID(), title, starts, ends, description, address);
        }

    }

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
    Map<String, Map<Weekday, BusinessHour>> catalogue =
            Map.of("Rheinstr.", rheinstrasse, "Bierstadt", bierstadt, "Erbenheim", erbenheim, "Dürerplatz", dürerplatz);
    Map<String, Location> dictionary =
            Map.of("Bierstadt", Location.BIERSTADT, "Rheinstr.", Location.RHEINSTRASSE, "Dürerplatz", Location.DÜRERPLATZ, "Erbenheim", Location.ERBENHEIM);
}
