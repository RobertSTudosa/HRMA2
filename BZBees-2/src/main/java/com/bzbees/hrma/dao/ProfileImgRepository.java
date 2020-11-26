package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
			 
			 		" ORDER BY pic_id DESC LIMIT 1;") 
			 public ProfileImg getLastProfilePic();
	 
	 
	// must be camelcase or else it will not find the instance of the object
	 public ProfileImg findProfileImgByPicId (long id);

}
