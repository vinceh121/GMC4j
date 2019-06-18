package me.vinceh121.gmc.tests;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import me.vinceh121.gmc.GMC;
import me.vinceh121.gmc.GMCCommand;

public class OpenFile {
	private GMC gmc;

	@Before
	public void setup() {
		gmc = GMC.fromComPort("/dev/ttyUSB0", 115200);
	}

	@Test
	public void test() throws IOException {

		assertNotNull(gmc.getVersion());
		System.out.println("Version: " + gmc.getVersion());

		assertNotNull(gmc.getCPM());
		System.out.println("CPM: " + gmc.getCPM());

		assertNotNull(gmc.getVoltage());
		System.out.println("Voltage: " + gmc.getVoltage());

	}

	@Test
	public void dateTime() throws IOException {
		assertNotNull(gmc.getDateTime());
		System.out.println(gmc.getDateTime().toString());
	}

	@Test
	public void config() throws IOException {
		assertNotNull(gmc.sendCommand(GMCCommand.GETCFG));
		System.out.println("Cfg: " + Arrays.toString(gmc.sendCommand(GMCCommand.GETCFG)));
	}
	
	@Test
	public void temperature() throws IOException {
		assertNotNull(gmc.getTemperature());
		System.out.println(gmc.getTemperature());
	}

}
