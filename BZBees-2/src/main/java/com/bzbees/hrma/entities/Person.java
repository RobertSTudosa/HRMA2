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
import javax.validation.constraints.FutureOrPresent;

import org.springframework.format.annotation.DateTimeFormat;



@Entity
@Table(name="persons")
public class Person implements Serializable {

	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_generator")
	@SequenceGenerator(name = "person_generator", sequenceName = "person_seq", allocationSize = 1)
	@Column(name="person_id")
	private long personId;

	private String firstName;

	private String lastName;
	
	private String email;

	private int appStatus;

	private String employmentStatus;
	
	private long lastImgId;
	
	private boolean unreadNotifs = false;	
		
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	@JoinTable(name="person_docs",
			joinColumns=@JoinColumn(name="person_id"),
			inverseJoinColumns=@JoinColumn(name="doc_id"))
	private List<Doc> docs= new ArrayList<>();
	
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	@JoinTable(name="person_pics",
			joinColumns=@JoinColumn(name="person_id"),
			inverseJoinColumns=@JoinColumn(name="pic_id"))
	private List<ProfileImg> pics= new ArrayList<>();
	

	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	@JoinTable(name="person_skills",
			joinColumns=@JoinColumn(name="person_id"),
			inverseJoinColumns=@JoinColumn(name="skill_id"))
	private List<Skill> skills = new ArrayList<>();
	
	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	@JoinTable(name="person_lang",
			joinColumns=@JoinColumn(name="person_id"),
			inverseJoinColumns=@JoinColumn(name="lang_id"))
	private List<Language> languages = new ArrayList<>();
	
	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	@JoinTable(name="person_jobs",
			joinColumns=@JoinColumn(name="person_id"),
			inverseJoinColumns=@JoinColumn(name="job_id"))
	private List<Job> jobs = new ArrayList<>();
	
	
	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
			fetch = FetchType.LAZY)
	@JoinTable(name="person_notifications",
			joinColumns=@JoinColumn(name="person_id"),
			inverseJoinColumns=@JoinColumn(name="notification_id"))
	private List<Notification> notifications = new ArrayList<>();
	
	
	
	@OneToMany(
	        mappedBy = "person",
	        cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
	        orphanRemoval = true
	    )
	private List<SocialMedia> socialMedia = new ArrayList<>();
	

	
	
	@OneToOne(mappedBy="person")
	private User user;
	
	private boolean isAffiliatedToAgency = false;

	private String location;

	private String currentJob;
	
	private boolean privateCurrentjob;

	private boolean activeJob;

	private int workExperience;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	@FutureOrPresent 
	private Date availability;
	
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date BirthDate;
	
	private String jobWishDesc;

	private double totalHours;
	
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date statusStartDate;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startJob;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endJob;
	
	public Person() {
		
		
	}
	

	public Person(long personId, String firstName, String lastName, String email, int appStatus,
			String employmentStatus, String location, String currentJob, boolean privateCurrentjob,
			boolean activeJob, int workExperience, @FutureOrPresent Date availability, Date birthDate,
			String jobWishDesc, double totalHours, Date statusStartDate, Date startJob, Date endJob) {
		super();
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.appStatus = appStatus;
		this.employmentStatus = employmentStatus;
		this.location = location;
		this.currentJob = currentJob;
		this.privateCurrentjob = privateCurrentjob;
		this.activeJob = activeJob;
		this.workExperience = workExperience;
		this.availability = availability;
		BirthDate = birthDate;
		this.jobWishDesc = jobWishDesc;
		this.totalHours = totalHours;
		this.statusStartDate = statusStartDate;
		this.startJob = startJob;
		this.endJob = endJob;
	}
	
	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public long getPersonId() {
		return personId;
	}

