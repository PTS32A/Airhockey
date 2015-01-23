/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Startup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
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

    /**
     * String containing reference to this server on FTP location.
     * Wiped on server shutdown.
     */
    private String ftpRefLocation = null;

    /**
     * constructor
     * @param ftpServer The name of the ftpserver
     * @param username The username
     * @param password The password
     * @param SSL Whether SSL should be used
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
     * @return Returns a boolean indicating whether the client has been
     * successfully logged in
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
     * Registers
     *
     * @param input The server info to be registered
     * @return The url that should be used as java for codebase purposes
     */
    public String registerServer(ServerInfo input) {
        File infoFile = this.saveInfoToFile(input);
        if (infoFile == null || infoFile.length() == 0) {
            System.out.println("No file to store: " + infoFile.getAbsolutePath());
            return null;
        }

        FTPClient client = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        String output = null;

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

            fis = new FileInputStream(infoFile);
            this.ftpRefLocation = "/Airhockey/Servers/"
                    + input.getIP()
                    + "-" + input.getBindingName()
                    + ".server";
            client.storeFile(this.ftpRefLocation, fis);

            File codebase = new File("codebase.properties");
            fos = new FileOutputStream(codebase.getAbsolutePath());
            client.retrieveFile("/Airhockey/Codebase/codebase.properties", fos);
            fos.close();
            output = this.readCodebaseInfo(codebase);

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
                infoFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    /**
     * Writes given info to local file, and returns a reference.
     *
     * @param input The serverinfo to be saved
     * @return Returns the file that the serverinfo was saved in
     */
    private File saveInfoToFile(ServerInfo input) {
        FileWriter fw = null;
        File file = null;
        BufferedWriter writer = null;
        try {
            file = new File(input.getIP() + ".server");
            System.out.println("Saving to: " + file.getAbsolutePath());
            if (file.exists()) {
                file.delete();
            }
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            file.createNewFile();

            // writes info
            writer.write(":" + input.getName() + eol);
            writer.write(":" + input.getDescription() + eol);
            writer.write(":" + input.getBindingName() + eol);
            writer.write(":" + input.getIP() + eol);
            writer.write(":" + input.getPort());

        } catch (IOException ex) {
            Logger.getLogger(FTPHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(FTPHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return file;
    }

    /**
     * Reads the file containing the codebase address as it should be used by
     * java
     *
     * @param input The file to be read
     * @return Returns the content of the file
     */
    private String readCodebaseInfo(File input) {
        if (input == null) {
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
            if (in != null) {
                in.close();
            }
            input.delete();
        }
        return output;
    }

    /**
     * Unregisters the server
     * @param serverInfo The serverinfo of the server
     */
    void unRegisterServer(ServerInfo serverInfo) {
        FTPClient client = null;

        if (SSL) {
            client = new FTPSClient(false);
        } else {
            client = new FTPClient();
        }

        try {
            System.out.println("connecting");
            client.connect(ftpServer);
            if(!client.login(username, password)){
                return;
            }

            boolean success = client.deleteFile(this.ftpRefLocation);
            System.out.println("dropped remote reference to file: " + success);
            client.logout();

        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("exception caught: " + ex.getMessage());
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
