package com.indosat.bidashboard.ws;

import javax.jws.WebMethod;

import dao.TableauRepoDAO;
import entity.TableauRemainingLicense;
import util.ReadConfig;

public class EISWS {
	
	@WebMethod
	public TableauRemainingLicense geTableauRemainingLicense(){
		TableauRemainingLicense remainingLicense = new TableauRemainingLicense();
		remainingLicense.setUserLicensedSize(TableauRepoDAO.getUserLicensedSize());
		remainingLicense.setUserUnlicensedSize(TableauRepoDAO.getUserUnlicensedSize());
		remainingLicense.setAvailableUsers(Integer.parseInt(ReadConfig.get("tableau.totalLicense"))-remainingLicense.getUserLicensedSize());
		return remainingLicense;
	}
	
	
}