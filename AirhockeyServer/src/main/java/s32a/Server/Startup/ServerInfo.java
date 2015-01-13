/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Startup;

import s32a.Shared.IServerInfo;

/**
 *
 * @author Kargathia
 */
public class ServerInfo implements IServerInfo {

    private final String name, description, bindingName, IP;
    private final int portNumber;

    public ServerInfo(String name, String description, String bindingName, String IP, int portNumber) {
        System.out.println("New ServerInfo: ");
        System.out.println("name: " + name);
        System.out.println("description: " + description);
        System.out.println("bindingName: " + bindingName);
        System.out.println("IP: " + IP);
        System.out.println("portNumber: " + portNumber);
        this.name = name;
        this.description = description;
        this.bindingName = bindingName;
        this.IP = IP;
        this.portNumber = portNumber;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getBindingName() {
        return this.bindingName;
    }

    @Override
    public String getIP() {
        return this.IP;
    }

    @Override
    public int getPort() {
        return this.portNumber;
    }

}
