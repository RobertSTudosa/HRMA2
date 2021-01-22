package com.bzbees.hrma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bzbees.hrma.dao.CompanyDocRepository;
import com.bzbees.hrma.entities.CompanyDoc;

@Service
public class CompanyDocService {

	@Autowired
	private CompanyDocRepository compDocRepo;
	
	public CompanyDoc saveCompDoc(CompanyDoc doc) {
		return compDocRepo.save(doc);
	}
	
	public List<CompanyDoc> getCompanyDocsByAgencyId(long agencyId) {
		List<CompanyDoc> docList = compDocRepo.getSavedDocsByAgencyId(agencyId);
		return docList;
	}
	
	public void flushCompDocDb() {
		compDocRepo.flush();
		return;
	}
	
	public void saveAndFlush(CompanyDoc doc) {
		compDocRepo.saveAndFlush(doc);
		return;
		
	}
	
	public CompanyDoc findCompDocById(long id) {
		return compDocRepo.findCompanyDocBycompDocId(id);
	}
	
	public void deleteCompDocById(CompanyDoc doc) {
		compDocRepo.delete(doc);
	}
	
	public void deleteAll(List<CompanyDoc> docList) {
		compDocRepo.deleteAll(docList);
	}
}
