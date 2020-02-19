package com.metamagic.ms.service.read;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.metamagic.ms.bean.ResponseBean;
import com.metamagic.ms.controller.BaseComponent;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import ch.qos.logback.classic.Logger;

/**
 * @author sagar THIS SERVICE HANDLE REQUEST FROM CONTROLLER 1.HANDLE REQUEST
 *         AND SEND TO SERVICE 2.FALLBACK MECHANISM
 * 
 */
@Service
public class OrderReadServiceImpl extends BaseComponent implements OrderReadService {

	private static final Logger log = (Logger) LoggerFactory.getLogger(OrderReadServiceImpl.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "findAllFallBack", commandKey = "Order History", groupKey = "Order Read Service")
	public ResponseEntity<ResponseBean> findAll(HttpServletRequest request) {
		org.springframework.http.HttpHeaders headers = this.createHeaders(request);
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<ResponseBean> response = this.restTemplate.exchange(
				"http://orderservice/order/query/orderhistory", HttpMethod.GET, httpEntity, ResponseBean.class);
		return response;
	}

	@HystrixCommand(fallbackMethod = "getOrderDetailsFallBack", commandKey = "Order Details", groupKey = "Order Read Service")
	public ResponseEntity<ResponseBean> getOrderDetails(HttpServletRequest request) {
		org.springframework.http.HttpHeaders headers = this.createHeaders(request);
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<ResponseBean> response = this.restTemplate.exchange(
				"http://orderservice/order/query/getOrderDetails", HttpMethod.GET, httpEntity, ResponseBean.class);
		return response;
	}

	public ResponseEntity<ResponseBean> findAllFallBack(HttpServletRequest request, Throwable t) {
		log.error("Enable to connect to requested Order Service, please try after some time");
		ResponseBean response = new ResponseBean(false,
				"Enable to connect to requested Order Service, please try after some time", "error", t.getMessage());
		return new ResponseEntity<ResponseBean>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseBean> getOrderDetailsFallBack(HttpServletRequest request, Throwable t) {
		log.error("Enable to connect to requested Order Service, please try after some time");
		ResponseBean response = new ResponseBean(false,
				"Enable to connect to requested Order Service, please try after some time", "error", t.getMessage());
		return new ResponseEntity<ResponseBean>(response, HttpStatus.OK);
	}
}
