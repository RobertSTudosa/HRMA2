package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bzbees.hrma.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	
	 @Query(nativeQuery=true, value="select notification_id, message, isRead from notifications "
	 		+ " left outer join person_notifications ON person_notifications.notification_id = notifications.notification_id "
	 		+ " left outer join user_accounts ON user_accounts.user_id = person_notifications.person_id") public List<Notification>
	 findNotificationsByUserId(long userId);
	 
}
