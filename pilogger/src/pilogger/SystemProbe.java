package pilogger;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import com.pi4j.system.SystemInfo;

import datachannel.AbstractProbe;
import datachannel.DataChannel;

public class SystemProbe extends AbstractProbe {

	private DataChannel memoryChannel = new DataChannel("System Memory", "System_Memory");
	private DataChannel cpuTempChannel = new DataChannel("System temperature", "System_CPU_temp");
	private DataChannel loadChannel = new DataChannel("System Load", "System_Load");
	private DataChannel[] channels = new DataChannel[]{memoryChannel, cpuTempChannel, loadChannel};
	
	public SystemProbe() {
		MemAndLoadReaderThread memAndLoadReaderThread = new MemAndLoadReaderThread();
		CPUtempReaderThread cpuTempReaderThread = new CPUtempReaderThread();
		
		memAndLoadReaderThread.start();
		cpuTempReaderThread.start();
	}
	
	@Override
	public DataChannel[] getChannels() {
		return channels;
	}
	
	private class MemAndLoadReaderThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					memoryChannel.newData( SystemInfo.getMemoryUsed() );
					loadChannel.newData( ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() );
					sleep(1000);
				}
			} catch (InterruptedException | NumberFormatException | IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	private class CPUtempReaderThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					cpuTempChannel.newData( SystemInfo.getCpuTemperature() );
					sleep(5000);
				}
			} catch (InterruptedException | NumberFormatException | IOException e) {
				e.printStackTrace();
			} 
		}
	}

}
