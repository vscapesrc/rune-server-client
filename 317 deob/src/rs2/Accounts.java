package rs2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import rs2.sign.signlink;
import rs2.util.Misc;

public class Accounts {

	public static Account[] accounts;

	public static class Account {
		public String name;
		public String password;
		public int uses;

		/**
		 * Creates the account.
		 * @param name
		 * @param password
		 * @param uid
		 */
		public Account(String name, String password, int uses) {
			this.name = name;
			this.password = password;
			this.uses = uses;
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
	public static Account getAccount(String name) {
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
	 * Sorts the names of all your accounts into an array
	 * based on the number of times you've used the accounts,
	 * highest to lowest.
	 * @return
	 */
	public static String[] sortNamesByUsage() {
		String[] names = null;
		int[] uses = null;
		if (accounts != null) {
			names = new String[accounts.length];
			uses = new int[accounts.length];
			for (int index = 0; index < accounts.length; index++) {
				names[index] = accounts[index].name;
				uses[index] = accounts[index].uses;
			}
			if (Misc.containsDuplicates(uses)) {
				Arrays.sort(names);
				return names;
			}
			Arrays.sort(uses);
			uses = Misc.reverse(uses);
			for (int index = 0; index < accounts.length; index++) {
				for (int index2 = 0; index2 < uses.length; index2++) {
					if (accounts[index].uses == uses[index2]) {
						names[index2] = accounts[index].name;
					}
				}
			}
		}
		return names;
	}

	/**
	 * Adds a new account.
	 * @param account
	 */
	public static void add(String name, String password, int uses) {
		Account account = new Account(name, password, uses);
		if (exists(name)) {
			getAccount(name).uses += 1;
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
					out.writeInt(getAccounts()[index] == null ? -1 : getAccounts()[index].uses);
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
						int uses = in.readInt();
						add(name, pass, uses);
					}
				}
			} while (true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}