package com.bzbees.hrma.entities;

import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="jobs")
public class Job {

	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_generator")
	@SequenceGenerator(name = "job_generator", sequenceName = "job_seq", allocationSize = 1)
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

	private double currency;
	
	@Size(min=2, max=4000)
	private String responsabilities;
	
	@Column(name="working_conditions")
	private String workingConditions;
	
	@Column(name="job_private")
	private boolean jobPrivate=false;



	private String skills;

	private String tags;
	
	@Column(name="necessary_documents")
	private String necessaryDocuments;
	
	


	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_jobs",
			joinColumns=@JoinColumn(name="job_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> persons = new HashSet<>();

	public Job() {

	}

	public Job(String jobTitle, String companyName, String jobLocation, Date startDate, Date endDate, double salary,
			double currency, String responsabilities, String workingConditions, boolean privateJob, String skills, String tags,
			String necessaryDocuments) {
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

	public double getCurrency() {
		return currency;
	}

	public void setCurrency(double currency) {
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

}
