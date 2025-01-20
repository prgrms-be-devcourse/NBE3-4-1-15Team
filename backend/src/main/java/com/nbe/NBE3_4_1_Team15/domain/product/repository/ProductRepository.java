package com.nbe.NBE3_4_1_Team15.domain.product.repository;

import com.nbe.NBE3_4_1_Team15.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    //save, findAll, findById, deleteById(ID id), long count(), existsById(ID id)

    Optional<Product> findByName(String name);
}
