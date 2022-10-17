import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;

public class Schedule implements APO {
	public static List<APO.Appointment> schedule(Weekday weekday, String raw) throws Exception{

		var appointments = new LinkedList<APO.Appointment>();
		String date = getDate(raw);
		// ISOLATING INFORMATION MATCHING A SPECIFIC LOCATION AT AT SPECIFIC DAY

		String s_String = Files.readString(Path.of(System.getProperty("user.home")+"/dev/testzentrum/out/arbeitsplan.txt"));

		final String WEEKDAY_R = "\\s(Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)\\s";
		var x = Pattern.compile("(?<=\\bContainer\\b ).*?(?=\\bContainer\\b |\\z|\\d\\d:\\d\\d \\d\\d:\\d\\d |\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d)", Pattern.DOTALL);

		var res = Arrays.stream(s_String.split(WEEKDAY_R)).collect(Collectors.joining());

		var m = x.matcher(res);

		while(m.find())
		{
			var s = m.group();
			String context = get("(Bierstadt|Rheinstr\\.|Dürerplatz|Erbenheim)\\s", s, 1);
			// SPLITTING BY LINEBREAKS TO GET ALL THE NECESSARY INFORMATION
			for(String line: s.split("\r\n|\r|\n"))
			{
				if(line.contains("Geschlossen") || line.equals(context)) break;

				LocalTime t1, t2;
				String name, starts, ends;
				if(line.contains(context))
				{
					name = get("(Bierstadt|Rheinstr\\.|Dürerplatz|Erbenheim)\\s([\\wÖÄÜöäü]*-?[\\wÖÄÜöäü]*|[\\wÖÄÜöäü]*\\s\\d?)", line, 2);
					t1 = catalogue.get(context).get(weekday).handover();
					t2 = catalogue.get(context).get(weekday).closes();
				}
				else
				{
					if(weekday.equals(APO.Weekday.SUNDAY) && appointments.size()>1)
					{
						name = get("([\\wÖÄÜöäü]*-?[\\wÖÄÜöäü]*|[\\wÖÄÜöäü]*\\s\\d?)", line, 1);
						t1 = LocalTime.of(16, 0);
						t2 = LocalTime.of(20, 0);
					}
					else
					{
						name = get("([\\wÖÄÜöäü]*-?[\\wÖÄÜöäü]*|[\\wÖÄÜöäü]*\\s\\d?)", line, 1);
						t1 = catalogue.get(context).get(weekday).opens();
						t2 = catalogue.get(context).get(weekday).handover();
					}
				}
				starts = getDate.apply(date.concat(String.valueOf(t1)));
				ends = getDate.apply(date.concat(String.valueOf(t2)));
				appointments.add(new APO.Appointment(name, "Arbeiten ".concat(context), starts, ends, dictionary.get(context).adressOf));
			}

		}
		return appointments;
	}
	private static String getDate(String raw) throws Exception{
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
	private static String get(String regex, String str, int group){
		var m = Pattern.compile(regex).matcher(str);
		return m.find()? m.group(group):"";
	}

}