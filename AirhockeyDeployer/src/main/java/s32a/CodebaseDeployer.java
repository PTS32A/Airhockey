/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

/**
 *
 * @author Kargathia
 */
public class CodebaseDeployer {

    private final String ftpServer, userName, password;
    private final boolean SSL;
    private String sharedLoc, classesLoc, serverLoc;
    private final String fs = File.separator;

    private List<String> directories;
    private File codebaseFile;

    public CodebaseDeployer(String FTPServer, String userName, String password, 
            String codebaseURL, boolean SSL) {
        this.ftpServer = FTPServer;
        this.userName = userName;
        this.password = password;
        this.SSL = SSL;
        this.codebaseFile = this.getCodebaseFile(codebaseURL);

        String placeholderName = ";;;;";
        File placeholder = new File(placeholderName);
        String loc = placeholder.getAbsolutePath();
        this.sharedLoc = loc.replace("AirhockeyDeployer"
                + fs + placeholderName, "");

        this.deployServer();
    }

    private void deployServer() {
        this.classesLoc = sharedLoc
                + "AirhockeyServer" + fs
                + "target" + fs
                + "classes" + fs;

        File serverDirFile = new File(classesLoc
                + "s32a" + fs
                + "Server");
        this.serverLoc = serverDirFile.getAbsolutePath();
        System.out.println("serverFile: " + serverLoc);

        System.out.println("server files:");
        this.directories = new ArrayList<>();
        Map<String, File> files = this.getSubFiles(serverDirFile);
        System.out.println("");

        System.out.println("Client files:");
        this.classesLoc = this.classesLoc.replace("AirhockeyServer", "AirhockeyClient");
        File clientDirFile = new File(classesLoc
                + "s32a" + fs
                + "Client");
        files.putAll(this.getSubFiles(clientDirFile));
        System.out.println("");

        this.uploadFiles(files);
    }

    /**
     * Recursive method gathering all files in given location
     *
     * @param input
     * @return
     */
    private Map<String, File> getSubFiles(File input) {
        Map<String, File> output = new HashMap<>();
        if (!input.isDirectory() && input.length() > 0) {
            String desiredFTPLoc = input.getAbsolutePath().replace(classesLoc, "");
            output.put(desiredFTPLoc, input);
            System.out.println("desired ftp location: " + desiredFTPLoc);
        } else if (input.isDirectory()) {
            String dir = fs + "Airhockey"
                    + fs + "Codebase"
                    + fs + input.getAbsolutePath().replace(classesLoc, "");
            directories.add(dir);
            System.out.println("--directory: " + dir);
            for (File f : input.listFiles()) {
                output.putAll(getSubFiles(f));
            }
        }
        return output;
    }
    
    /**
     * 
     * @return the file containing the URL linking to the codebase.
     */
    private File getCodebaseFile(String url){      
        FileWriter fw = null;
        File file = null;
        BufferedWriter writer = null;
        try {
            file = new File("codebase.properties");
            System.out.println("Saving to: " + file.getAbsolutePath());
            if (file.exists()) {
                file.delete();
            }
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            file.createNewFile();

            // writes info
            writer.write(url);

        } catch (IOException ex) {
            Logger.getLogger(CodebaseDeployer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(CodebaseDeployer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return file;
    
    }

    /**
     * Uploads given files to ftp server.
     *
     * @param input key: desired name on server, Value: file to upload.
     */
    private void uploadFiles(Map<String, File> input) {

        FTPClient client = null;
        if (SSL) {
            client = new FTPSClient(false);
        } else {
            client = new FTPClient();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            System.out.println("connecting");
            client.connect(ftpServer);
            boolean login = client.login(this.userName, this.password);
            System.out.println("login: " + login);
            client.enterLocalPassiveMode();

//            client.setFileType(FTP.ASCII_FILE_TYPE);

            //Creates all directories required on the server
            System.out.println("creating directories");
            client.makeDirectory("Airhockey");
            client.makeDirectory("Airhockey/Codebase");
            client.makeDirectory("Airhockey/Servers");
            client.makeDirectory("Airhockey/Codebase/s32a");
            System.out.println("default directories made");
            for (String s : directories) {
                client.makeDirectory(s);
            }
            
            //Uploads codebase URL
            fis = new FileInputStream(this.codebaseFile);
            boolean stored = client.storeFile("Airhockey/Codebase/codebase.properties", fis);
            System.out.println("Stored codebase file: " + stored);
            fis.close();
                      
            // Removes references to all servers
            for (FTPFile f : client.listFiles("Airhockey/Servers")){
                if(f.isFile()){
                    System.out.println("Deleting Server Listing: " + f.getName());
                    client.deleteFile("/Airhockey/Servers/" + f.getName());
                }
            }

            // Uploads all class files
            System.out.println("Uploading classes");
            String defaultLoc = fs + "Airhockey" + fs + "Codebase" + fs;
            for (String dest : input.keySet()) {
                fis = new FileInputStream(input.get(dest));
                if (!client.storeFile(defaultLoc + dest, fis)) {
                    System.out.println("unable to save: " + defaultLoc + dest);
                }
                fis.close();
                client.completePendingCommand();
            }

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
}
