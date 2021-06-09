package com.bzbees.hrma.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
	
	@Autowired
	NotificationService notifServ;
	
	@Async
	public void createAsyncNotificationForCandidateApprovedByAgencyToJob(long candidateId, long agencyId, String agencyName, String jobTitle) 
				throws InterruptedException {
		
		notifServ.createNotificationForCandidateApprovedByAgencyToJob(candidateId, agencyId, agencyName, jobTitle);
	}
	
	@Async
	public void createAsynNotificationForAgencyByCandidateAcceptedJon(long agencyAdminId, long personId, String jobTitle)  throws InterruptedException {
		notifServ.createNotificationForAgencyByCandidateAcceptedJob(agencyAdminId, personId, jobTitle);
	}
	
	@Async
	public void createAsyncNotificationForAgencyByCandidateRejectedJob(long agencyAdminId, long personId, String jobTitle) throws InterruptedException {
		notifServ.createNotificationForRejectionByCandidate(agencyAdminId, personId, jobTitle);
	}
	
	@Async
	public void createNotificationForJobRejectionByAgency(long candidateId, long jobId) {
		notifServ.createNotificationForRejectionByAgency(candidateId, jobId);
	}
	
	@Async
	public void createNotificationForJobAppliedByUser(long candidateId, long jobId, long agencyId) {
		notifServ.createNotificationForJobAppliedByCandidate(candidateId, jobId, agencyId);
	}
		
}
