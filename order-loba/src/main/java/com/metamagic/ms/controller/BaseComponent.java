package com.metamagic.ms.controller;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @author sagar
 * THIS CLASS USED FOR COMMON METHODS OF REST CONTROLLER
 */
public abstract class BaseComponent {

	/**
	 * HERE HTTPHEADERS SET CONTENT TYPE
	 * */
	protected final HttpHeaders createHeaders(HttpServletRequest request) {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			headers.add(key, value);
		}

		return headers;
	}
}