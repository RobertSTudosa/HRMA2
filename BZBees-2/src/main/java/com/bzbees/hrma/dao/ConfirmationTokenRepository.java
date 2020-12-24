package com.bzbees.hrma.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.ConfirmationToken;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository <ConfirmationToken, Long>  {
	
	public ConfirmationToken findByConfirmationToken(String confirmationToken);

}
