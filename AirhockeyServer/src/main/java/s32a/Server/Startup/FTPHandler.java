/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Startup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

/**
 *
 * @author Kargathia
 */
public class FTPHandler {

    private final String ftpServer, username, password;
    private String eol = System.getProperty("line.separator");

    public FTPHandler(String ftpServer, String username, String password) {
        this.ftpServer = ftpServer;
        this.username = username;
        this.password = password;
    }

    /**
     * Checks whether client was able to login with given info.
     * @return
     */
    public boolean checkLogin() {
        boolean success = false;
        FTPClient client = null;
        try {
            client = new FTPSClient(false);
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
     * @param input
     */
    public void registerServer(ServerInfo input) {
        File file = this.saveInfoToFile(input);
        if (file == null || file.length() == 0) {
            System.out.println("No file to store: " + file.getAbsolutePath());
            return;
        }

        FTPClient client = new FTPSClient(false);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            System.out.println("connecting");
            client.connect(ftpServer);
            boolean login = client.login(this.username, this.password);
            System.out.println("login: " + login);
            client.enterLocalPassiveMode();

            client.setFileType(FTP.ASCII_FILE_TYPE);

            fis = new FileInputStream(file);
            client.storeFile("/Airhockey/Servers/" + input.getIP(), fis);

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
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes given info to local file, and returns a reference.
     *
     * @param input
     * @return
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
}
