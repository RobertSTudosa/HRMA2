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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tag")
public class Tag implements Serializable {
	
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_generator")
	@SequenceGenerator(name = "tag_generator", sequenceName = "tag_seq", allocationSize = 1)
	@Column(name="tag_id")
	long tag_id;
	
	private String tag;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
	private Job theJob;
	
	
	public Tag() {
		
	}


	public Tag(String tag) {
		this.tag = tag;
	}


	public long getTag_id() {
		return tag_id;
	}


	public void setTag_id(long tag_id) {
		this.tag_id = tag_id;
	}


	public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}


	public Job getTheJob() {
		return theJob;
	}


	public void setTheJob(Job theJob) {
		this.theJob = theJob;
	}
	
	
	
}
