package com.dcits.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.dcits.bean.LinuxInfo;
import com.dcits.util.linux.GetLinuxInfoUtil;

public class TestUtil {
	
	@Test
	public void test1() throws Exception {
		LinuxInfo info = new LinuxInfo("39.108.62.206", "22", "root", "Dctest@123", "", "", "");
		long begin = System.currentTimeMillis();
		info.conect();
		long end = System.currentTimeMillis();
		
		begin = System.currentTimeMillis();
		info.getConn().openSession();
		end = System.currentTimeMillis();
		
		System.out.println(end - begin);
		String str = GetLinuxInfoUtil.execCommand(info.getConn(), "ls -ltr;bash -l test.sh", 999, null, 2, "");
		System.out.println(str);
		String[] strs = str.trim().split("\\n");
		for (String s:strs) {
			System.out.println(s);
		}
		info.disconect();
		
	}
	
	@Test
	public void test2() {
		File file = new File("F:\\apache-tomcat-7.0.82\\webapps\\ResourcesMonitoringPlatform\\infos\\infos.json");
		byte[] buffer = new byte[(int) file.length()];
		List<String> strs = null;
		try {
			long begin = System.currentTimeMillis();			
			IOUtils.read(new FileInputStream(file), buffer);
			long end = System.currentTimeMillis();
			System.out.println("1、" + (end - begin));
			
			begin = System.currentTimeMillis();			
			strs = IOUtils.readLines(new FileReader(file));
			end = System.currentTimeMillis();
			System.out.println("2、" + (end - begin));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(new String(buffer));
		System.out.println(strs.get(0));
		
	}
	
}
