package me.vinceh121.gmc.tests;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import me.vinceh121.gmc.GMC;

public class OpenFile {

	@Test
	public void test() throws IOException {
		GMC gmc = GMC.fromComPort("/dev/ttyUSB0", 115200);
		assertNotNull(gmc.getVersion());
		System.out.println("Version: " + gmc.getVersion());

		assertNotNull(gmc.getCPM());
		System.out.println("CPM: " + gmc.getCPM());

		assertNotNull(gmc.getVoltage());
		System.out.println("Voltage: " + gmc.getVoltage());
	}

}
