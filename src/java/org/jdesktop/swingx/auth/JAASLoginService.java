package org.jdesktop.swingx.auth;

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * <b>JAASLoginService</b> implements a <b>LoginService</b>
 * that uses JAAS for authentication. <b>JAASLoginService</b> uses the 
 * server name as name of the configuration for JAAS.
 * 
 * @author Bino George
 */
public class JAASLoginService extends LoginService {

	/**
	 * Constructor for <b>JAASLoginService</b>
	 * @param server server name that is also used for the JAAS config name
	 */
	public JAASLoginService(String server) {
		super(server);
	}
	
	
	/**
	 * @inheritDoc
	 * 	 
	 */
	public boolean authenticate(String name, char[] password, String server) {
		try {
			LoginContext loginContext = null;

			loginContext = new LoginContext(getServer(),
					new JAASCallbackHandler(name, password));
			loginContext.login();
			return true;
		} catch (AccountExpiredException e) {
			// TODO log
			e.printStackTrace();
			return false;
		} catch (CredentialExpiredException e) {
			// TODO log
			e.printStackTrace();
			return false;
		} catch (FailedLoginException e) {
			// TODO log
			e.printStackTrace();
			return false;
		} catch (LoginException e) {
			// TODO log
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			// TODO log
			e.printStackTrace();
			return false;
		}
	}

	class JAASCallbackHandler implements CallbackHandler {

		private String name;

		private char[] password;

		public JAASCallbackHandler(String name, char[] passwd) {
			this.name = name;
			this.password = passwd;
		}

		public void handle(Callback[] callbacks) throws java.io.IOException {
			for (int i = 0; i < callbacks.length; i++) {
				if (callbacks[i] instanceof NameCallback) {
					NameCallback cb = (NameCallback) callbacks[i];
					cb.setName(name);
				} else if (callbacks[i] instanceof PasswordCallback) {
					PasswordCallback cb = (PasswordCallback) callbacks[i];
					cb.setPassword(password);
				}
			}
		}

	}

	
}
