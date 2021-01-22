package com.bzbees.hrma.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="agency")
public class Agency implements Serializable {
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agency_generator")
	@SequenceGenerator(name = "agency_generator", sequenceName = "agency_seq", allocationSize = 1)
	private long agencyId;
	
	private String agencyName;
	
	private String adminName;
	
	private String userFullName;
	
	private String userCredentials;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date userBirthDate;
	
	//tax number
	private String uniqueRegCode;
	
	//chamber of commerce
	private String regComNumber;
	
	private String legalAddress;
	
	private String webAddress;
	
	private String email;
	
	private String phoneNumber;
	
	
	private String shortDescription;
	
//	//company certificate
//	private CompanyDoc companyRegCertif;

	private CompanyDoc userDocumentId;

	//constitutive act
	private CompanyDoc articleOfIncorporation;
	
	//certificate of status (constatator)
	private CompanyDoc confirmationOfCompanyDetails;
	
	@Column(name="account_non_expired")
    private boolean accountNonExpired = true;
	
	@Column(name="account_non_locked")
    private boolean accountNonLocked = true;
	
	@Column(name="credentials_non_expired")
    private boolean credentialsNonExpired = true;
	
	private boolean active=false;
	
	
	@OneToMany(
	        mappedBy = "agency",
	        cascade= {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}
//	        orphanRemoval = true
	    )
	private List<CompanyDoc> companyDocs = new ArrayList<>();
	
	
	@OneToMany(
	        mappedBy = "agency",
	        cascade = CascadeType.ALL,
	        orphanRemoval = true
	    )
	private List<SocialMedia> socialMedia = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.LAZY, cascade= {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},optional = false)
	@JoinColumn(name = "user_id", referencedColumnName="user_id", nullable = false )
	private User user;
	
	
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	@JoinTable(name="agency_pics",
			joinColumns=@JoinColumn(name="agency_id"),
			inverseJoinColumns=@JoinColumn(name="pic_id"))
	private List<ProfileImg> pics= new ArrayList<>();
	
	
	
	public Agency () {
		
	}
	

	public Agency(long agencyId, String agencyName, String adminName, String userFullName, String userCredentials,
			Date userBirthDate, String uniqueRegCode, String regComNumber, String legalAddress, String webAddress,
			String email, String phoneNumber,  String shortDescription, boolean active) {
		super();
		this.agencyId = agencyId;
		this.agencyName = agencyName;
		this.adminName = adminName;
		this.userFullName = userFullName;
		this.userCredentials = userCredentials;
		this.userBirthDate = userBirthDate;
		this.uniqueRegCode = uniqueRegCode;
		this.regComNumber = regComNumber;
		this.legalAddress = legalAddress;
		this.webAddress = webAddress;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.shortDescription = shortDescription;
		this.active = active;
		
	}


	public long getAgencyId() {
		return (long) agencyId;
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


	public List<CompanyDoc> getCompanyDocs() {
		return companyDocs;
	}


	public void setCompanyDocs(List<CompanyDoc> companyDocs) {
		this.companyDocs = companyDocs;
	}

	public void addDocs(CompanyDoc companyDoc) {
		companyDocs.add(companyDoc);
		companyDoc.setAgency(this);
	}
	
	public void removeDoc(CompanyDoc companyDoc) {
		companyDocs.remove(companyDoc);
		companyDoc.setAgency(null);
	}
	
	public String getUniqueRegCode() {
		return uniqueRegCode;
	}


	public void setUniqueRegCode(String uniqueRegCode) {
		this.uniqueRegCode = uniqueRegCode;
	}


	public String getRegComNumber() {
		return regComNumber;
	}


	public void setRegComNumber(String regComNumber) {
		this.regComNumber = regComNumber;
	}


	public String getLegalAddress() {
		return legalAddress;
	}


	public void setLegalAddress(String legalAddress) {
		this.legalAddress = legalAddress;
	}


	public String getWebAddress() {
		return webAddress;
	}


	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}



	public String getShortDescription() {
		return shortDescription;
	}


	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}


//	public CompanyDoc getCompanyRegCertif() {
//		return companyRegCertif;
//	}
//
//
//	public void setCompanyRegCertif(CompanyDoc companyRegCertif) {
//		this.companyRegCertif = companyRegCertif;
//	}


	public CompanyDoc getUserId() {
		return userDocumentId;
	}


	public void setUserId(CompanyDoc userId) {
		this.userDocumentId = userId;
	}


	public CompanyDoc getArticleOfIncorporation() {
		return articleOfIncorporation;
	}


	public void setArticleOfIncorporation(CompanyDoc articleOfIncorporation) {
		this.articleOfIncorporation = articleOfIncorporation;
	}


	public CompanyDoc getConfirmationOfCompanyDetails() {
		return confirmationOfCompanyDetails;
	}


	public void setConfirmationOfCompanyDetails(CompanyDoc confirmationOfCompanyDetails) {
		this.confirmationOfCompanyDetails = confirmationOfCompanyDetails;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public CompanyDoc getUserDocumentId() {
		return userDocumentId;
	}


	public void setUserDocumentId(CompanyDoc userDocumentId) {
		this.userDocumentId = userDocumentId;
	}


	public List<SocialMedia> getSocialMedia() {
		return socialMedia;
	}


	public void setSocialMedia(List<SocialMedia> socialMedia) {
		this.socialMedia = socialMedia;
	}


	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}


	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}


	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}


	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}


	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}


	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public List<ProfileImg> getPics() {
		return pics;
	}


	public void setPics(List<ProfileImg> pics) {
		this.pics = pics;
	}
	
	
	

}
