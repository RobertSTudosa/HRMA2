package com.bzbees.hrma.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bzbees.hrma.dto.PersonLikedJobs;
import com.bzbees.hrma.entities.Like;

public interface LikeRepository extends JpaRepository<Like, Long>{
	
	@Query(nativeQuery= true, value="select likes.like_id, likes.date_created, likes.liked_job_job_id, likes.liked_agency_agency_id, likes.like_owner "
			+ " FROM likes "
			+ " WHERE likes.liked_job_job_id = ?1 ; ")
	public Set<Like> findLikesbyJobId(long jobId);
	
	@Query(nativeQuery= true, value="select likes.like_id, likes.date_created, likes.liked_job_job_id, likes.liked_agency_agency_id, likes.like_owner "
			+ " FROM likes "
			+ " WHERE likes.liked_agency_agency_id = ?1 ; ")
	public Set<Like> findLikesbyAgencyId(long jobId);
	
	
	@Query(nativeQuery= true, value="select likes.like_id, likes.date_created, likes.liked_job_job_id, likes.liked_agency_agency_id, likes.like_owner "
			+ " FROM likes "
			+ " WHERE likes.like_owner = ?1 ; ")
	public Set<Like> findLikesbyUsername(String username);
	
	@Query(nativeQuery= true, value="select likes.liked_job_job_id "
			+ " FROM likes "
			+ " WHERE likes.like_owner = ?1 ; ")
	public Set<Long> findLikedJobIdbyUsername(String username);			
	
	
	@Query(nativeQuery= true, value="select likes.liked_agency_agency_id "
			+ " FROM likes "
			+ " WHERE likes.like_owner = ?1 ; ")
	public Set<Long> findLikedAgencyIdbyUsername(String username);
	
	@Query(nativeQuery= true, value="select likes.like_id, likes.date_created, likes.liked_job_job_id, likes.liked_agency_agency_id, likes.like_owner "
			+ " FROM likes "
			+ " WHERE likes.liked_job_job_id = ?1 and likes.like_owner = ?2 ; ")
	public Like findLikesbyJobIdAndUsername(long jobId, String username);
	
	@Query(nativeQuery= true, value="select likes.like_id, likes.date_created, likes.liked_job_job_id, likes.liked_agency_agency_id, likes.like_owner "
			+ " FROM likes "
			+ " WHERE likes.liked_agency_agency_id = ?1 and likes.like_owner = ?2 ; ")
	public Like findLikesbyAgencyIdAndUsername(long agencyId, String username);
	
	@Query(nativeQuery= true, value="select likes.liked_job_job_id as likedJob, likes.like_owner as ownerLike"
			+ "	FROM likes  "
			+ "	WHERE ownerLike = ?1 ;")
	public PersonLikedJobs findLikesByOwner(String owner);
	
	

}
