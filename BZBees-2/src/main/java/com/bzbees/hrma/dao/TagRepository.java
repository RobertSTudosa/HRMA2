package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	
	@Query(nativeQuery= true, value="select tag.tag_id, tag.tag, tag.the_job_job_id "
			+ " FROM tag "
			+ " WHERE tag.the_job_job_id = ?1 ; ")
	public List<Tag> findTagsbyJobId(long jobId);
}
