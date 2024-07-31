package com.github.dscommerce.services;

import com.github.dscommerce.dto.ProductDTO;
import com.github.dscommerce.entities.Product;
import com.github.dscommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
        Product product = productRepository.findById(id).get();
        return modelMapper.map(product, ProductDTO.class);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(x -> modelMapper.map(x, ProductDTO.class));
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product product = productRepository.save(modelMapper.map(dto, Product.class));
        return modelMapper.map(product, ProductDTO.class);
    }

}
