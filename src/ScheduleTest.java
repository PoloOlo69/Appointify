import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ScheduleTest {

    public static final String DST = "C:\\dev\\out\\arbeitsplan_2.txt";

    public static final String[] raw;

    static
    {
        try
        {
            raw = SchedulePreProcessor.readClean(DST);
        }catch(java.io.IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void schedule() throws Exception{
        test1909();
    }

    @Test
    void test1909() throws Exception{
        var sub = Schedule.schedule(APO.Weekday.MONDAY, raw[0]).toArray();
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
    void show(){
        for(String s: raw)
        {
            var split = s.split(".*(Container\\s)");
            for(String spl: split)
            {
                System.out.println(spl+"\n");
            }
            System.out.println(
                    "\n*********"
            );
        }
    }

    static String at(int M, int d, int h, int m){
        return LocalDateTime.of(2022, M, d, h, m, 0).minusHours(2).format(DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss'Z'"));
    }

}