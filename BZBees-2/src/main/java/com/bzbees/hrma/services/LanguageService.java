package com.bzbees.hrma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.LanguageRepository;
import com.bzbees.hrma.entities.Language;

@Service
public class LanguageService {
	
	@Autowired
	LanguageRepository langRepo;
	
	
	public Language save (Language lang) {
		return langRepo.save(lang);
	}
	
	public List<Language> langToSave () {
		return langRepo.getLangsToSave(); 
	}
	
	public List<Language> savedLangs () {
		return langRepo.getSavedLangs(); 
	}
	
	public List<Language> getDbLangs () {
		return langRepo.getDbLangs(); 
	}
	
	
	public List<Language> getLangsSavedByPersonId(long personId) {
		
		List<Language> langList = (List<Language>) langRepo.getSavedLangsByPersonId(personId);
		return langList;
	}

	
	public List<Language> getAll () {

		List<Language> allLang = (List<Language>)langRepo.findAll();
		
		return allLang;
	}
	
	public Language findLangById(long id) {
		return langRepo.findLanguageByLanguageId(id);
	}
	
	public void deleteLang(Language lang) {
		langRepo.delete(lang);
	}
	

}
