package de.schwarz.libraryapp.auth.service;

import de.schwarz.libraryapp.customer.domain.CustomerRepository;
import de.schwarz.libraryapp.customer.domain.dto.CustomerDto;
import de.schwarz.libraryapp.customer.domain.entity.Customer;
import de.schwarz.libraryapp.exception.NoContentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import static de.schwarz.libraryapp.customer.service.CustomerService.ERROR_CUSTOMERS_USERNAME;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final CustomerRepository customerRepository;


    /**
     * @param email
     * @return
     */
    @Transactional
    public CustomerDto findByEmail(final String email) {
        try {
            var customer = customerRepository.findByUsername(email.trim());
            if (customer.isEmpty()) {
                throw new NoContentException();
            }

            return createCustomerDto(customer.get());
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting customers by email: {}", email, e);
            throw new InternalError(ERROR_CUSTOMERS_USERNAME);
        }
    }

    /**
     * Creates from entity customer the dto.
     *
     * @param customer
     * @return
     */
    protected CustomerDto createCustomerDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerId(customer.getId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setPassword(customer.getPassword());

        return customerDto;
    }

    /**
     * For testing purposes refactored.
     */
    protected void rollback() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
