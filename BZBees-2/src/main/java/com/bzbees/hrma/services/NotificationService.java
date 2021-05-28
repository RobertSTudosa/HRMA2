package com.bzbees.hrma.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bzbees.hrma.dao.AgencyRepository;
import com.bzbees.hrma.dao.JobRepository;
import com.bzbees.hrma.dao.NotificationRepository;
import com.bzbees.hrma.dao.PersonRepository;
import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Message;
import com.bzbees.hrma.entities.Notification;
import com.bzbees.hrma.entities.Person;

@Service
public class NotificationService {
	
	@Autowired
	NotificationRepository notifRepo;
	
	@Autowired
	PersonRepository persRepo;
	
	@Autowired
	AgencyRepository agencyRepo;
	
	@Autowired
	JobRepository jobRepo;
	
	
//	public Notification saveAndFlush(Notification notif) {
//		 return notifRepo.saveAndFlush(notif);
//	}
	
	public Notification saveNotif(Notification notif) {
		return notifRepo.save(notif);
	}
	
	public List<Notification> findNotificationsByUserId(long userId) {
		return notifRepo.findNotificationsByUserId(userId);
	}
	
	public List<Notification> reverseFindNotificationsByUserId(long userId) {
		return notifRepo.reverseFindNotificationsByUserId(userId);
	}
	
//	public void  flushDB() {
//		notifRepo.flush();
//	}
	
	public Notification findNotifByNotifId(long id) {
		return notifRepo.findNotificationByNotificationId(id);
	}
	
	public void deleteNotificationById(Notification notif) {
		notifRepo.delete(notif);
	}
	
	
//	public Slice<Notification> findNotifByUserId(long userId, Pageable pageable) {
//		return notifRepo.findByUserId(userId, pageable);
//	}
	
	//must be transactional so the session is perpetuated
	@Transactional
	public void createNotificationForCandidateApprovedByAgencyToJob (long candidateId, long jobId, String agencyName, String jobTitle)
			throws InterruptedException {
		
		Person candidate = (Person) persRepo.findPersonByPersonId(candidateId);
		
		//start with the candidate notification
		//candidate is notified the he/she is approved for a job
		Notification candidateNotif = new Notification();
		
		
		//create Message list to parse to Notification		
		Message shortHref = new Message("/showJob/" + Long.toString(jobId));
		Message messageText = new Message("Agency " + agencyName + " has approved you for ");
		Message longText = new Message("Check your profile");
		Message nameText = new Message(jobTitle);
		List<Message> candidateMessages = new ArrayList<>();
		candidateMessages.add(0, messageText);
		candidateMessages.add(1, nameText);
		candidateMessages.add(2, shortHref);
		candidateMessages.add(3, longText);
		
		Notification userNotif = new Notification(candidateMessages, false,false, messageText.getMessage(), new Date());
		messageText.setNotification(userNotif);
		longText.setNotification(userNotif);
		nameText.setNotification(userNotif);
		shortHref.setNotification(userNotif);	
		List<Notification> candidateNotifs = candidate.getNotifications();
		candidateNotifs.add(userNotif);
		candidate.setNotifications(candidateNotifs);
		candidate.setUnreadNotifs(true);
		notifRepo.save(userNotif);
		persRepo.save(candidate);
		
		
		
		System.out.println("I am a new notification for the candidate that was approve by the AGENCY *******************------------->");
		
		
	
	}
	
	@Transactional
	public void createNotificationForAgencyByCandidateAcceptedJob (long agencyAdminId, long personId, String jobTitle) {
		
		
		Person agencyAdminPerson = (Person) persRepo.findPersonByPersonId(agencyAdminId);
		Person personInNotif = (Person) persRepo.findPersonByPersonId(personId);
		String agencyAdminPersonName = agencyAdminPerson.getFirstName();
		
		//start the agency admin accepted notification
		//agency is notified the he/she has accepted for a job
		Notification candidateNotif = new Notification();
		
		
		//create Message list to parse to Notification		
		Message shortHref = new Message("/agency/cprofile?id=" + Long.toString(personId));
		Message messageText = new Message("Candidate ");
		Message longText = new Message("has accepted your " + jobTitle + "'s offer. Check your agency's profile");
		Message nameText = new Message(personInNotif.getFirstName());
		List<Message> candidateMessages = new ArrayList<>();
		candidateMessages.add(0, messageText);
		candidateMessages.add(1, nameText);
		candidateMessages.add(2, shortHref);
		candidateMessages.add(3, longText);
		
		Notification userNotif = new Notification(candidateMessages, false,false, messageText.getMessage(), new Date());
		messageText.setNotification(userNotif);
		longText.setNotification(userNotif);
		nameText.setNotification(userNotif);
		shortHref.setNotification(userNotif);	
		List<Notification> candidateNotifs = agencyAdminPerson.getNotifications();
		candidateNotifs.add(userNotif);
		agencyAdminPerson.setNotifications(candidateNotifs);
		agencyAdminPerson.setUnreadNotifs(true);
		notifRepo.save(userNotif);
		persRepo.save(agencyAdminPerson);
	}
	
