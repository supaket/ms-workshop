package com.metamagic.ms.events.integration;

import java.io.Serializable;

/**
 * @author sagar
 *
 */
public class UserCreatedEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;

	public UserCreatedEvent() {
	}

	public UserCreatedEvent(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
