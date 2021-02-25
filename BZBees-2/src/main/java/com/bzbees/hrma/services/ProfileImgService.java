package com.bzbees.hrma.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.ProfileImgRepository;
import com.bzbees.hrma.entities.Doc;
import com.bzbees.hrma.entities.ProfileImg;

@Service
public class ProfileImgService {
	
	@Autowired
	private ProfileImgRepository picRepo;
	
	public ProfileImg savePic ( ProfileImg pic) {
		return picRepo.save(pic);
	}
	
	public Optional<ProfileImg> getProfilePic(Long picId) {
		return Optional.of(picRepo.findProfileImgByPicId(picId));
	}
	
	public List<ProfileImg> getProfilePics() {
		return (List<ProfileImg>) picRepo.findAll();
	}
	
	public List<ProfileImg> getProfilePicsToSave() {
		List<ProfileImg> picList = picRepo.getProfilePicsToSave();
		return picList;
	}
	
	public ProfileImg getLastProfilePic (long personId) {
		ProfileImg lastPicUploaded = (ProfileImg) picRepo.getLastProfilePic(personId);
		return lastPicUploaded;
	}
	
	public ProfileImg getLastAgencyPic (long agencyId) {
		ProfileImg lastPicUploaded = (ProfileImg) picRepo.getLastAgencyPic(agencyId);
		return lastPicUploaded;
	}
	
	public ProfileImg getLastJobPic (long jobId) {
		ProfileImg lastPicUploaded = (ProfileImg) picRepo.getLastJobPic(jobId);
		return lastPicUploaded; 
	}
	
	public ProfileImg findProfilePicById(long id) {
		return picRepo.findProfileImgByPicId(id);
	}
	
	public List<ProfileImg> getPicsByPersonId(long personId) {
		List<ProfileImg> picList = picRepo.getSavedPicsByPersonId(personId);
		return picList;
	}
	
	public List<ProfileImg> getPicsByAgencyId(long agencyId) {
		List<ProfileImg> picList = picRepo.getSavedPicsByAgencyId(agencyId);
		return picList;
	}
	
	public List<ProfileImg> getPicsByJobId(long jobId) {
		List<ProfileImg> picList = picRepo.getJobPicsByJobId(jobId);
		return picList;
	}
	
	public void deleteProfileImgById(ProfileImg pic) {
		picRepo.delete(pic);
	}
	

}
