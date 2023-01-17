package nro.main;

import java.io.*;
import java.util.Calendar;
import java.util.Date;

public class FileIO {
    public static byte[] readFile(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            byte[] ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            return ab;
        } catch(IOException e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if(f.exists())
                f.delete();
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeFileTrans(String name, String value){
//        Util.log(name);
//        Util.log(value);
        Date dateNow = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        int month = calendar.get(Calendar.MONTH);
        month = (month + 1) > 12 ? 1 : (month + 1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
//        String PATH = "trans/" + month + "/" + day + "/" + hour;
        String PATH = "trans/" + month + "/" + day;
//        String fileName = name + "_" + minute + "p" + second + "s.txt";
        String fileName = hour + "h.txt";

        File directory = new File(PATH);

        if (!directory.exists()){
            boolean rsCFolder = directory.mkdirs();
        }

        File file = new File(PATH + "/" + fileName);
        try{

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("*" + minute + "p" + second + "s:\n" + value + "\n\n");
            bw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void writeFileSP(String name, String value){
//        Util.log(name);
//        Util.log(value);
        Date dateNow = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        int month = calendar.get(Calendar.MONTH);
        month = (month + 1) > 12 ? 1 : (month + 1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
//        String PATH = "trans/" + month + "/" + day + "/" + hour;
        String PATH = "check/" + month + "/" + day;
//        String fileName = name + "_" + minute + "p" + second + "s.txt";
        String fileName = hour + "h.txt";

        File directory = new File(PATH);

        if (!directory.exists()){
            boolean rsCFolder = directory.mkdirs();
        }

        File file = new File(PATH + "/" + fileName);
        try{

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("*" + minute + "p" + second + "s:\n" + value + "\n\n");
            bw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
