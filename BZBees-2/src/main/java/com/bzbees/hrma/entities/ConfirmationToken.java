package com.bzbees.hrma.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="confirmationToken")
public class ConfirmationToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_generator")
	@SequenceGenerator(name = "token_generator", sequenceName = "token_seq", allocationSize = 1)
	@Column(name="token_id")
	private long tokenId;
	
	 @Column(name="confirmation_token")
	    private String confirmationToken;

	    @Temporal(TemporalType.TIMESTAMP)
	    private Date createdDate;

	    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	    @JoinColumn(nullable = false, name = "user_id")
	    private User user;

	    public ConfirmationToken() {}

		public ConfirmationToken(User user) {
			this.confirmationToken = UUID.randomUUID().toString();
			this.createdDate = createdDate;
			this.user = user;
		}

		public long getTokenId() {
			return tokenId;
		}


		public String getConfirmationToken() {
			return confirmationToken;
		}

		public void setConfirmationToken(String confirmationToken) {
			this.confirmationToken = confirmationToken;
		}

		public Date getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
		
		
	    
	    

}
