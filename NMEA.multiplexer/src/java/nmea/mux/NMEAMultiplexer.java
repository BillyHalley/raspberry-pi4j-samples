package nmea.mux;

import nmea.api.Multiplexer;
import nmea.api.NMEAClient;
import nmea.consumers.client.DataFileClient;
import nmea.consumers.client.SerialClient;
import nmea.consumers.client.TCPClient;
import nmea.consumers.reader.DataFileReader;
import nmea.consumers.reader.SerialReader;
import nmea.consumers.reader.TCPReader;

/**
 * Just a SAMPLE, for validation of the concept.
 *
 * Use {@link GenericNMEAMultiplexer} instead (for production).
 */
public class NMEAMultiplexer implements Multiplexer {
	@Override
	public synchronized void onData(String mess) {
		System.out.println(">> From MUX:" + mess);
	}

	@Override
	public void setVerbose(boolean b) {
	}

	private NMEAClient tcpClient;
	private NMEAClient fileClient;
	private NMEAClient serialClient;

	private static String tcpServerName = "192.168.1.1";
	private static int tcpPort = 7001;
	private static String dataFile = "./sample.data/2010-11-08.Nuku-Hiva-Tuamotu.nmea";
	// like "/dev/tty.usbserial"on Mac, "COMx" on Windows, "/dev/ttyUSB0" on Linux
	private static String serialPort = "/dev/tty.usbserial";
	private static int serialBaudRate = 4800;

	public NMEAMultiplexer() {
		tcpClient = new TCPClient(this);
		fileClient = new DataFileClient(this);
		serialClient = new SerialClient(this);

		Runtime.getRuntime().addShutdownHook(new Thread("SampleMUX shutdown hook") {
			public void run() {
				System.out.println("Shutting down multiplexer nicely.");
				tcpClient.stopDataRead();
				fileClient.stopDataRead();
				serialClient.stopDataRead();
			}
		});
		tcpClient.initClient();
		tcpClient.setReader(new TCPReader(tcpClient.getListeners(), tcpServerName, tcpPort));

		fileClient.initClient();
		fileClient.setReader(new DataFileReader(fileClient.getListeners(), dataFile));

		serialClient.initClient();
		serialClient.setReader(new SerialReader(serialClient.getListeners(), serialPort, serialBaudRate));

		tcpClient.startWorking();
		fileClient.startWorking();
		serialClient.startWorking();
	}

	public static void main(String... args) {
		new NMEAMultiplexer();
	}
}
