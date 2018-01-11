package toTheMoon;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class HighScoreChecker {
    private static final String newLine = System.getProperty("line.separator");
    private static String newHighScore;

    public static String checkHighScore(AtomicInteger score){
        String fileName = "highscore.txt";
        PrintWriter printWriter = null;
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                newHighScore = "NEW HIGH SCORE ! CONGRATS.";
                printWriter = new PrintWriter(new FileOutputStream(fileName, false));
                printWriter.write(score.toString()+ newLine);
            }else{
                Scanner in = new Scanner(new FileReader("highscore.txt"));
                StringBuilder sb = new StringBuilder();
                while(in.hasNext()) {
                    sb.append(in.next());
                }
                in.close();
                String outString = sb.toString();

                if(Integer.parseInt(outString) < score.get()){
                    printWriter = new PrintWriter(new FileOutputStream(fileName, false));
                    newHighScore = "NEW HIGH SCORE ! CONGRATS.";
                    printWriter.write(score.toString()+ newLine);

                }
            }

        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            return newHighScore;
        }

    }

}
