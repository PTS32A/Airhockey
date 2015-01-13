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
        String userName = "i293443";
        String password = "ifvr2edfh101";

        Console cnsl = null;
        String alpha = null;

        Scanner scanner = new Scanner(System.in);
        System.out.println("FTP Server: (Leave blank for default)");

        String command = scanner.nextLine();
        if (!command.trim().isEmpty()) {
            ftpAddress = command;
        }

        System.out.println("Username: (Leave blank for default)");
        command = scanner.nextLine();
        if(!command.trim().isEmpty()){
            userName = command;
        }

        System.out.println("password: (Leave blank for default)");
        command = scanner.nextLine();
        if(!command.trim().isEmpty()){
            password = command;
        }

        CodebaseDeployer deployer = new CodebaseDeployer(ftpAddress, userName, password);

    }

}
