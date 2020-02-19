package com.metamagic.ms.service.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metamagic.ms.entity.Product;
import com.metamagic.ms.exception.RepositoryException;
import com.metamagic.ms.repo.write.ProductWriteRepository;

/**
 * @author sagar
 * 
 * THIS SERVICE IS USED FOR WRITE PRODUCT OPERATION
 */
@Service
public class ProductWriteServiceImpl implements ProductWriteService {

	@Autowired
	ProductWriteRepository productRepository;

	/**
	 * THIS METHOD IS USED FOR SAVE PRODUCT
	 * @throws RepositoryException 
	 */
	public void save(Product product) throws RepositoryException {
		productRepository.save(product);
	}
}
