package com.bzbees.hrma.entities;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity()
@Table(name="notifications")
public class Notification implements Serializable  {
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_generator")
	@SequenceGenerator(name = "notification_generator", sequenceName = "notification_seq", allocationSize = 1)
	@Column(name="notification_id")
	private long notificationId;
	
	private String message;
	
	private boolean isRead;
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_notifications",
			joinColumns=@JoinColumn(name="notification_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private List<Person> persons = new ArrayList<>();
	
	public Notification () {
		
	}

	public Notification(String message, boolean isRead) {
		super();
		this.message = message;
		this.isRead = isRead;
		
	}

	public long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(long notificationId) {
		this.notificationId = notificationId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
	
	
	
	
	
	
}
