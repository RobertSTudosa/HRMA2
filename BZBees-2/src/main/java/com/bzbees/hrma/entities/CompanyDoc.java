package com.bzbees.hrma.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="companyDocs")
public class CompanyDoc  implements Serializable {

	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "companydocs_generator")
	@SequenceGenerator(name = "companydocs_generator", sequenceName = "companydocs_seq", allocationSize = 1)
	@Column(name="companydoc_id")
	private long compDocId;
	
	@Column(name="compdoc_name")
	private String docName;
	
	@Column(name="compdoc_type")
	private String docType;
		
	@Lob
	private byte[] data;
	
	@ManyToOne(fetch = FetchType.LAZY)
    private Agency agency;
	
	public CompanyDoc () {
		
	}

	public CompanyDoc(String docName, String docType, byte[] data) {
		super();
		
		this.docName = docName;
		this.docType = docType;
		this.data = data;
	}

	public long getCompDocId() {
		return compDocId;
	}

	public void setCompDocId(long compDocId) {
		this.compDocId = compDocId;
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

	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyDoc )) return false;
        return compDocId != 0L && compDocId == (((CompanyDoc) o).getCompDocId());
    }
	
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
