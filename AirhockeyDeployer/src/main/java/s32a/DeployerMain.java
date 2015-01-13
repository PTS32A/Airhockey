/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a;

import java.io.Console;
import java.util.Scanner;

/**
 *
 * @author Kargathia
 */
public class DeployerMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        FTPTest test = new FTPTest();
        
        String ftpAddress = "athena.fhict.nl";
        String userName = "";
        String password = "";

        Console cnsl = null;
        String alpha = null;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter FTP server address if it is NOT " + ftpAddress);

        String command = scanner.nextLine();
        if (!command.trim().isEmpty()) {
            ftpAddress = command;
        }

        System.out.println("Username:");
        userName = scanner.nextLine();

        System.out.println("password:");
        password = scanner.nextLine();

        CodebaseDeployer deployer = new CodebaseDeployer(ftpAddress, userName, password);

    }

}
