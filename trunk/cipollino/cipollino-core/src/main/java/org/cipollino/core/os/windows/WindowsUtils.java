package org.cipollino.core.os.windows;

import org.cipollino.core.os.OSUtils;

public class WindowsUtils extends OSUtils {
	final String SYSTEM_SERVICES = "HKLM\\SYSTEM\\CurrentControlSet\\Services";

	public boolean isRegKeyExists(String key) throws Exception {
		String[] cl = new String[] { "reg.exe", "query", key, };
		ExecOutput out = exec(cl);
		return out.errorCode == 0;
	}

	public boolean removeRegKey(String key) throws Exception {
		String[] cl = new String[] { "reg.exe", "delete", key, "/f", };
		ExecOutput out = exec(cl);
		return out.errorCode == 0;
	}

	public String getRegValue(String key, String name) throws Exception {
		String value = "";
		String[] cl = new String[] { "reg.exe", "query", key, "/v", name };
		ExecOutput out = exec(cl);
		if (out.errorCode == 0) {
			for (int i = 0; i < out.stdout.length; i++) {
				String[] tokens = out.stdout[i].trim().split("\\p{Space}+");
				if (tokens.length == 3) {
					value = tokens[2].trim();
				}
			}
		}
		return value;
	}

	public boolean setRegValue(String key, String name, int value) throws Exception {
		String[] cl = new String[] { "reg.exe", "add", key, "/v", name, "/t", "REG_DWORD", "/d", String.valueOf(value),
				"/f" };
		ExecOutput out = exec(cl);
		return out.errorCode == 0;
	}

	public boolean setRegValue(String key, String name, String value) throws Exception {
		String[] cl = new String[] { "reg.exe", "add", key, "/v", name, "/t", "REG_SZ", "/d", value, "/f" };
		ExecOutput out = exec(cl);
		return out.errorCode == 0;
	}

	public boolean startService(String name) throws Exception {
		String[] cl = new String[] { "net.exe", "start", name, };
		ExecOutput out = exec(cl);
		return out.errorCode == 0;
	}

	public boolean stopService(String name) throws Exception {
		String[] cl = new String[] { "net.exe", "stop", name, };
		ExecOutput out = exec(cl);
		return out.errorCode == 0;
	}

	public boolean removeService(String name) throws Exception {
		return removeRegKey(SYSTEM_SERVICES + "\\" + name);
	}

	public boolean updateService2Manual(String name) throws Exception {
		return setRegValue(SYSTEM_SERVICES + "\\" + name, "Start", 3);
	}

	public boolean updateService2Automatic(String name) throws Exception {
		return setRegValue(SYSTEM_SERVICES + "\\" + name, "Start", 2);
	}

	public boolean updateService2Disable(String name) throws Exception {
		return setRegValue(SYSTEM_SERVICES + "\\" + name, "Start", 4);
	}

	public void reboot(int timeout, String comment) throws Exception {
		String[] cl = new String[] { "shutdown", "-r", "-t", String.valueOf(timeout), "-f", "-c",
				"\"" + comment + "\"", };
		exec(cl);
	}

	public void shutdown(int timeout, String comment) throws Exception {
		String[] cl = new String[] { "shutdown", "-s", "-t", String.valueOf(timeout), "-f", "-c",
				"\"" + comment + "\"", };
		exec(cl);
	}
}