//	public void setPersonId(long personId) {
//		this.personId = personId;
//	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}


	public List<Skill> getSkills() {
		
		return skills;
	}


	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}


	public void addSkill(Skill skill) {
		if (skills == null) {
			
			skills = new ArrayList<>();
		}
		
		skills.add(skill);
		
	}
	
	public void removeSkill(long skillId) {
		this.skills.remove(skillId);
	}
	
	public List<Language> getLanguages() {
		return languages;
	}


	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}
	
	public void removeLang(long langId) {
		this.languages.remove(langId);
	}
	
	public void addLang(Language lang) {
		if (languages == null) {
			
			languages = new ArrayList<>();
		}
		
		languages.add(lang);
	}

	public List<Job> getJobs() {
		return jobs;
	}


	public void addJob(Job job) {
		if (jobs == null) {
			
			jobs = new ArrayList<>();
		}
		
		jobs.add(job);
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
	
	public List<Doc> getDocs() {
		return docs;
	}


	public void setDocs(List<Doc> docs) {
		this.docs = docs;
	}
	
	
	public void addDoc(Doc doc) {
		if(docs == null) {
			docs = new ArrayList<>();
		}
		
		docs.add(doc);
	}
	
	public void addPic (ProfileImg img) {
		if(pics == null) {
			pics = new ArrayList<>();
		}
		
		pics.add(img);
	}

	public List<ProfileImg> getPics() {
		return pics;
	}




	public void setPics(List<ProfileImg> pics) {
		this.pics = pics;
	}




	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCurrentJob() {
		return currentJob;
	}

	public void setCurrentJob(String currentJob) {
		this.currentJob = currentJob;
	}

	public boolean isPrivateCurrentjob() {
		return privateCurrentjob;
	}

	public void setPrivateCurrentjob(boolean privateCurrentjob) {
		this.privateCurrentjob = privateCurrentjob;
	}

	public boolean isActiveJob() {
		return activeJob;
	}

	public void setActiveJob(boolean activeJob) {
		this.activeJob = activeJob;
	}

	public int getWorkExperience() {
		return workExperience;
	}
	

	public void setWorkExperience(int workExperience) {
		this.workExperience = workExperience;
	}
	
	public Date getBirthDate() {
		return BirthDate;
	}


	public void setBirthDate(Date birthDate) {
		BirthDate = birthDate;
	}

	public Date getAvailability() {
		return availability;
	}

	public void setAvailability(Date availability) {
		this.availability = availability;
	}

	public String getJobWishDesc() {
		return jobWishDesc;
	}

	public void setJobWishDesc(String jobWishDesc) {
		this.jobWishDesc = jobWishDesc;
	}

	public double getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(double totalHours) {
		this.totalHours = totalHours;
	}

	public Date getStatusStartDate() {
		return statusStartDate;
	}

	public void setStatusStartDate(Date statusStartDate) {
		this.statusStartDate = statusStartDate;
	}

	public Date getStartJob() {
		return startJob;
	}

	public void setStartJob(Date startJob) {
		this.startJob = startJob;
	}

	public Date getEndJob() {
		return endJob;
	}

	public void setEndJob(Date endJob) {
		this.endJob = endJob;
	}





	public long getLastImgId() {
		return lastImgId;
	}


	public void setLastImgId(long lastImgId) {
		this.lastImgId = lastImgId;
	}


	public List<SocialMedia> getSocialMedia() {
		return socialMedia;
	}


	public void setSocialMedia(List<SocialMedia> socialMedia) {
		this.socialMedia = socialMedia;
	}


	public List<Notification> getNotifications() {
		return notifications;
	}


	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}


	public void setPersonId(long personId) {
		this.personId = personId;
	}


	public boolean isUnreadNotifs() {
		return unreadNotifs;
	}


	public void setUnreadNotifs(boolean unreadNotifs) {
		this.unreadNotifs = unreadNotifs;
	}




	public boolean isAffiliatedToAgency() {
		return isAffiliatedToAgency;
	}




	public void setAffiliatedToAgency(boolean isAffiliatedToAgency) {
		this.isAffiliatedToAgency = isAffiliatedToAgency;
	}



	

}
