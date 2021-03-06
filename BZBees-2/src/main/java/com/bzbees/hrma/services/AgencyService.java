package com.bzbees.hrma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.AgencyRepository;
import com.bzbees.hrma.entities.Agency;

@Service
public class AgencyService {
	
	@Autowired
	private AgencyRepository agencyRepo;
	
	public Agency saveAgency(Agency agency) {
		return agencyRepo.save(agency);
	}
	
	public Agency findAgencyByUserId(long userId) {
		return agencyRepo.findAgencyFromUserId(userId);
	}
	
	public Agency findAgencyByID(long agencyId) {
		return agencyRepo.findAgencyByagencyId(agencyId);
	}
	
	public void deleteAgencyById(long agencyId) {
		agencyRepo.deleteAgencyByagencyId(agencyId);
	}
	
	public List<Agency> findAll() {
		return (List<Agency>) agencyRepo.findAll();
	}
	
	public Agency findAgencyByJobId(long jobId) {
		return agencyRepo.findAgencyByJobId(jobId);
	}

}
