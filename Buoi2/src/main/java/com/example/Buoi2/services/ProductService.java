package com.example.Buoi2.services;



import com.example.Buoi2.entity.Product;
import com.example.Buoi2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Add a new product to the database
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setQuality(product.getQuality()); // Cập nhật quality
        existingProduct.setImageData(product.getImageData());
        return productRepository.save(existingProduct);
    }

    // Delete a product by its id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }
}
