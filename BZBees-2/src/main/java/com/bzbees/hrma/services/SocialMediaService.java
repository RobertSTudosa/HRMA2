package com.bzbees.hrma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.SocialMediaRepository;
import com.bzbees.hrma.entities.SocialMedia;

@Service
public class SocialMediaService {

	@Autowired
	private SocialMediaRepository socialMediaRepo;
	
	public SocialMedia saveSocMedia(SocialMedia socMedia) {
		return socialMediaRepo.save(socMedia);
	}
	
	public List<SocialMedia> getSocialMediaByAgencyId(long agencyId) {
		List<SocialMedia> socialMediaList = socialMediaRepo.getSocialMediaByAgencyId(agencyId); 
		return socialMediaList;
	}
	
	public void flushSocMediaDb() {
		socialMediaRepo.flush();
		return;
	}
	
	public void saveAndFlush(SocialMedia socMedia) {
		socialMediaRepo.saveAndFlush(socMedia);
		return;	
	}
	
	public SocialMedia findSocMediaById(long id) {
		return socialMediaRepo.findSocialMediaBysocialMediaId(id);
	}
	
	public void deleteSocialMedia(SocialMedia socMedia) {
		socialMediaRepo.delete(socMedia);
	}
	
	public void deleteSocialMediaById(long id) {
		socialMediaRepo.deleteSocialMediaBysocialMediaId(id); 
	}
		
	public SocialMedia socMediaByStringAndAgencyId(String socMediaName, long agencyId) {
		return socialMediaRepo.socialMediaByStringAndAgencyId(socMediaName, agencyId); 	
	}
}
