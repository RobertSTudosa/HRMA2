package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bzbees.hrma.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	
	 @Query(nativeQuery=true, value="select notifications.notification_id, is_read, first_text, date_created, has_approve "
	 		+ " from notifications "
	 		+ " left outer join person_notifications ON person_notifications.notification_id = notifications.notification_id "
	 		+ " left outer join user_accounts ON user_accounts.user_id = person_notifications.person_id "
	 		+ " WHERE person_notifications.person_id = ?1 ;") 
	 public List<Notification> findNotificationsByUserId(long userId);
	 
	 
	 @Query(nativeQuery=true, value="select notifications.notification_id, is_read, first_text, date_created, has_approve "
		 		+ " from notifications "
		 		+ " left outer join person_notifications ON person_notifications.notification_id = notifications.notification_id "
		 		+ " left outer join user_accounts ON user_accounts.user_id = person_notifications.person_id "
		 		+ " WHERE person_notifications.person_id = ?1 "
		 		+ " ORDER BY notifications.date_created DESC;") 
		 public List<Notification> reverseFindNotificationsByUserId(long userId);
	 
	 public Notification findNotificationByNotificationId(long notificationId);
	 
	 
	// public Slice<Notification> findByUserId(long userId, org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable pageable);
	 
}
