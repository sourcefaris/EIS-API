package entity;

import java.sql.Date;

public class User {
	private String id ;
	private Date requestDate;
	private String userName;
	private String fullName;
	private String division;
	private String password;
	private String msisdnRole;
	private String msisdnRoleDaily;
	private MenuGroup menuGroup;
	private int activeFlag;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMsisdnRole() {
		return msisdnRole;
	}
	public void setMsisdnRole(String msisdnRole) {
		this.msisdnRole = msisdnRole;
	}
	public String getMsisdnRoleDaily() {
		return msisdnRoleDaily;
	}
	public void setMsisdnRoleDaily(String msisdnRoleDaily) {
		this.msisdnRoleDaily = msisdnRoleDaily;
	}
	public MenuGroup getMenuGroup() {
		return menuGroup;
	}
	public void setMenuGroup(MenuGroup menuGroup) {
		this.menuGroup = menuGroup;
	}
	public int getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(int activeFlag) {
		this.activeFlag = activeFlag;
	}
	
}
