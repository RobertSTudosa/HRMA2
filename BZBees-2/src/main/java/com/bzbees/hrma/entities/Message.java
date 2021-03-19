package com.bzbees.hrma.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity()
@Table(name="messages")
public class Message implements Serializable {

	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_generator")
	@SequenceGenerator(name = "message_generator", sequenceName = "message_seq", allocationSize = 1)
	@Column(name="message_id")
	private long messageId;
	
	private String message;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private Notification notification;
	
	public Message() {
		
	}

	public Message( String message) {
		this.message = message;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
	
	
	
}
