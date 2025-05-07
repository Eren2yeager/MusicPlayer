package com.mrgautam.service;

import java.io.IOException;

public class NotificationService {
   public void openAppMail() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "Q:\\pro lan stuff\\python stuff\\loby\\sendMail.py");
            pb.inheritIO(); // Optional: show script output
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Notify:" + e.getMessage());
        }
    }
   public void closeAppMail() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "Q:\\pro lan stuff\\python stuff\\loby\\sendMailForClose.py");
            pb.inheritIO(); // Optional: show script output
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Notify:" + e.getMessage());
        }
    }
}
