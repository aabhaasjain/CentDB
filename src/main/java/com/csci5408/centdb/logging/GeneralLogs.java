package com.csci5408.centdb.logging;

import java.io.*;
import java.util.Date;

public class GeneralLogs {
    public void createGeneralLogs(String userId, String status, String logMessage) throws IOException {
        String general_log="";
        File f = new File("resources\\GeneralLogs.txt");
        if(f.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            if (br.readLine() == null) {
                general_log = "User|Status|Log Message|TimeStamp\n";
            }
        }
        else{
            general_log = "User|Status|Log Message|TimeStamp\n";
        }
        general_log = general_log+userId+"|"+status+"|"+logMessage+"|"+new Date();
        FileWriter writer = new FileWriter("resources\\GeneralLogs.txt",true);
        writer.write(general_log+"\n");
        writer.close();
    }
}
