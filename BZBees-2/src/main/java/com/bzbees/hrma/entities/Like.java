package com.bzbees.hrma.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="likes")
public class Like implements Serializable {
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "likes_generator")
	@SequenceGenerator(name = "likes_generator", sequenceName = "likes_seq", allocationSize = 1)
	@Column(name="like_id")
	private long likeId;
	
	@Column(name="date_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	
	@Column(name="like_owner")
	private String likeOwner;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private Job likedJob;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private Agency likedAgency;

	public Like() {
		
	}

	public Like(Date dateCreated, Job likedJob, Agency likedAgency, String likeOwner) {
		this.dateCreated = dateCreated;
		this.likedJob = likedJob;
		this.likedAgency = likedAgency;
		this.likeOwner = likeOwner;
		
	}

	public long getLikeId() {
		return likeId;
	}

	public void setLikeId(long likeId) {
		this.likeId = likeId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Job getLikedJob() {
		return likedJob;
	}

	public void setLikedJob(Job likedJob) {
		this.likedJob = likedJob;
	}

	public String getLikeOwner() {
		return likeOwner;
	}

	public void setLikeOwner(String likeOwner) {
		this.likeOwner = likeOwner;
	}

	public Agency getLikedAgency() {
		return likedAgency;
	}

	public void setLikedAgency(Agency likedAgency) {
		this.likedAgency = likedAgency;
	}
	
	
	
	
	
	
	
	

}
