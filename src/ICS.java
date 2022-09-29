import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class ICS implements APO {
    private static final String DTSTAMP = "DTSTAMP:"+STAMP;
    private static final String PRODID = "PRODID:POC_PDF_ICS_GEN\\PoloOlo\\69\r\n"; // Author
    private static final String UID = "UID:%s\r\n";  // User
    private static final String SUMMARY = "SUMMARY:%s\r\n"; // Appointment
    private static final String DTSTART = "DTSTART:%s\r\n";  // Starts
    private static final String DTEND = "DTEND:%s\r\n";  // Ends
    private static final String DESCRIPTION = "DESCRIPTION:%s"; // Comment
    private static final String LOCATION = "LOCATION:%s";  // Location
    private static final HashMap<String, String> mandatory = new HashMap<>(
            Map.of(UID, "User", SUMMARY, "Appointment",
                    DTSTART, "Starts(dd.mm.yyyy HHmm\"Z\")", DTEND, "Ends",
                    DESCRIPTION, "Comment", LOCATION, "Where"));
    private static final Scanner sc = new Scanner(System.in);
    static String HEAD = """
            BEGIN:VCALENDAR
            VERSION:2.0
            METHOD:PRIVATE
            PRODID:POC_PDF_ICS_GEN\\PoloOlo\\69
            """;
    static StringBuffer BODY = new StringBuffer();
    static String TAIL = """
            END:VCALENDAR
            """;

    public static void main(String... args) throws Exception{

        String src = "C:\\dev\\in\\arbeitsplan.pdf";
        String dst = "C:\\dev\\out\\arbeitsplan.txt";

        Extractor.extract.accept(src, dst);

        var data = SchedulePreProcessor.readClean(dst);

        ArrayList<List<Appointment>> collector = new ArrayList<>();

        for(Weekday w : Weekday.values())
        {
            collector.add(Schedule.schedule(w, data[w.ordinal()]));
        }

        Map<String, List<Appointment>> res = new HashMap<>();
        collector.forEach(l ->
        {
            l.forEach(app ->
            {
                if(res.containsKey(app.who()))
                {
                    res.get(app.who()).add(app);
                } else
                {
                    var entry = new ArrayList<Appointment>();
                    entry.add(app);
                    res.put(app.who(), entry);
                }
            });
        });
        finish(res);
    }

    private static void finish(Map<String, List<Appointment>> appointmentMap){
        appointmentMap.forEach((name, appointments) -> {

            File dir = new File("C:\\dev\\testzentrum\\kalendereinträge\\"+name.toLowerCase());

            if(!dir.exists())
            {
                if(dir.mkdirs())
                {
                    System.out.println("Successfully created: "+dir.getPath());
                } else
                {
                    System.out.println("Error while creating"+dir.getPath());
                }
            } else System.out.println(dir.getPath()+" already exists!");

            File res = new File("C:\\dev\\testzentrum\\kalendereinträge\\"+name.toLowerCase()+"\\"+name.toLowerCase()+".ics");
            try
            {
                if(!res.exists())
                {
                    if(res.createNewFile())
                    {
                        System.out.println("Successfully created: "+res);
                    } else System.out.println("Error while creating"+res);

                } else
                {
                    System.out.println(res.getAbsolutePath()+" already exists, trying to delete: "+res);
                    if(res.delete())
                    {
                        System.out.println("Successfully deleted: "+res);
                    } else System.out.println("Error while deleting: "+res);
                }

            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }

            try (FileWriter writer = new FileWriter(res, true))
            {
                writer.write(HEAD);
                appointments.forEach(e -> {
                    try
                    {
                        writer.write(e.toICS());
                    }
                    catch(IOException e2)
                    {
                        throw new RuntimeException(e2);
                    }
                });
                writer.write(TAIL);
            }
            catch(IOException e1)
            {
                throw new RuntimeException(e1);
            }
        });

    }
}

