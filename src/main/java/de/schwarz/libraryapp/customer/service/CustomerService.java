package de.schwarz.libraryapp.customer.service;


import de.schwarz.libraryapp.customer.domain.CustomerRepository;
import de.schwarz.libraryapp.customer.domain.dto.CustomerDto;
import de.schwarz.libraryapp.customer.domain.dto.CustomerRequest;
import de.schwarz.libraryapp.customer.domain.entity.Customer;
import de.schwarz.libraryapp.exception.NoContentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {

    public static final String ERROR_CUSTOMERS_ALL = "error.customers.all";
    public static final String ERROR_CUSTOMERS_USERNAME = "error.customers.username";
    public static final String ERROR_CUSTOMERS_ID = "error.customers.id";
    public static final String ERROR_CUSTOMERS_SAVE = "error.customers.save";
    public static final String ERROR_CUSTOMER_USERNAME_EMPTY = "error.customer.username.empty";
    public static final String ERROR_CUSTOMER_USERNAME_INVALID = "error.customer.username.invalid";
    public static final String ERROR_CUSTOMER_ID_EMPTY = "error.customer.id.empty";
    public static final String ERROR_CUSTOMER_REQUEST_EMPTY = "error.customer.request.empty";
    public static final String ERROR_CUSTOMER_REQUEST_NAME_EMPTY = "error.customer.request.name.empty";
    public static final String ERROR_CUSTOMER_REQUEST_USERNAME_EMPTY = "error.customer.request.username.empty";
    public static final String ERROR_CUSTOMER_REQUEST_USERNAME_INVALID = "error.customer.request.username.invalid";
    public static final String ERROR_CUSTOMER_REQUEST_PASSWORD_EMPTY = "error.customer.request.password.empty";


    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value(value = "${application.properties.email.regex}")
    private String emailRegex;


    /**
     * Validates request param email
     *
     * @param email
     */
    public void validateRequestParamEmail(final String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException(ERROR_CUSTOMER_USERNAME_EMPTY);
        }

        if (!emailValid(email)) {
            throw new IllegalArgumentException(ERROR_CUSTOMER_USERNAME_INVALID);
        }
    }

    /**
     * Validates request param customer id.
     *
     * @param customerId
     */
    public void validateRequestParamCustomerId(Long customerId) {
        if (ObjectUtils.isEmpty(customerId)) {
            throw new IllegalArgumentException(ERROR_CUSTOMER_ID_EMPTY);
        }
    }

    /**
     * Validates request params
     *
     * @param request
     */
    public void validateRequestParams(final CustomerRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            throw new IllegalArgumentException(ERROR_CUSTOMER_REQUEST_EMPTY);
        }

        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException(ERROR_CUSTOMER_REQUEST_NAME_EMPTY);
        }

        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException(ERROR_CUSTOMER_REQUEST_USERNAME_EMPTY);
        }

        if (!emailValid(request.getEmail())) {
            throw new IllegalArgumentException(ERROR_CUSTOMER_REQUEST_USERNAME_INVALID);
        }

        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException(ERROR_CUSTOMER_REQUEST_PASSWORD_EMPTY);
        }
    }

    /**
     * Detects all customers by the given author
     *
     * @return
     */
    @Transactional
    public List<CustomerDto> detectAllCustomers() {
        try {
            var customers = customerRepository.findAll();
            if (customers.isEmpty()) {
                throw new NoContentException();
            }

            return customers.stream()
                    .map(this::createCustomerDto)
                    .toList();
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting all customers...", e);
            throw new InternalError(ERROR_CUSTOMERS_ALL);
        }
    }


    /**
     * Detects all customers by the given email
     *
     * @param email
     * @return
     */
    @Transactional
    public CustomerDto detectCustomerByEmail(String email) {
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
     * Detects a customer by the given customer id.
     *
     * @param customerId
     * @return
     */
    @Transactional
    public CustomerDto detectCustomer(Long customerId) {
        try {
            var customer = customerRepository.findById(customerId);
            if (customer.isEmpty()) {
                throw new NoContentException();
            }

            return createCustomerDto(customer.get());
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting customer by customer id: {}", customerId, e);
            throw new InternalError(ERROR_CUSTOMERS_ID);
        }
    }

    /**
     * Creates or updates a customer in online library
     *
     * @param customerRequest
     * @return
     */
    @Transactional
    public CustomerDto createOrUpdateCustomer(CustomerRequest customerRequest) {
        try {
            Customer customer = createCustomerEntityFromRequest(customerRequest);
            var customerNew = customerRepository.save(customer);

            return createCustomerDto(customerNew);
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during creating customer for the name: {}", customerRequest.getName(), e);
            throw new InternalError(ERROR_CUSTOMERS_SAVE);
        }
    }

    /**
     * Removes a customer for the given customer id.
     *
     * @param customerId
     * @return
     */
    @Transactional
    public void removeCustomer(Long customerId) {
        try {
            customerRepository.deleteById(customerId);
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during removing customer for the customer id: {}", customerId, e);
            throw new InternalError(ERROR_CUSTOMERS_ID);
        }
    }

    /**
     * Validates per regex email address.
     *
     * @param email
     * @return
     */
    protected Boolean emailValid(final String email) {
        return Pattern.compile(emailRegex)
                .matcher(email)
                .matches();
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
     * Creates an entity from request.
     *
     * @param customerRequest
     * @return
     */
    protected Customer createCustomerEntityFromRequest(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        customer.setId(customerRequest.getCustomerId());
        customer.setName(customerRequest.getName());
        customer.setEmail(customerRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(customerRequest.getPassword()));
        customer.setUpdatedOn(!ObjectUtils.isEmpty(customer.getId()) ? LocalDateTime.now() : null);

        return customer;
    }

    /**
     * For testing purposes refactored.
     */
    protected void rollback() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
