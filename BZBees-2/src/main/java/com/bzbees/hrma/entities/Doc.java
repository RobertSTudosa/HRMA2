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

@Entity
@Table(name="docs")
public class Doc implements Serializable{
	
	

	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "docs_generator")
	@SequenceGenerator(name = "docs_generator", sequenceName = "docs_seq", allocationSize = 1)
	@Column(name="doc_id")
	private long docId;
	
	@Column(name="doc_name")
	private String docName;
	
	@Column(name="doc_type")
	private String docType;
	
	@Column(name="doc_private")
	private boolean docPrivate = false;
	

	@Lob
	private byte[] data;
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_docs",
			joinColumns=@JoinColumn(name="doc_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private List<Person> persons;
		
	

	public Doc () {
		
	}

	


	public Doc(String docName, String docType, byte[] data) {
		super();
		this.docName = docName;
		this.docType = docType;
		this.data = data;
	}
	
	

	public long getDocId() {
		return docId;
	}


	public void setDocId(long docId) {
		this.docId = docId;
	}


	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
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
	
	public boolean isDocPrivate() {
		return docPrivate;
	}


	public void setDocPrivate(boolean docPrivate) {
		this.docPrivate = docPrivate;
	}


	

}
