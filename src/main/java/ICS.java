import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class ICS implements APO {
    public static final String SRC = System.getProperty("user.home")+"/dev/testzentrum/in/arbeitsplan.pdf";
    public static final String DST = System.getProperty("user.home")+"/dev/testzentrum/out/arbeitsplan.pdf";

    static final String HEAD = """
            BEGIN:VCALENDAR
            VERSION:2.0
            METHOD:PRIVATE
            PRODID:POC_PDF_ICS_GEN\\PoloOlo\\69
            """;
    static final String TAIL = """
            END:VCALENDAR
            """;
    //TODO - FIX NAMEREGEX - FIX TESTS - CLEANUP
    public static void main(String... args) throws Exception{

        Extractor.extract.accept(SRC, DST);

        var raw = SchedulePreProcessor.readClean(DST);

        ArrayList<Appointment> collected = new ArrayList<>();
        for(Weekday w: Weekday.values())
        {
            collected.addAll(Schedule.schedule(w, raw.get(w.ordinal())));
        }
        System.out.println("\n"+groupingByName(collected));
        // finish(groupingByName(collected));
    }
    private static void finish(Map<String, List<Appointment>> appointmentMap){
        appointmentMap.forEach((name, appointments) -> {

            File dir = new File("C:\\dev\\testzentrum\\kalendereinträge\\"+name.toLowerCase()); //TODO

            if(!dir.exists())
            {
                if(!dir.mkdirs())

                {
                    System.out.println("Error while creating"+dir.getPath());
                }
            }
            File res = new File("C:\\dev\\testzentrum\\kalendereinträge\\"+name.toLowerCase()+"\\"+name.toLowerCase()+".ics"); // TODO
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
    private static Map<String, List<Appointment>> groupingByName(ArrayList<Appointment> processed){
        return processed.stream().collect(Collectors.groupingBy(Appointment::who));
    }

}