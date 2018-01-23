package com.dcits.util.linux;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.dcits.bean.LinuxInfo;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JSchUtil {     
    
    private static Session getSession(String host, int port, String ueseName)  
            throws Exception {         
        Session session = new JSch().getSession(ueseName, host, port);  
        return session;  
    }  
  
    private static Session connect(String host, int port, String ueseName,  
            String password) throws Exception {  
        Session session = getSession(host, port, ueseName);  
        session.setPassword(password);  
        Properties config = new Properties();  
        config.setProperty("StrictHostKeyChecking", "no");  
        session.setConfig(config);  
        session.connect();  
        return session;  
    }  
  
    public static String execCmd(LinuxInfo info, String command) throws Exception {  
    	Session session = connect(info.getHost(), Integer.parseInt(info.getPort()), info.getUsername(), info.getPassword());
    	
        if (session == null) {  
            throw new RuntimeException("Session is null!");  
        }  
        
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        
        InputStream in = exec.getInputStream();  
  
        exec.setCommand(command);  
        exec.connect();  
        
        String result = IOUtils.toString(in, "UTF-8");
        
        exec.disconnect();  
        session.disconnect();
        session = null;
        return result;  
    } 
  
    public static void main(String[] args) throws Exception {  
    	System.out.println(JSchUtil.execCmd(new LinuxInfo("39.108.62.206", "22", "root", "Dctest@123", "", "", ""), "ls -ltr;echo $PATH"));
    }  
}
