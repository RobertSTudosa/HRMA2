package com.bzbees.hrma.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class Agency {
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agency_generator")
	@SequenceGenerator(name = "agency_generator", sequenceName = "agency_seq", allocationSize = 1)
	private long agencyId;
	
	private String agencyName;
	
	private String adminName;
	
	private String userFullName;
	
	private String userCredentials;
	
	private Date userBirthDate;
	
	private String companyDocs;
	

	
	public Agency () {
		
	}
	

	public Agency(String agencyName, String adminName, String userFullName, String userCredentials, Date userBirthDate,
			String companyDocs) {
		super();
		this.agencyName = agencyName;
		this.adminName = adminName;
		this.userFullName = userFullName;
		this.userCredentials = userCredentials;
		this.userBirthDate = userBirthDate;
		this.companyDocs = companyDocs;
	}



	public long getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(long agencyId) {
		this.agencyId = agencyId;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getUserCredentials() {
		return userCredentials;
	}

	public void setUserCredentials(String userCredentials) {
		this.userCredentials = userCredentials;
	}

	public Date getUserBirthDate() {
		return userBirthDate;
	}

	public void setUserBirthDate(Date userBirthDate) {
		this.userBirthDate = userBirthDate;
	}

	public String getCompanyDocs() {
		return companyDocs;
	}

	public void setCompanyDocs(String companyDocs) {
		this.companyDocs = companyDocs;
	}
	
	
	
	

}