	@Transactional
	public void createNotificationForRejectionByCandidate (long agencyAdminId, long personId, String jobTitle) {
		
		Person agencyAdminPerson = (Person) persRepo.findPersonByPersonId(agencyAdminId);
		Person personInNotif = (Person) persRepo.findPersonByPersonId(personId);
		String agencyAdminPersonName = agencyAdminPerson.getFirstName();
		
		//start the agency admin accepted notification
		//agency is notified the he/she has accepted for a job
		Notification candidateNotif = new Notification();
		
		
		//create Message list to parse to Notification		
		Message shortHref = new Message("/agency/cprofile?id=" + Long.toString(personId));
		Message messageText = new Message("Candidate ");
		Message longText = new Message("has REJECTED your " + jobTitle + "'s offer. Check your agency's profile");
		Message nameText = new Message(personInNotif.getFirstName());
		List<Message> candidateMessages = new ArrayList<>();
		candidateMessages.add(0, messageText);
		candidateMessages.add(1, nameText);
		candidateMessages.add(2, shortHref);
		candidateMessages.add(3, longText);
		
		Notification userNotif = new Notification(candidateMessages, false,false, messageText.getMessage(), new Date());
		messageText.setNotification(userNotif);
		longText.setNotification(userNotif);
		nameText.setNotification(userNotif);
		shortHref.setNotification(userNotif);	
		List<Notification> candidateNotifs = agencyAdminPerson.getNotifications();
		candidateNotifs.add(userNotif);
		agencyAdminPerson.setNotifications(candidateNotifs);
		agencyAdminPerson.setUnreadNotifs(true);
		notifRepo.save(userNotif);
		persRepo.save(agencyAdminPerson);
	}
	
	@Transactional
	public void createNotificationForRejectionByAgency (long candidateId, long jobId) {
		
		Agency agency = (Agency) agencyRepo.findAgencyByJobId(jobId);
		long agencyAdminId = agency.getAgencyId();
		
		Person agencyAdminPerson = (Person) persRepo.findPersonByPersonId(agencyAdminId);
		String agencyName = agency.getAgencyName();
		
		Person personInNotif = (Person) persRepo.findPersonByPersonId(candidateId);
		String agencyAdminPersonName = agencyAdminPerson.getFirstName();
		long agencyId = agency.getAgencyId();
		
		//start the agency admin accepted notification
		//agency is notified the he/she has accepted for a job
		Notification candidateNotif = new Notification();
		
		Job job = (Job) jobRepo.findJobByJobId(jobId);
		String jobTitle = job.getJobTitle();
		
		
		//create Message list to parse to Notification		
		Message shortHref = new Message("/agencyProfile?id=" + Long.toString(agencyId));
		Message messageText = new Message("Agency ");
		Message longText = new Message("has denied your application to " + jobTitle + "'s offer. Check your agency's profile");
		Message nameText = new Message(agencyName);
		List<Message> candidateMessages = new ArrayList<>();
		candidateMessages.add(0, messageText);
		candidateMessages.add(1, nameText);
		candidateMessages.add(2, shortHref);
		candidateMessages.add(3, longText);
		
		Notification userNotif = new Notification(candidateMessages, false,false, messageText.getMessage(), new Date());
		messageText.setNotification(userNotif);
		longText.setNotification(userNotif);
		nameText.setNotification(userNotif);
		shortHref.setNotification(userNotif);	
		List<Notification> candidateNotifs = personInNotif.getNotifications();
		candidateNotifs.add(userNotif);
		personInNotif.setNotifications(candidateNotifs);
		personInNotif.setUnreadNotifs(true);
		notifRepo.save(userNotif);
		persRepo.save(personInNotif);
	}
	
	

}
