/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kargathia
 */
public class CodebaseDeployer {

    private final String codebase, userName, password;
    private String sharedLoc, classesLoc, serverLoc;
    private final String fs = File.separator;

    public CodebaseDeployer(String codebase, String userName, String password) {
        this.codebase = codebase;
        this.userName = userName;
        this.password = password;

        String placeholderName = ";;;;";
        File placeholder = new File(placeholderName);
        String loc = placeholder.getAbsolutePath();
        this.sharedLoc = loc.replace("AirhockeyDeployer"
                + fs + placeholderName, "");
    }

    private void deployServer(){
        this.classesLoc = sharedLoc +
                "AirhockeyServer" + fs +
                "target" + fs +
                "classes" + fs;

        File serverDirFile = new File(classesLoc +
                "s32a" + fs +
                "Server");
        this.serverLoc = serverDirFile.getAbsolutePath();
        System.out.println("serverFile: " + serverLoc);

        Map<String, File> serverFiles = this.getSubFiles(serverDirFile);
        this.uploadFiles(serverFiles);
    }

    /**
     * Recursive method gathering all files in given location
     * @param input
     * @return
     */
    private Map<String, File> getSubFiles(File input){
        Map<String, File> output = new HashMap<>();
        if(!input.isDirectory() && input.length() > 0){
            String desiredFTPLoc = input.getAbsolutePath().replace(classesLoc, "");
            output.put(desiredFTPLoc, input);
            System.out.println("desired ftp location: " + desiredFTPLoc);
        } else if (input.isDirectory()){
            for (File f : input.listFiles()){
                output.putAll(getSubFiles(f));
            }
        }
        return output;
    }

    /**
     * Uploads given files to ftp server.
     * @param input key: desired name on server, Value: file to upload.
     */
    private void uploadFiles(Map<String, File> input){

    }
}
