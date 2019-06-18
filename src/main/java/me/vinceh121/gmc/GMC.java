package me.vinceh121.gmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;

import com.fazecast.jSerialComm.SerialPort;

public class GMC {
	private InputStream input;
	private OutputStream output;
	private Charset ascii;
	private SerialPort port;

	public static GMC fromComPort(SerialPort port, int baudRate) {
		port.setBaudRate(baudRate);
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 1000);
		port.openPort();
		return new GMC(port.getInputStream(), port.getOutputStream());
	}

	public static GMC fromComPort(String comPort, int baudRate) {
		return fromComPort(SerialPort.getCommPort(comPort), baudRate);
	}

	public GMC(File stream) throws FileNotFoundException {
		this(new FileInputStream(stream), new FileOutputStream(stream));
	}

	public GMC(InputStream in, OutputStream out) {
		this.input = in;
		this.output = out;

		this.ascii = Charset.forName("US-ASCII");
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			public void run() {
				try {
					close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	public short getGyroX() throws IOException {
		byte[] arr = sendCommand(GMCCommand.GETGYRO);
		return (short) (((arr[0] & 0xFF) << 8) | (arr[1] & 0xFF));
	}

	public short getGyroY() throws IOException {
		byte[] arr = sendCommand(GMCCommand.GETGYRO);
		return (short) (((arr[2] & 0xFF) << 8) | (arr[3] & 0xFF));

	}

	public short getGyroZ() throws IOException {
		byte[] arr = sendCommand(GMCCommand.GETGYRO);
		return (short) (((arr[4] & 0xFF) << 8) | (arr[5] & 0xFF));
	}

	public float getTemperature() throws IOException {
		byte[] arr = sendCommand(GMCCommand.GETTEMP);

		if (arr[3] != 0xAA)
			throw new IOException("4th byte isn't 0xAA: " + Integer.toHexString(Byte.toUnsignedInt(arr[3])));

		BigDecimal dec = new BigDecimal(arr[0]);
		dec = dec.scaleByPowerOfTen(dec.precision());
		dec = dec.add(new BigDecimal(arr[1]));
		dec = dec.movePointLeft(new BigDecimal(arr[1]).precision());
		if (arr[2] == 0)
			dec = dec.plus();
		else
			dec = dec.negate();
		return dec.floatValue();
	}

	public Calendar getDateTime() throws IOException {
		byte[] arr = sendCommand(GMCCommand.GETDATETIME);
		if (arr[6] != 0xAA) { // Last byte is constant
			throw new IOException("Last byte is not equal to 0xAA: " + arr[6]);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, arr[0]);
		cal.set(Calendar.MINUTE, arr[1]);
		cal.set(Calendar.DAY_OF_MONTH, arr[2]);
		cal.set(Calendar.HOUR, arr[3]);
		cal.set(Calendar.MINUTE, arr[4]);
		cal.set(Calendar.SECOND, arr[5]);
		return cal;
	}

	public String getConfig() throws IOException {
		return new String(ascii.decode(ByteBuffer.wrap(sendCommand(GMCCommand.GETCFG))).array());
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
	
	public void enableAlarm(boolean enabled) throws IOException {
		if (enabled)
			alarmOn();
		else
			alarmOff();
	}

	public void alarmOn() throws IOException {
		sendCommand(GMCCommand.ALARM1);
	}
	
	public void alarmOff() throws IOException {
		sendCommand(GMCCommand.ALARM0);
	}
	
	public void enableSpeaker(boolean enabled) throws IOException {
		if (enabled)
			speakerOn();
		else
			speakerOff();
	}

	public void speakerOn() throws IOException {
		sendCommand(GMCCommand.SPEAKER1);
	}

	public void speakerOff() throws IOException {
		sendCommand(GMCCommand.SPEAKER0);
	}

	public short getCPM() throws IOException {
		byte[] arr = sendCommand(GMCCommand.GETCPM);
		return (short) (((arr[0] & 0xFF) << 8) | (arr[1] & 0xFF));
	}
	
	public short getCPS() throws IOException {
		byte[] arr = sendCommand(GMCCommand.GETCPS);
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
