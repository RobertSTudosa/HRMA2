package com.bzbees.hrma.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.LikeRepository;
import com.bzbees.hrma.entities.Like;

@Service
public class LikeService {
	@Autowired
	LikeRepository likeRepo; 
	
	public Like saveLike(Like like) {
		return likeRepo.save(like);
	}
	
	public Set<Like> findLikesByJobId(long jobId) {
		return likeRepo.findLikesbyJobId(jobId);
	}
	
	public Set<Like> findLikesByAgencyId(long agencyId) {
		return likeRepo.findLikesbyAgencyId(agencyId);
	}
	
	public void deleteLike (Like like) {
		likeRepo.delete(like);
	}
	
	public Like findLikeByJobIdAndUsername (long jobId, String username) {
		return likeRepo.findLikesbyJobIdAndUsername(jobId, username);
	}
	
	public Like findLikeByAgencyIdAndUsername (long agencyId, String username) {
		return likeRepo.findLikesbyAgencyIdAndUsername(agencyId, username);
	}
	
	public Set<Like> findLikesByUsername(String username) {
		return likeRepo.findLikesbyUsername(username);
	}
	
	public Set<Long> findLikedJobsIdsByUsername(String username) {
		return likeRepo.findLikedJobIdbyUsername(username);
	}
	

}
