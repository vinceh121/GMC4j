package me.vinceh121.gmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import com.fazecast.jSerialComm.SerialPort;

public class GMC {
	private InputStream input;
	private OutputStream output;
	private Charset ascii;
	private SerialPort port;

	public static GMC fromComPort(String comPort, int baudRate) {
		SerialPort port = SerialPort.getCommPort(comPort);
		port.setBaudRate(baudRate);
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 1000);
		port.openPort();
		return new GMC(port.getInputStream(), port.getOutputStream());
	}

	public GMC(File stream) throws FileNotFoundException {
		this(new FileInputStream(stream), new FileOutputStream(stream));
	}

	public GMC(InputStream in, OutputStream out) {
		this.input = in;
		this.output = out;

		this.ascii = Charset.forName("US-ASCII");
	}
	
	
	
	public void reboot() throws IOException {
		sendCommand(GMCCommand.REBOOT);
	}
	
	public void powerOn() throws IOException {
		sendCommand(GMCCommand.POWERON);
	}
	
	public void powerOff() throws IOException {
		sendCommand(GMCCommand.POWEROFF);
	}

	public float getVoltage() throws IOException {
		return (float) sendCommand(GMCCommand.GETVOLT)[0] / 10f;
	}

	public short getCPM() throws IOException {
		byte[] arr = sendCommand(GMCCommand.GETCPM);
		return (short) (((arr[0] & 0xFF) << 8) | (arr[1] & 0xFF));
	}

	public String getVersion() throws IOException {
		return new String(ascii.decode(ByteBuffer.wrap(sendCommand(GMCCommand.GETVER))).array());
	}

	public byte[] sendCommand(GMCCommand cmd, Object... args) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(cmd.toString());
		for (Object o : args) {
			sb.append(o);
		}
		sb.append(">>");

		output.write(ascii.encode(CharBuffer.wrap(sb.toString())).array());

		if (cmd.getReturnBytes() <= 0) {
			return null;
		} else {
			byte[] buf = new byte[cmd.getReturnBytes()];
			input.read(buf);
			return buf;
		}
	}
	
	public void close() throws IOException {
		input.close();
		output.close();
		if (port != null)
			port.closePort();
	}

	public SerialPort getPort() {
		return port;
	}

	public void setPort(SerialPort port) {
		this.port = port;
	}

}
