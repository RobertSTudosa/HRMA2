package com.bzbees.hrma.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.ConfirmationTokenRepository;
import com.bzbees.hrma.entities.ConfirmationToken;

@Service
public class ConfirmationTokenService {
	
	@Autowired
	ConfirmationTokenRepository confTokenRepo;
	
	public ConfirmationToken findConfirmationTokenByConfirmationToken (String confirmationToken) {
		return confTokenRepo.findByConfirmationToken(confirmationToken);
	}
	
	public void saveToken (ConfirmationToken confToken) {
		confTokenRepo.save(confToken);
	}

}
