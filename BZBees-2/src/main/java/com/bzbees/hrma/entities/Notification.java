package com.bzbees.hrma.entities;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity()
@Table(name="notifications")
public class Notification implements Serializable {
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_generator")
	@SequenceGenerator(name = "notification_generator", sequenceName = "notification_seq", allocationSize = 1)
	@Column(name="notification_id")
	private long notificationId;
	
	
	@OneToMany(
	        mappedBy = "notification",
	        cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST},
	        orphanRemoval = true
	    )
	@JsonIgnore
	private List<Message> messages = new ArrayList<Message>();
	
	private boolean isRead;
	
	private boolean hasApprove;
	
	@Column(name="date_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_notifications",
			joinColumns=@JoinColumn(name="notification_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> persons = new HashSet<>();
	
	private String firstText;
	
	
	public Notification () {
		
	}

	

	public Notification(List<Message> messages, boolean isRead, boolean hasApprove,
			String firstText, Date dateCreated ) {
		
		this.messages = messages;
		this.isRead = isRead;
		this.hasApprove = hasApprove;
		this.dateCreated = dateCreated;
		this.firstText = firstText;
	}



	public long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(long notificationId) {
		this.notificationId = notificationId;
	}


	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public Set<Person> getPersons() {
		return persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}

	public String getFirstText() {
		return firstText;
	}

	public void setFirstText(String firstText) {
		this.firstText = firstText;
	}



	public Date getDateCreated() {
		return dateCreated;
	}



	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}



	public boolean isHasApprove() {
		return hasApprove;
	}



	public void setHasApprove(boolean hasApprove) {
		this.hasApprove = hasApprove;
	}
	
	
	
	
	
	
}
