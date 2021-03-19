package com.bzbees.hrma.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.MessageRepository;
import com.bzbees.hrma.entities.Message;

@Service
public class MessageService {
	
	@Autowired
	private MessageRepository messageRepo;
	
	public List<Message> findMessagesByNotificationId(long notifId) {
		return  messageRepo.findMessageByNotificationId(notifId);
	}
	
	public List<Message> getMessagesById(long messageid) {
		return (List<Message>) messageRepo.findMessageByMessageId(messageid);
		 
	}
	
	public void messageSave(Message message) {
		messageRepo.save(message);
	}

}
