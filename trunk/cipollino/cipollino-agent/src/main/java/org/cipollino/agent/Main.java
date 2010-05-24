package org.cipollino.agent;

import java.io.IOException;
import java.util.List;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Main {
	public static void main(String[] args) {
		List<VirtualMachineDescriptor> machineDescriptors = VirtualMachine
				.list();
		String pid = null;
		for (VirtualMachineDescriptor virtualMachineDescriptor : machineDescriptors) {
			System.out.println(virtualMachineDescriptor.displayName() + " "
					+ virtualMachineDescriptor.id());
			if (virtualMachineDescriptor.displayName().startsWith("org.jboss")) {
				pid = virtualMachineDescriptor.id();
			}
		}
		if (pid != null) {
			System.out.println(pid);
			try {
				VirtualMachine machine = VirtualMachine.attach(pid);
				machine
						.loadAgent(
								"/Users/mark/projects/cipollino/cipollino-build/target/output/cipollino/lib/cipollino-agent-0.2-SNAPSHOT.jar",
								"--file=/Users/mark/projects/cipollino/cipollino-agent/src/test/resources/jboss-control-file.xml");
			} catch (AttachNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AgentLoadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AgentInitializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
