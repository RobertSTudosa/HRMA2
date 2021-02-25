package com.bzbees.hrma.entities;

import java.io.Serializable;
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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.tomcat.util.codec.binary.Base64;

@Entity
@Table(name="profileImg")
public class ProfileImg implements Serializable {
	

	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pics_generator")
	@SequenceGenerator(name = "pics_generator", sequenceName = "pics_seq", allocationSize = 1)
	@Column(name="pic_id")
	private long picId;
	
	private String picName;
	private String picType;
	

	
	@Lob
	private byte[] data;
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_pics",
			joinColumns=@JoinColumn(name="pic_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private List<Person> persons;
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="agency_pics",
			joinColumns=@JoinColumn(name="pic_id"),
			inverseJoinColumns=@JoinColumn(name="agency_id"))
	private List<Agency> agencies;
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="job_pics",
			joinColumns=@JoinColumn(name="pic_id"),
			inverseJoinColumns=@JoinColumn(name="job_id"))
	private List<Job> jobs;
	
	public ProfileImg () {
		
	}

	public ProfileImg(String picName, String picType, byte[] data) {
		super();
		this.picName = picName;
		this.picType = picType;
		this.data = data;
		
	}
	
	

	public long getPicId() {
		return picId;
	}

	public void setPicId(long picId) {
		this.picId = picId;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
	}

	public String getPicType() {
		return picType;
	}

	public void setPicType(String picType) {
		this.picType = picType;
	}
	
	public String generateBase64Image(){
	    return Base64.encodeBase64String(this.getData());
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	public List<Agency> getAgencies() {
		return agencies;
	}

	public void setAgencies(List<Agency> agencies) {
		this.agencies = agencies;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
	
	
		
	

}
