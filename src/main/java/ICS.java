import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class ICS implements APO {
    public static final String SRC = System.getProperty("user.home")+"/dev/testzentrum/in/arbeitsplan.pdf";
    public static final String DST = System.getProperty("user.home")+"/dev/testzentrum/out/arbeitsplan.txt";

    //TODO - FIX NAMEREGEX - FIX TESTS - CLEANUP
    public static void main(String... args) throws Exception{
        // Extract text from PDF
        Extractor.extract(SRC, DST);
        // Remove unnecessary Strings and split by weekdays
        var raw = SchedulePreProcessor.readClean(DST);
        // Create a List to Store the generated Calendar data into
        ArrayList<Appointment> result = new ArrayList<>();
        // Schedule each day and add the appointments to the Result
        for(Weekday w: Weekday.values())
        {
            var generated = Schedule.scheduleByDay(raw.get(w.ordinal()), w);
            result.addAll(generated);
        }
        // Write all collected appointments grouped by names to a file
        finish(groupingByName(result));
    }
    private static Map<String, List<Appointment>> groupingByName(ArrayList<Appointment> processed){
        return processed.stream().collect(Collectors.groupingBy(Appointment::who));
    }
    private static void finish(Map<String, List<Appointment>> appointmentMap){
        appointmentMap.forEach((name, appointments) -> {

            File dir = new File(System.getProperty("user.home")+"/dev/testzentrum/kalendereinträge/"+name.toLowerCase());

            if(!dir.exists())
            {
                if(!dir.mkdirs())
                {
                    System.out.println("Error while creating"+dir.getPath());
                }
            }
            File res = new File(System.getProperty("user.home")+"/dev/testzentrum/kalendereinträge/"+name.toLowerCase()+"/"+name.toLowerCase()+".ics");
            try
            {
                if(!res.exists())
                {
                    if(!res.createNewFile())
                    {
                        System.out.println("Error trying to create File: "+res);
                    }
                }
                else
                {
                    System.out.println(res.getAbsolutePath()+" already exists, trying to update: "+res);
                    if(!res.delete())
                    {
                        System.out.println("Error while deleting: "+res);
                    }
                }

            }catch(IOException e)
            {
                throw new RuntimeException(e);
            }

            try(FileWriter writer = new FileWriter(res, true))
            {
                writer.write(HEAD);
                appointments.forEach(e -> {
                    try
                    {
                        writer.write(e.toICS());
                    }catch(IOException e2)
                    {
                        throw new RuntimeException(e2);
                    }
                });
                writer.write(TAIL);
            }catch(IOException e1)
            {
                throw new RuntimeException(e1);
            }
        });

    }
    static final String HEAD = """
            BEGIN:VCALENDAR
            VERSION:2.0
            METHOD:PRIVATE
            PRODID:POC_PDF_ICS_GEN\\PoloOlo\\69
            """;
    static final String TAIL = """
            END:VCALENDAR
            """;
}