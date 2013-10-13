package edu.bistu.hich.autoservice.entity;

import java.sql.Timestamp;

public class Spot {
	private int id;
	private String spotPguid;
	private String spotName;
	private String spotAddr;
	private double spotLat;
	private double spotLon;
	private String spotPhone;
	private Timestamp addTime;
	
	public Spot(int id, String spotPguid, String spotName, String spotAddr, double spotLat, double spotLon, String spotPhone, Timestamp addTime) {
		super();
		this.id = id;
		this.spotPguid = spotPguid;
		this.spotName = spotName;
		this.spotAddr = spotAddr;
		this.spotLat = spotLat;
		this.spotLon = spotLon;
		this.spotPhone = spotPhone;
		this.addTime = addTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSpotPguid() {
		return spotPguid;
	}

	public void setSpotPguid(String spotPguid) {
		this.spotPguid = spotPguid;
	}

	public String getSpotName() {
		return spotName;
	}

	public void setSpotName(String spotName) {
		this.spotName = spotName;
	}

	public String getSpotAddr() {
		return spotAddr;
	}

	public void setSpotAddr(String spotAddr) {
		this.spotAddr = spotAddr;
	}

	public double getSpotLat() {
		return spotLat;
	}

	public void setSpotLat(double spotLat) {
		this.spotLat = spotLat;
	}

	public double getSpotLon() {
		return spotLon;
	}

	public void setSpotLon(double spotLon) {
		this.spotLon = spotLon;
	}

	public String getSpotPhone() {
		return spotPhone;
	}

	public void setSpotPhone(String spotPhone) {
		this.spotPhone = spotPhone;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}

	@Override
	public String toString() {
		return "Spot [id=" + id + ", spotPguid=" + spotPguid + ", spotName=" + spotName + ", spotAddr=" + spotAddr + ", spotLat=" + spotLat + ", spotLon=" + spotLon + ", spotPhone=" + spotPhone
				+ ", addTime=" + addTime + "]";
	}
	
}
