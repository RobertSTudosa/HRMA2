package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Doc;
import com.bzbees.hrma.entities.ProfileImg;

@Repository
public interface ProfileImgRepository extends CrudRepository<ProfileImg, Long> {
	
	 @Query(nativeQuery= true,
			 value="select profile_img.pic_id , pic_name, pic_type, data " + 
			 		"	FROM profile_img " + 
			 		"	left outer join person_pics ON profile_img.pic_id = person_pics.pic_id " + 
			 		"       WHERE person_pics.person_id is NOT NULL;") 
			 public List<ProfileImg> getProfilePicsToSave ();
	 
	 @Query(nativeQuery= true,
			 value="select profile_img.pic_id , pic_name, pic_type, data " + 
			 		"	FROM profile_img " + 
			 		"	left outer join person_pics ON profile_img.pic_id = person_pics.pic_id " + 
			 		"  	left outer join persons ON person_pics.person_id = persons.person_id   " +
			 		" 	where persons.person_id = ?1 		" + 
			 		" ORDER BY pic_id DESC LIMIT 1;") 
			 public ProfileImg getLastProfilePic(long personId);
	 
	 @Query(nativeQuery= true,
			 value="select profile_img.pic_id , pic_name, pic_type, data " + 
			 		"	FROM profile_img " + 
			 		"	left outer join person_pics ON profile_img.pic_id = person_pics.pic_id " + 
			 		"       WHERE person_pics.person_id = ?1 ;") 
	public List<ProfileImg> getSavedPicsByPersonId (long personId);
	 
	 
	// must be camelcase or else it will not find the instance of the object
	 public ProfileImg findProfileImgByPicId (long id);
	 
	 
	 @Query(nativeQuery= true,
			 value="select profile_img.pic_id , pic_name, pic_type, data " + 
			 		"	FROM profile_img " + 
			 		"	left outer join agency_pics ON profile_img.pic_id = agency_pics.pic_id " + 
			 		"       WHERE agency_pics.agency_id = ?1 ;") 

	public List<ProfileImg> getSavedPicsByAgencyId(long agencyId);

	 @Query(nativeQuery= true,
			 value="select profile_img.pic_id , pic_name, pic_type, data " + 
			 		"	FROM profile_img " + 
			 		"	left outer join agency_pics ON profile_img.pic_id = agency_pics.pic_id " + 
			 		"  	left outer join agency ON agency_pics.agency_id = agency.agency_id   " +
			 		" 	where agency.agency_id = ?1 		" + 
			 		" ORDER BY pic_id DESC LIMIT 1;")  
	public ProfileImg getLastAgencyPic(long agencyId);
	 
	 @Query(nativeQuery = true,
			 value = "select profile_img.pic_id, pic_name, pic_type, data " + 
			 			" FROM profile_img " + 
			 			" left outer join jobs_pics ON profile_img.pic_id = jobs_pics.pic_id " +	
			 			" where jobs_pics.job_id = ?1  ")
	 public List<ProfileImg> getJobPicsByJobId (long jobId);
	 
	 
	 @Query(nativeQuery= true,
			 value="select profile_img.pic_id , pic_name, pic_type, data " + 
			 		"	FROM profile_img " + 
			 		"	left outer join jobs_pics ON profile_img.pic_id = jobs_pics.pic_id " + 
			 		"  	left outer join jobs ON jobs_pics.job_id = jobs.job_id   " +
			 		" 	where jobs.job_id = ?1 		" + 
			 		" ORDER BY pic_id DESC LIMIT 1;")  
	public ProfileImg getLastJobPic(long jobId);

}
