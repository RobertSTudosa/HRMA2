package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Message;
import com.bzbees.hrma.entities.Notification;

@Repository
public interface MessageRepository  extends JpaRepository<Message, Long>{

	 @Query(nativeQuery=true, value="select messages.message_id, message, notification_notification_id from messages "
		 		+ " WHERE notification_notification_id = ?1 ;") 
		 public List<Message> findMessageByNotificationId(long notifId);
	 
	 public Message findMessageByMessageId(long messageId);
	
}
