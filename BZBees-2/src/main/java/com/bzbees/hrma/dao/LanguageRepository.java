package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Language;

@Repository
public interface LanguageRepository extends CrudRepository <Language, Long> {
	
	@Query(nativeQuery= true, value="select lang.lang_id , lang.name, lang.level "
			+ "FROM lang "
			+ "left outer join person_lang ON lang.lang_id = person_lang.lang_id " 
			+ " WHERE person_lang.person_id is NULL;")
	public List<Language> getLangsToSave ();
	
	@Query(nativeQuery= true, value="select lang.lang_id , lang.name, lang.level "
			+ "FROM lang "
			+ "left outer join person_lang ON lang.lang_id = person_lang.lang_id " 
			+ " WHERE person_lang.person_id is NOT NULL;")
	public List<Language> getSavedLangs ();
	
	@Query(nativeQuery= true, value="select lang.lang_id , lang.name, lang.level "
			+ "FROM lang "
			+ "left outer join person_lang ON lang.lang_id = person_lang.lang_id " 
			+ " WHERE person_lang.person_id = ?1 ;")
	public List<Language> getSavedLangsByPersonId (long personId);
	
	@Query(nativeQuery= true, value="select lang.lang_id , lang.name, lang.level "
			+ "FROM lang;")
	public List<Language> getDbLangs ();
	
	public Language findLanguageByLanguageId(long id);

	

}
