package com.bzbees.hrma.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="jobs")
public class Job implements Serializable {

	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobs_generator")
	@SequenceGenerator(name = "jobs_generator", sequenceName = "jobs_seq", allocationSize = 1)
	@Column(name="job_id")
	private long jobId;
	
	@Column(name="job_title")
	private String jobTitle;
	
	@Column(name="company_name")
	private String companyName;
	
	@Column(name="job_location")
	private String jobLocation;
	
	@Column(name="start_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	
	@Column(name="end_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;

	private double salary;

	private String currency;
	
	@Size(min=2, max=4000)
	private String responsabilities;
	
	@Column(name="working_conditions")
	private String workingConditions;
	
	@Column(name="job_private")
	private boolean jobPrivate=false;

	private String skills;

	private String tags;
	
	private long lastImageId;
	
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	@JoinTable(name="jobs_pics",
			joinColumns=@JoinColumn(name="job_id"),
			inverseJoinColumns=@JoinColumn(name="pic_id"))
	private List<ProfileImg> pics= new ArrayList<>();
	
	@OneToMany(
	        mappedBy = "theJob",
	        cascade= {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST}
//	        orphanRemoval = true
	    )
	private List<Tag> jobTags = new ArrayList<>();
	
	
	@OneToMany(
	        mappedBy = "likedJob",
	        cascade= {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST}
//	        orphanRemoval = true
	    )
	private Set<Like> jobLikes = new HashSet<>();
	
	private int jobLikesCount = 0;
	
	
	@Column(name="necessary_documents")
	private String necessaryDocuments;
	

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_jobs",
			joinColumns=@JoinColumn(name="job_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> persons = new HashSet<>();
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_jobsInList",
			joinColumns=@JoinColumn(name="job_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> personsInList = new HashSet<>();
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_jobsValidDate",
			joinColumns=@JoinColumn(name="job_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> personsValidDate = new HashSet<>();
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_jobsApplied",
			joinColumns=@JoinColumn(name="job_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> personsApplied = new HashSet<>();
	
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_jobsRejected",
			joinColumns=@JoinColumn(name="job_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> personsRejected = new HashSet<>();
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_jobsApproved",
			joinColumns=@JoinColumn(name="job_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> personsApproved = new HashSet<>();
	
		
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private Agency theAgency;
	
	

	public Job() {

	}

	public Job(String jobTitle, String companyName, String jobLocation, Date startDate, Date endDate, double salary,
			String currency, String responsabilities, String workingConditions, boolean privateJob, String skills, String tags,
			String necessaryDocuments, int jobLikesCount) {
		super();
		this.jobTitle = jobTitle;
		this.companyName = companyName;
		this.jobLocation = jobLocation;
		this.startDate = startDate;
		this.endDate = endDate;
		this.salary = salary;
		this.currency = currency;
		this.responsabilities = responsabilities;
		this.workingConditions = workingConditions;
		this.jobPrivate = privateJob;
		this.skills = skills;
		this.tags = tags;
		this.necessaryDocuments = necessaryDocuments;
		this.jobLikesCount = jobLikesCount;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getJobLocation() {
		return jobLocation;
	}

	public void setJobLocation(String jobLocation) {
		this.jobLocation = jobLocation;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getResponsabilities() {
		return responsabilities;
	}

	public void setResponsabilities(String responsabilities) {
		this.responsabilities = responsabilities;
	}

	public String getWorkingConditions() {
		return workingConditions;
	}

	public void setWorkingConditions(String workingConditions) {
		this.workingConditions = workingConditions;
	}
	
	public boolean isJobPrivate() {
		return jobPrivate;
	}
	
	

	public void setJobPrivate(boolean jobPrivate) {
		this.jobPrivate = jobPrivate;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getNecessaryDocuments() {
		return necessaryDocuments;
	}

	public void setNecessaryDocuments(String necessaryDocuments) {
		this.necessaryDocuments = necessaryDocuments;
	}
	
	
	public Set<Person> getPersons() {
		return persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}

	public Agency getTheAgency() {
		return theAgency;
	}

	public void setTheAgency(Agency theAgency) {
		this.theAgency = theAgency;
	}
	
	
//	public void copyToArrayListOfTags(String tags) {
//		String[] trimmedTags = tags.trim().split("\\s+");
//		
//		for(String s : trimmedTags) {
//			Tag tag = new Tag(s);
//			tag.setTheJob(this);
//			this.jobTags.add(tag);
//			System.out.println("Tag inserted here is ====> " + s);
//		}
//	}

	public List<Tag> getJobTags() {
		return jobTags;
	}

	public void setJobTags(List<Tag> jobTags) {
		this.jobTags = jobTags;
	}

	public List<ProfileImg> getPics() {
		return pics;
	}

	public void setPics(List<ProfileImg> pics) {
		this.pics = pics;
	}

	public long getLastImageId() {
		return lastImageId;
	}

	public void setLastImageId(long lastImageId) {
		this.lastImageId = lastImageId;
	}
	
	public long setAutoLastImageId() {
		
		if(!this.pics.isEmpty() && this.pics.size() > 0) {
		
			 this.lastImageId = this.pics.size()-1;
			 
			return this.lastImageId;
		} 
			return this.lastImageId = 0;
	}

	public Set<Like> getJobLikes() {
		return jobLikes;
	}

	public void setJobLikes(Set<Like> jobLikes) {
		this.jobLikes = jobLikes;
	}

	public int getJobLikesCount() {
		return jobLikesCount;
	}

	public void setJobLikesCount(int jobLikesCount) {
		this.jobLikesCount = jobLikesCount;
	}

	public Set<Person> getPersonsInList() {
		return personsInList;
	}

	public void setPersonsInList(Set<Person> personsInList) {
		this.personsInList = personsInList;
	}

	public Set<Person> getPersonsApplied() {
		return personsApplied;
	}

	public void setPersonsApplied(Set<Person> personsApplied) {
		this.personsApplied = personsApplied;
	}

	public Set<Person> getPersonsApproved() {
		return personsApproved;
	}

	public void setPersonsApproved(Set<Person> personsApproved) {
		this.personsApproved = personsApproved;
	}

	@Override
	public String toString() {
		
		return this.jobTitle + this.companyName + this.jobLocation;
	}

	public Set<Person> getPersonsValidDate() {
		return personsValidDate;
	}

	public void setPersonsValidDate(Set<Person> personsValidDate) {
		this.personsValidDate = personsValidDate;
	}

	public Set<Person> getPersonsRejected() {
		return personsRejected;
	}

	public void setPersonsRejected(Set<Person> personsRejected) {
		this.personsRejected = personsRejected;
	}
	
	
	
	
	
	

}
