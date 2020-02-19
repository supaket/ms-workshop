package com.metamagic.ms.service.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.metamagic.ms.dto.UserDTO;
import com.metamagic.ms.entity.User;
import com.metamagic.ms.events.integration.UserCreatedEvent;
import com.metamagic.ms.exception.BussinessException;
import com.metamagic.ms.exception.IllegalArgumentCustomException;
import com.metamagic.ms.exception.RepositoryException;
import com.metamagic.ms.repository.read.UserReadRepository;
import com.metamagic.ms.repository.write.UserWriteRepository;

/**
 * @author sagar
 *
 *         THIS SERVICE IS USED FOR WRITE OPERATION OF USERS
 */
@Service
public class UserWriteServiceImpl implements UserWriteService {

	@Autowired
	private UserWriteRepository userWriteRepository;

	@Autowired
	private UserReadRepository userReadRepository;

	@Autowired
	private KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

	/**
	 * THIS METHOD IS USED TO CREATE USER
	 * 
	 * @throws RepositoryException
	 * @throws BussinessException
	 * 
	 */
	public void createUser(UserDTO userDTO) throws RepositoryException, BussinessException {

		User userStored = userReadRepository.findByUserId(userDTO.getUserId());
		if (userStored == null) {
			User user;
			try {
				user = new User(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getUserId(),
						userDTO.getPassword());
			} catch (IllegalArgumentCustomException e) {
				throw new BussinessException(e.getMessage());
			}
			userWriteRepository.save(user);
			UserCreatedEvent createdEvent = new UserCreatedEvent(user.getId());
			this.onUserCreateEvent(createdEvent);
		} else {
			throw new BussinessException("User with same userId is present, please choose other");
		}
	}

	/**
	 * THIS METHOD IS USED FOR CREATE USER EVENT
	 */
	private void onUserCreateEvent(UserCreatedEvent createdEvent) {
		Message<UserCreatedEvent> message = MessageBuilder.withPayload(createdEvent)
				.setHeader(KafkaHeaders.TOPIC, "user_created").setHeader("custom-header", createdEvent.getUserId()).build();
		kafkaTemplate.send(message);
	}
}
