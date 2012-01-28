package rs2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import rs2.cryption.MD5;
import rs2.sign.signlink;

public class Accounts {

	public static Account[] accounts;

	public static class Account {
		public String name;
		public String password;

		/**
		 * Creates the account.
		 * @param name
		 * @param password
		 * @param uid
		 */
		public Account(String name, String password) {
			this.name = name;
			this.password = password;
		}
	}

	/**
	 * Returns the accounts.
	 * @return
	 */
	public static Account[] getAccounts() {
		return accounts;
	}

	/**
	 * Returns an account for the specified name.
	 * @param name
	 * @return
	 */
	public Account getAccount(String name) {
		if (accounts == null || accounts.length == 0) {
			return null;
		}
		for (int index = 0; index < accounts.length; index++) {
			if (accounts[index].name.equalsIgnoreCase(name)) {
				return accounts[index];
			}
		}
		return null;
	}

	/**
	 * Checks to see if an account for the specified name exists.
	 * @param name
	 * @return
	 */
	public static boolean exists(String name) {
		if (accounts == null || accounts.length == 0) {
			return false;
		}
		for (int index = 0; index < accounts.length; index++) {
			if (accounts[index].name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a new account.
	 * @param account
	 */
	public static void add(String name, String password) {
		Account account = new Account(name, password);
		if (exists(name)) {
			return;
		}
		if (accounts != null) {
			Account[] old = accounts;
			accounts = new Account[old.length + 1];
			for (int index = 0; index < accounts.length; index++) {
				if (index < old.length) {
					accounts[index] = old[index];
				} else {
					accounts[index] = account;
				}
			}
		} else {
			accounts = new Account[1];
			accounts[0] = account;
		}
	}

	/**
	 * Removes the account for a given name.
	 * @param name
	 */
	public static void remove(Account account) {
		if (accounts == null || accounts.length == 0) {
			return;
		}
		for (int index = 0; index < accounts.length; index++) {
			if (accounts[index] == account) {
				accounts[index] = null;
				break;
			}
		}
	}

	/**
	 * Edits an existing account.
	 * @param name
	 * @param account
	 */
	public static void edit(Account account) {
		if (accounts == null || accounts.length == 0 || !exists(account.name)) {
			return;
		}
		for (int index = 0; index < accounts.length; index++) {
			if (accounts[index].name.equalsIgnoreCase(account.name)) {
				accounts[index] = account;
				break;
			}
		}
		write();
	}

	/**
	 * Clears the stores accounts.
	 */
	public static void clear() {
		accounts = null;
	}

	/**
	 * Writes the accounts file.
	 */
	public static void write() {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(signlink.getDirectory() + "accounts.dat"));
			out.writeShort(accounts.length);
			if (getAccounts() != null && accounts.length > 0) {
				out.writeByte(1);
				for (int index = 0; index < accounts.length; index++) {
					out.writeUTF(getAccounts()[index] == null ? "" : getAccounts()[index].name);
					out.writeUTF(getAccounts()[index] == null ? "" : getAccounts()[index].password);
				}
			}
			out.writeByte(0);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads the accounts file.
	 */
	public static void read() {
		File file = new File(signlink.getDirectory() + "accounts.dat");
		if (!file.exists()) {
			return;
		}
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int total = in.readShort();
			readValues(in, total);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads the values for the accounts file.
	 * @param file
	 */
	public static void readValues(DataInputStream in, int total) {
		try {
			do {
				int opCode = in.readByte();
				if (opCode == 0) {
					break;
				}
				if (opCode == 1) {
					for (int index = 0; index < total; index++) {
						String name = in.readUTF();
						String pass = in.readUTF();
						add(name, pass);
					}
				}
			} while (true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}