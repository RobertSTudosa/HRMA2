package com.bzbees.hrma.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class SocialMedia {
	
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "socialmedia_generator")
	@SequenceGenerator(name = "social_generator", sequenceName = "socialmedia_seq", allocationSize = 1)
	private long socialMediaId;
	
	private String name;
	
	private String urlAddress;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
    private Agency agency;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
    private Person person;
	
}
