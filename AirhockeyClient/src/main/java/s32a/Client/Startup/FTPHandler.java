/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.Startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import s32a.Shared.ServerInfo;

/**
 *
 * @author Kargathia
 */
public class FTPHandler {
    
    private final String ftpServer, username, password;
    private final boolean SSL;
    private String eol = System.getProperty("line.separator");
    
    private String codebaseURL = null;

    /**
     * Get the value of codebaseURL
     *
     * @return the value of codebaseURL
     */
    public String getCodebaseURL() {
        return codebaseURL;
    }

    /**
     * @return the URL of the active FTP server
     */
    public String getFTPServerURL(){
        return this.ftpServer;
    }

    /**
     * Constructor
     * @param ftpServer
     * @param username
     * @param password
     * @param SSL
     */
    public FTPHandler(String ftpServer, String username, String password, boolean SSL) {
        this.ftpServer = ftpServer;
        this.username = username;
        this.password = password;
        this.SSL = SSL;
    }
    
    /**
     * Checks whether client was able to login with given info.
     *
     * @return
     */
    public boolean checkLogin() {
        boolean success = false;
        FTPClient client = null;
        try {
            if (SSL) {
                client = new FTPSClient(false);
            } else {
                client = new FTPClient();
            }

            client.connect(this.ftpServer);
            success = client.login(username, password);
        } catch (IOException ex) {
            success = false;
            Logger.getLogger(FTPHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (client != null) {
                try {
                    client.logout();
                } catch (IOException ex) {
                    Logger.getLogger(FTPHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return success;
    }
    
    /**
     * Retrieves server and codebase info from FTP server.
     * Codebase will need to be queried separately afterwards.
     * @return 
     */
    public List<ServerInfo> getFTPData(){
        FTPClient client = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        List<ServerInfo> output = new ArrayList<>();

        if (SSL) {
            client = new FTPSClient(false);
        } else {
            client = new FTPClient();
        }

        try {
            System.out.println("connecting");
            client.connect(ftpServer);
            boolean login = client.login(this.username, this.password);
            System.out.println("login: " + login);
            client.enterLocalPassiveMode();
            
            // Reads codebase file from server
            File codebase = new File("codebase.properties");
            fos = new FileOutputStream(codebase.getAbsolutePath());
            client.retrieveFile("/Airhockey/Codebase/codebase.properties", fos);
            fos.close();
            this.codebaseURL = this.readCodebaseInfo(codebase);
            
            // Retrieves all currently active files from server
            File server = null;
            for(FTPFile f : client.listFiles("/Airhockey/Servers")){
                server = new File(f.getName());
                fos = new FileOutputStream(server);
                client.retrieveFile("/Airhockey/Servers/" + f.getName(), fos);
                fos.close();
                output.add(this.readServerFile(server));
            }
            //Removes null entries
            output.remove(null);
            
            client.logout();
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("exception caught: " + ex.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output;
    }
    
    /**
     * Reads the file containing the codebase address as it should be used by java
     * @param input
     * @return 
     */
    private String readCodebaseInfo(File input){
        if(input == null){
            return null;
        }
        String output = null;
        Scanner in = null;
        try {
            in = new Scanner(input);
            output = in.nextLine();
            System.out.println("codebase: " + output);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FTPHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(in != null){
                in.close();
            }
            input.delete();
        }
        return output;
    }
    
    /**
     * Reads the information of a server file
     * @param input
     * @return Returns the information as serverinfo
     */
    private ServerInfo readServerFile(File input){
        Scanner in = null;
        ServerInfo output = null;
        try {
            in = new Scanner(input);
            String name = in.nextLine().substring(1);
            String desc = in.nextLine().substring(1);
            String bindingName = in.nextLine().substring(1);
            String IP = in.nextLine().substring(1);
            int port = Integer.valueOf(in.nextLine().substring(1));
            
            output = new ServerInfo(name, desc, bindingName, IP, port);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FTPHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (NumberFormatException ex) {
            System.out.println("Unable to parse portnumber");
            return null;
        } finally {
            if(in != null){
                in.close();
            }
            input.delete();
        }
        return output;
    }
}
