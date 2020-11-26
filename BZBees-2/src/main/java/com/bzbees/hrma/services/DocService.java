package com.bzbees.hrma.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.DocRepository;
import com.bzbees.hrma.entities.Doc;

@Service
public class DocService {
	
	@Autowired
	private DocRepository docRepo;
	
	public Doc saveDoc (Doc doc) {	
		return docRepo.save(doc);		
	}
	
	public Optional<Doc> getFile(Long fileId) {
		return Optional.of(docRepo.findDocByDocId(fileId));
	}
	
	public List<Doc> getDocs() {
		return (List<Doc>) docRepo.findAll();
	}
	
	public List<Doc> getDocsToSave() {
		List<Doc> docList = docRepo.getDocsToSave();
		return docList;
	}
	
	public List<Doc> getSavedDocs () {
		List<Doc> docList = docRepo.getSavedDocs();
		return docList;
	}
	
	public List<Doc> getDbDocs () {
		List<Doc> docList = docRepo.getDbDocs();
		return docList;
	}
	
	public List<Doc> getDocsByPersonId(long personId) {
		List<Doc> docList = docRepo.getSavedDocsByPersonId(personId);
		return docList;
	}
	

	
	public void flushDocDb() {
		docRepo.flush();
		return;
	}
	
	public void saveAndFlush(Doc doc) {
		docRepo.saveAndFlush(doc);
		return;
	}
	

	
	public Doc findDocById(long id) {
		return docRepo.findDocByDocId(id);
	}
	
	public void deleteDocById(Doc doc) {
		docRepo.delete(doc);
	}

}
