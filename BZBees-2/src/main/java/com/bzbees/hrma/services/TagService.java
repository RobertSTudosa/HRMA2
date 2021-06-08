package com.bzbees.hrma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.TagRepository;
import com.bzbees.hrma.entities.Tag;

@Service
public class TagService {
	
	@Autowired
	TagRepository tagRepo;
	
	public void tagSave(Tag tag) {
		tagRepo.saveAndFlush(tag);
	}

	public void flush() {
		tagRepo.flush();
		
	}
	
	public List<Tag> findTagsByJobId(long jobId) {
		return tagRepo.findTagsbyJobId(jobId);
	}
	
	public List<Tag> find2TagsByJobId(long jobId) {
		return tagRepo.find2TagsbyJobId(jobId);
	}
	
	public void deleteJobTags(List<Tag> jobTags) {
		tagRepo.deleteAll(jobTags);
	}

}
