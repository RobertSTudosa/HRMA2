package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.CompanyDoc;
import com.bzbees.hrma.entities.Doc;

@Repository
public interface CompanyDocRepository extends JpaRepository<CompanyDoc, Long> {
	
	@Query(nativeQuery= true,
			 value= "select company_docs.companydoc_id , compdoc_name, compdoc_type, data " +
			 		"	FROM company_docs " + 
			 		"	left outer join agency ON agency.agency_id = company_docs.agency_agency_id "+  
			 		"	WHERE agency.agency_id = ?1 ;"		
			 ) 
	public List<CompanyDoc> getSavedDocsByAgencyId (long AgencyId);
	
	
	public CompanyDoc findCompanyDocBycompDocId(long id);

}
