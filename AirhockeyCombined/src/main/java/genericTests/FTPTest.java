/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

/**
 *
 * @author Kargathia
 */
public class FTPTest {

    public FTPTest() {
        FTPClient client = new FTPSClient(false);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            System.out.println("connecting");
            client.connect("athena.fhict.nl");
            boolean login = client.login("i293443", "ifvr2edfh101");
            System.out.println("login: " + login);
            client.enterLocalPassiveMode();
            System.out.println("connected: " + client.isConnected()
                    + ", available: " + client.isAvailable());

            client.setFileType(FTP.ASCII_FILE_TYPE);
    //
            // Create an InputStream of the file to be uploaded
            //
            String filename = ".gitattributes";
            File file = new File(filename);
            file.createNewFile();
            System.out.println(file.length());
            fis = new FileInputStream(file.getAbsolutePath());

    //
            // Store file to server
            //
            System.out.println("storefile: " + file.getAbsolutePath() + " - "
                    + client.storeFile("/Airhockey/" + filename, fis));
            System.out.println("file stored");

            File output = new File("colors.json");
            fos = new FileOutputStream(output.getAbsolutePath());
            client.retrieveFile("/colors.json", fos);


            client.logout();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("exception caught: " + ex.getMessage());
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null){
                    fos.close();
                }
                client.disconnect();
                System.exit(0);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
