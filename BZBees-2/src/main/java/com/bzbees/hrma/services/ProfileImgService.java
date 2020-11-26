package com.bzbees.hrma.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.ProfileImgRepository;
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
	
	public ProfileImg getLastPic () {
		ProfileImg lastPicUploaded = (ProfileImg) picRepo.getLastProfilePic();
		return lastPicUploaded;
	}
	
	public ProfileImg findProfilePicById(long id) {
		return picRepo.findProfileImgByPicId(id);
	}
	
	public void deleteProfileImgById(ProfileImg pic) {
		picRepo.delete(pic);
	}
	

}
