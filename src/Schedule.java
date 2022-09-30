import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schedule implements APO {
	private static final String locations = "(Bierstadt|Rheinstr\\.|Dürerplatz|Erbenheim)";
	public static List<APO.Appointment> schedule(Weekday weekday, String raw) throws Exception{

		var appointments = new LinkedList<APO.Appointment>();
		String date = getDate(raw);
		// ISOLATING INFORMATION MATCHING A SPECIFIC LOCATION AT AT SPECIFIC DAY
		for(String s: raw.split(".*?(Container\\s)"))
		{
			if(s.isBlank()) continue;

			String context = get("(Bierstadt|Rheinstr\\.|Dürerplatz|Erbenheim)", s, 1);
			// SPLITTING BY LINEBREAKS TO GET ALL THE NECESSARY INFORMATION
			for(String line: s.split("\r\n"))
			{
				LocalTime t1, t2;
				String name, starts, ends;
				if(!line.contains(context))
				{
					name = get("([ZäöüÄÖÜßA-Za-z]*[-\\d]?[ZäöüÄÖÜßA-Za-z]*)", line, 1);
					t1 = catalogue.get(context).get(weekday).opens();
					t2 = catalogue.get(context).get(weekday).handover();
					starts = getDate.apply(date.concat(String.valueOf(t1)));
					ends = getDate.apply(date.concat(String.valueOf(t2)));
					appointments.add(new Appointment(name, "Arbeiten ".concat(context), starts, ends, dictionary.get(context)));
				}
				else
				{
					name = get("(Bierstadt|Rheinstr\\.|Dürerplatz|Erbenheim)\\s([ZäöüÄÖÜßA-Za-z]*[-\\d]?[ZäöüÄÖÜßA-Za-z]*)", line, 2);
					t1 = catalogue.get(context).get(weekday).handover();
					t2 = catalogue.get(context).get(weekday).closes();
					starts = getDate.apply(date.concat(String.valueOf(t1)));
					ends = getDate.apply(date.concat(String.valueOf(t2)));
					appointments.add(new Appointment(name, "Arbeiten ".concat(context), starts, ends, dictionary.get(context)));
				}
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