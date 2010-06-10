package org.cipollino.core.os.unix;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.cipollino.core.os.OSUtils;

public class UnixUtils extends OSUtils {
        public boolean isRoot() throws Exception {
                ExecOutput out = exec("/usr/bin/id");
                return out.stdout.length > 0 && out.stdout[0].indexOf("(root)") >= 0;
        }

        public boolean isUserExists(String name) throws Exception {
                ExecOutput out = exec(new String[] { "id", "-a", name });
                return out.stdout.length > 0 && out.stdout[0].indexOf("(" + name + ")") >= 0;
        }

        public boolean isGroupExists(String name) throws Exception {
                ExecOutput out = exec(new String[] { "/usr/bin/grep", name, "/etc/group" });
                return out.stdout.length > 0 && out.stdout[0].startsWith(name);
        }

        public int createGroup(String name) throws Exception {
                return exec(new String[] { "/usr/sbin/groupadd", name }).errorCode;
        }

        public int deleteGroup(String name) throws Exception {
                return exec(new String[] { "/usr/sbin/groupdel", name }).errorCode;
        }

        public int createUser(String name, String dir, String g, String G, String shell) throws Exception {
                return exec(new String[] { "/usr/sbin/useradd", "-d", dir, "-m", "-g", g, "-G", G, "-s", shell, name }).errorCode;
        }

        public int changeUser(String name, String dir, String g, String G, String shell) throws Exception {
                return exec(new String[] { "/usr/sbin/usermod", "-d", dir, "-m", "-g", g, "-G", G, "-s", shell, name }).errorCode;
        }

        public int deleteUser(String name) throws Exception {
                return deleteUser(name, false);
        }

        public int deleteUser(String name, boolean removeHome) throws Exception {
                if (removeHome)
                        return exec(new String[] { "/usr/sbin/userdel", "-r", name }).errorCode;
                else
                        return exec(new String[] { "/usr/sbin/userdel", name }).errorCode;
        }

        public int chmod(String mode, String file) throws Exception {
                return exec(new String[] { "/bin/chmod", mode, file }).errorCode;
        }

        public int chgrp(String group, String file) throws Exception {
                return exec(new String[] { "/bin/chgrp", "-R", group, file }).errorCode;
        }

        public int chown(String owner, String file) throws Exception {
                return exec(new String[] { "/bin/chown", owner, file }).errorCode;
        }

        public int rm(String file) throws Exception {
                return exec(new String[] { "/bin/rm", "-fR", file }).errorCode;
        }

        public StringBuilder createScriptHeader() {
                return createScriptHeader("/bin/sh");
        }

        public StringBuilder createScriptHeader(String shell) {
                return createScriptHeader(StringUtils.defaultIfEmpty(shell, "/bin/sh"), "");
        }

        public StringBuilder createScriptHeader(String shell, String shellArgs) {
                StringBuilder sb = new StringBuilder();
                sb.append("#!");
                sb.append(shell);
                sb.append(shellArgs);
                sb.append("\n");
                sb.append("\n");
                return sb;
        }

        public ExecOutput runScript(StringBuilder sb) throws Exception {
                return runScript(sb, new String[0]);
        }

        public ExecOutput runScript(StringBuilder sb, String[] args) throws Exception {
                return runScript(sb, args, new String[0], null);
        }

        public ExecOutput runScript(StringBuilder sb, String[] args, String[] env, String runAs) throws Exception {
                sb.append("\n");
                File scriptFile = File.createTempFile("tmp", "", getTempDir());
                StringReader reader = new StringReader(sb.toString());
                FileWriter writer = new FileWriter(scriptFile);
                IOUtils.copy(reader, writer);
                writer.flush();
                writer.close();
                chmod("a+x", scriptFile.getAbsolutePath());

                List<File> tmpFiles = new ArrayList<File>();
                tmpFiles.add(scriptFile);

                if (StringUtils.isNotBlank(runAs)) {
                        StringBuilder builder = createScriptHeader();
                        builder.append(String.format("su \"-c %s $*\" %s", scriptFile.getAbsolutePath(), runAs));
                        scriptFile = File.createTempFile("tmp", "", getTempDir());
                        reader = new StringReader(builder.toString());
                        writer = new FileWriter(scriptFile);
                        IOUtils.copy(reader, writer);
                        writer.flush();
                        writer.close();
                        chmod("a+x", scriptFile.getAbsolutePath());
                        tmpFiles.add(scriptFile);
                }

                List<String> l = new ArrayList<String>();
                l.add(scriptFile.getAbsolutePath());
                for (int i = 0; i < args.length; i++) {
                        l.add(args[i]);
                }
                String[] cl = l.toArray(new String[0]);
                ExecOutput out = exec(cl, env);
                for (File file : tmpFiles) {
                        file.delete();
                }
                return out;
        }
}
 