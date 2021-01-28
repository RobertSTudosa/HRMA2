package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.CompanyDoc;
import com.bzbees.hrma.entities.SocialMedia;

@Repository
public interface SocialMediaRepository extends JpaRepository<SocialMedia, Long> {
	
	@Query(nativeQuery= true,
			 value= "select social_media.social_media_id, social_media.name, social_media.agency_agency_id, " + 
					"	social_media.person_person_id, social_media.url_address " + 
			 		"		FROM social_media " + 
			 		"		left outer join agency ON agency.agency_id = social_media.agency_agency_id " + 
			 		"		WHERE agency.agency_id = ?1 ;"
			 ) 
	public List<SocialMedia> getSocialMediaByAgencyId (long AgencyId);
	
	@Query(nativeQuery=true,
				value = "select social_media.social_media_id, social_media.name, social_media.agency_agency_id, " + 
						"	social_media.person_person_id, social_media.url_address " + 
				 		"		FROM social_media " + 
				 		"		left outer join agency ON agency.agency_id = social_media.agency_agency_id " + 
				 		"		WHERE INSTR(social_media.name , ?1) " + 
				 		"   AND (agency.agency_id= ?2 ) ;")
	public SocialMedia socialMediaByStringAndAgencyId(String doc_name, long agencyId);
	
	public SocialMedia findSocialMediaBysocialMediaId(long id);
	
	public void deleteSocialMediaBysocialMediaId(long id);
	
	

}
