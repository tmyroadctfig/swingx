package org.jdesktop.swingx.auth;

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.spec.SecretKeySpec;

/* <p>
 * <b>KeyChain</b> is a class that implements the "KeyChain" concept.
 * Fundamentally, it allows you to store multiple keys/credentials 
 * in a central password store. Access to this central store is
 * controlled through a master password. This mechanism is used in
 * many popular client applications where you need to store credentials
 * for multiple servers/accounts. The actual store for the KeyStore
 * can be any OutputStream and it can work in the webstart sandbox
 * using Muffins.
 * </p>
 * <p>
 * To contstruct a <b>KeyChain</b>, you need to pass in an InputStream to the
 * store and it will initialize the KeyStore from the InputStream.
 * You can add and remove entries any time once you have an instance of
 * KeyChain. To persist the KeyChain and reflect any changes, you need to
 * call <b>store</b> method with an OutputStream.
 * </p>
 * 
 * @author Bino George
 */
public class KeyChain {
	private KeyStore store;

	private char[] masterPassword;

	/**
	 * Creates an instance of KeyChain and initializes the store
	 * from the InputStream.
	 * 
	 * @param masterPassword
	 * @param inputStream
	 * @throws IOException
	 */
	public KeyChain(char[] masterPassword, InputStream inputStream)
			throws IOException {
		this.masterPassword = masterPassword;

		try {
			store = KeyStore.getInstance("JCEKS");
			store.load(inputStream, masterPassword);

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Fetches the password for a given account/user and server.
	 * @param user
	 * @param server
	 * @return
	 */
	public String getPassword(String user, String server) {

		try {

			KeyStore.SecretKeyEntry entry2 = (KeyStore.SecretKeyEntry) store
					.getEntry(user + "@" + server,
							new KeyStore.PasswordProtection(masterPassword));
			return new String(entry2.getSecretKey().getEncoded());
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableEntryException ce) {
			ce.printStackTrace();
		} catch (NoSuchAlgorithmException ne) {
			ne.printStackTrace();
		}

		return null;
	}

	/**
	 * Adds a password to the KeyChain for a given account/user and server.
	 * 
	 * @param user
	 * @param server
	 * @param password
	 * @throws IOException
	 */
	public void addPassword(String user, String server, char[] password)
			throws IOException {
		String pass = new String(password);
		SecretKeySpec passwordKey = new SecretKeySpec(pass.getBytes(), "JCEKS");
		KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(passwordKey);
		try {
			store.setEntry(user + "@" + server, entry,
					new KeyStore.PasswordProtection(masterPassword));
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes a password for a given account/user and server.
	 * 
	 * @param user
	 * @param server
	 */
	public void removePassword(String user, String server) {
		try {
			store.deleteEntry(user + "@" + server);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Persists the KeyChain to an OutputStream
	 * 
	 * @param ostream
	 * @throws IOException
	 */

	public void store(OutputStream ostream) throws IOException {
		try {
			store.store(ostream, masterPassword);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException ce) {
			ce.printStackTrace();
		} catch (NoSuchAlgorithmException ne) {
			ne.printStackTrace();
		}
	}


	public static void main(String[] args) {
		try {
			File file = new File("c:\\test.txt");
			FileInputStream fis;
			if (!file.exists()) {
				file.createNewFile();
				fis = null;
			} else {
				fis = new FileInputStream(file);
			}
			KeyChain kc = new KeyChain("test".toCharArray(), fis);
			kc.addPassword("bino", "sun-ds.sfbay", "test123".toCharArray());
			System.out.println("pass = "
					+ kc.getPassword("bino", "sun-ds.sfbay"));

			System.out.println("More testing :");
			for (int i = 0; i < 100; i++) {
				kc.addPassword("" + i, "sun-ds.sfbay", ("" + i).toCharArray());
			}
			for (int i = 0; i < 100; i++) {
				System.out.println("key =" + i + " pass ="
						+ kc.getPassword("" + i, "sun-ds.sfbay"));
			}
			kc.store(new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
