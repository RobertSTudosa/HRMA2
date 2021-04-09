package com.bzbees.hrma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.NotificationRepository;
import com.bzbees.hrma.entities.Notification;

@Service
public class NotificationService {
	
	@Autowired
	NotificationRepository notifRepo;
	
	
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

}
