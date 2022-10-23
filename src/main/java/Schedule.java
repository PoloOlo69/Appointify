import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schedule implements APO {
    private static String getDate(String raw) {
        Matcher date = Pattern.compile(DATE_REGEX).matcher(raw);
        if (date.find()) {
            return date.group().concat("2022");
        }
        return "NOT PROVIDED";
    }

    private static final String NEW_LINE = "\\r\\n|\\r|\\n";
    private static final String DATE_REGEX = "(?:[0-9]{2}\\.){2}";
    private static final String WEEKDAY_REGEX = "\\w*tag|Mittwoch";
    private static final String DURATION_REGEX = "(([0-9]),?([0-9])?h)";
    private static final String CONTEXT_REGEX = "(Bierstadt|Rheinstr\\.|Dürerplatz|Erbenheim)";
    private static final String CONTAINER_REGEX =
            "(?:([0-9],?[0-9]?h )?Container\\b).*?"+"(?=([0-9],?[0-9]?h )?Container\\b|\\z)";
    private static final Pattern CONTEXT_PATTERN = Pattern.compile(CONTEXT_REGEX, Pattern.MULTILINE);
    private static final Pattern CONTAINER_PATTERN = Pattern.compile(CONTAINER_REGEX, Pattern.DOTALL);

    public static List<Appointment> scheduleByDay(String raw, Weekday weekday) {
        List<Appointment> appointments = new ArrayList<>();
        String date = getDate(raw);
        Matcher contextSplitter = CONTAINER_PATTERN.matcher(raw);
        while (contextSplitter.find())
        {
            String contextGroup = contextSplitter.group();
            Matcher contextMatcher = CONTEXT_PATTERN.matcher(contextGroup);
            if (contextMatcher.find())
            {
                String context = contextMatcher.group();
                String address = APO.dictionary.get(context).adressOf;

                for (String line : contextGroup.split(NEW_LINE))
                {
                    if (line.equals(context) || line.equals("Container") || line.contains("Geschlossen")) break;
                    var appointment = schedule(weekday, date, context, address, line);
                    appointment.ifPresent(appointments::add);
                }
            }
        }
        return appointments;
    }
    private static Optional<Appointment> schedule(Weekday weekday, String date, String context, String address, String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            Optional<Appointment> appointment = switch (token) {

                case "Container" -> {
                    if (tokenizer.hasMoreTokens())
                    {
                        String employee = tokenizer.nextToken();
                        String start = getFormattedDateTime.apply(date.concat(String.valueOf(catalogue.get(context).get(weekday).opens())));
                        String end = getFormattedDateTime.apply(date.concat(String.valueOf(catalogue.get(context).get(weekday).handover())));
                        String description = flushTokenizer(tokenizer);
                        yield Optional.of(new Appointment(employee, "Arbeiten " + context, description, start, end, address));
                    }
                    yield Optional.empty();
                }

                case "Bierstadt", "Rheinstr.", "Dürerplatz", "Erbenheim" -> {
                    if (tokenizer.hasMoreTokens())
                    {
                        String employee = tokenizer.nextToken();
                        String start = getFormattedDateTime
                                .apply(date.concat(String.valueOf(catalogue.get(context).get(weekday).handover())));
                        String end = getFormattedDateTime
                                .apply(date.concat(String.valueOf(catalogue.get(context).get(weekday).closes())));
                        String description = flushTokenizer(tokenizer);
                        yield Optional.of(new Appointment(employee, "Arbeiten " + context, description, start, end, address));
                    }
                    yield Optional.empty();
                }

                default -> {
                    if (!token.matches(DATE_REGEX) && !token.matches(DURATION_REGEX) && !token.matches(WEEKDAY_REGEX))
                    {
                        String employee = token;
                        String start = getFormattedDateTime.apply(date.concat(String.valueOf(catalogue.get(context)
                                .get(weekday).closes())));
                        String end = getFormattedDateTime.apply(date.concat(String.valueOf(catalogue.get(context)
                                .get(weekday).closes().plusHours(4))));
                        String description = flushTokenizer(tokenizer);
                        yield Optional.of(new Appointment(employee, "Arbeiten " + context, description, start, end, address));
                    }
                    yield Optional.empty();
                }
            };
            if(appointment.isPresent()) return appointment;
        }
        return Optional.empty();
    }

    private static String flushTokenizer(StringTokenizer st) {
        StringBuilder res = new StringBuilder();
        while (st.hasMoreTokens()) {
            res.append(" ").append(st.nextToken());
        }
        return res.toString();
    }

}