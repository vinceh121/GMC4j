package me.vinceh121.gmc.tests;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import me.vinceh121.gmc.GMC;

public class TestPower {
	private GMC gmc;

	public void setup() {
		gmc = GMC.fromComPort("/dev/ttyUSB0", 115200);
	}

	@Test
	public void powerOff() throws IOException, InterruptedException {
		setup();
		gmc.powerOff();
		Thread.sleep(5000);
		gmc.close();
	}

	@Test
	public void powerOn() throws IOException, InterruptedException {
		setup();
		gmc.powerOn();
		Thread.sleep(5000);
		gmc.close();
	}

	@Test
	public void reboot() throws IOException, InterruptedException {
		setup();
		gmc.reboot();
		Thread.sleep(5000);
		gmc.close();
	}

}
