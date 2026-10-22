package com.example.day2.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    // Makes step 3 observable in the test: did changeEmail() catch the exception itself?
    private volatile boolean lastCallCaughtInside;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    /** Step 1: change the name of a customer, WITHOUT calling save(). */
    @Transactional
    public void rename(Long id, String newName) {
        Customer customer = repository.findById(id).orElseThrow();
        customer.setName(newName);
    }

    /** Step 2: this method has no @Transactional, and the test that calls it has none either. */
    public Customer load(Long id) {
        return repository.findById(id).orElseThrow();
    }

    /** Step 3: give a customer a new email, inside the try block. */
    @Transactional
    public void changeEmail(Long id, String email) {
        lastCallCaughtInside = false;
        try {
            Customer customer = repository.findById(id).orElseThrow();
            customer.setEmail(email);
            repository.flush();   // send the UPDATE now, so a constraint violation surfaces inside the try
        } catch (DataIntegrityViolationException e) {
            lastCallCaughtInside = true;
            System.out.println("[persistence] step 3: the exception was caught INSIDE changeEmail()");
        }
    }

    public boolean lastCallCaughtInside() {
        return lastCallCaughtInside;
    }
}
