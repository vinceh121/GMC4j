package me.vinceh121.gmc;

public enum GMCCommand {
	GETVER(14, 0), GETCPM(2, 0), HEARTBEAT1(2, 0), HEARTBEAT0(0, 0), GETVOLT(1, 0), SPIR(0, 5), GETCFG(256, 0),
	ECFG(2, 0), WCFG(0, 2), key(0, 1), GETSERIAL(7, 0), POWEROFF(0, 0), CFGUPDATE(2, 0), SETDATEYY(2, 1),
	SETDATEMM(2, 1), SETDATEDD(2, 1), SETTIMEHH(2, 1), SETTIMEMM(2, 1), SETTIMESS(2, 1), FACTORYRESET(2, 0),
	REBOOT(0, 0), SETDATETIME(2, 1), GETDATETIME(7, 0), GETTEMP(4, 0), GETGYRO(7, 0), POWERON(0, 0);
	private int returnBytes;
	private int numberOfArgs;

	GMCCommand(int returnBytes, int numberOfArgs) {
		this.returnBytes = returnBytes;
		this.numberOfArgs = numberOfArgs;
	}

	public int getNumberOfArguments() {
		return numberOfArgs;
	}

	public int getReturnBytes() {
		return returnBytes;
	}
}
