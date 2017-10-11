package com.dcits.util.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import ch.ethz.ssh2.Session;

public class SSHBufferedReader extends BufferedReader {
	private Session session;

	public SSHBufferedReader(Reader in, Session session) {
		super(in);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void close() throws IOException {
		try {
			super.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
	}

}
