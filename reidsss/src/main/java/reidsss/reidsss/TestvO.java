package reidsss.reidsss;

public class TestvO {

	private long ip;

	private Integer interfaceId;

	private double caps;

	private double sucReest;

	private double quest;

	private double ttool;

	private double tcaps;

	private String hostname;

	private String itemName;

	public long getIp() {
		return ip;
	}

	public void setIp(long ip) {
		this.ip = ip;
	}

	public Integer getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(Integer interfaceId) {
		this.interfaceId = interfaceId;
	}

	public double getCaps() {
		return caps;
	}

	public void setCaps(double caps) {
		this.caps = caps;
	}

	public double getSucReest() {
		return sucReest;
	}

	public void setSucReest(double sucReest) {
		this.sucReest = sucReest;
	}

	public double getQuest() {
		return quest;
	}

	public void setQuest(double quest) {
		this.quest = quest;
	}

	public double getTtool() {
		return ttool;
	}

	public void setTtool(double ttool) {
		this.ttool = ttool;
	}

	public double getTcaps() {
		return tcaps;
	}

	public void setTcaps(double tcaps) {
		this.tcaps = tcaps;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@Override
	public String toString() {

		return this.caps + "" + this.sucReest;
	}

}
