package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Doc;

@Repository
public interface DocRepository extends JpaRepository<Doc, Long> {

	
	 @Query(nativeQuery= true,
	 value="select docs.doc_id , doc_name, doc_type, data " + 
	 		"	FROM docs " + 
	 		"	left outer join person_docs ON docs.doc_id = person_docs.doc_id " + 
	 		"       WHERE person_docs.person_id is NULL;") 
	 public List<Doc> getDocsToSave ();
	 
	 @Query(nativeQuery= true,
			 value="select docs.doc_id , doc_name, doc_type, data " + 
			 		"	FROM docs " + 
			 		"	left outer join person_docs ON docs.doc_id = person_docs.doc_id " + 
			 		"       WHERE person_docs.person_id is NOT NULL;") 
	public List<Doc> getSavedDocs ();
	 
	 @Query(nativeQuery= true,
			 value="select docs.doc_id , doc_name, doc_type, doc_private, data " + 
			 		"	FROM docs " + 
			 		"	left outer join person_docs ON docs.doc_id = person_docs.doc_id " + 
			 		"       WHERE person_docs.person_id = ?1 ;") 
	public List<Doc> getSavedDocsByPersonId (long personId);
	 

	 
	 @Query(nativeQuery= true,
			 value="select docs.doc_id , doc_name, doc_type, data " + 
			 		"	FROM docs ;") 
			 public List<Doc> getDbDocs (); 
	 

	// must be camelcase or else it will not find the instance of the object
	public Doc findDocByDocId(long id);

}
