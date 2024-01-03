package de.schwarz.libraryapp.customer.domain;


import de.schwarz.libraryapp.customer.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT c.* FROM library.customer c WHERE c.email = :username", nativeQuery = true)
    Optional<Customer> findByUsername(@Param(value = "username") String username);
}