package com.github.dscommerce.services;

import com.github.dscommerce.dto.ProductDTO;
import com.github.dscommerce.entities.Product;
import com.github.dscommerce.repositories.ProductRepository;
import com.github.dscommerce.services.exceptions.DatabaseException;
import com.github.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
        return modelMapper.map(product, ProductDTO.class);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(String name, Pageable pageable) {
        Page<Product> products = productRepository.searchByName(name, pageable);
        return products.map(x -> modelMapper.map(x, ProductDTO.class));
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product product = productRepository.save(modelMapper.map(dto, Product.class));
        return modelMapper.map(product, ProductDTO.class);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product product = productRepository.getReferenceById(id);
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setImgUrl(dto.getImgUrl());

            product = productRepository.save(product);

            return modelMapper.map(product, ProductDTO.class);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            productRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
        productRepository.deleteById(id);
    }
}
