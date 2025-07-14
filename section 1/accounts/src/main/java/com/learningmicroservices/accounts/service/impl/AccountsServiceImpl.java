package com.learningmicroservices.accounts.service.impl;

import com.learningmicroservices.accounts.Repository.AccountsRepository;
import com.learningmicroservices.accounts.Repository.CustomerRepository;
import com.learningmicroservices.accounts.constants.AccountsConstants;
import com.learningmicroservices.accounts.dto.*;
import com.learningmicroservices.accounts.entity.*;
import com.learningmicroservices.accounts.exceptions.*;
import com.learningmicroservices.accounts.mapper.*;
import com.learningmicroservices.accounts.service.IAccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service                        // ①  ➜ registers a Spring bean
@RequiredArgsConstructor        // ②  ➜ constructor‑based injection
@Transactional                  // ③  ➜ optional: wraps methods in a tx
public class AccountsServiceImpl implements IAccountsService {

    private final AccountsRepository  accountsRepository;
    private final CustomerRepository  customerRepository;

    /* ── CREATE ─────────────────────────────────────── */
    @Override
    public void createAccount(CustomerDto customerDto) {
        Optional<Customer> existing =
                customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if (existing.isPresent()) {
            throw new CustomerAlreadyExistsException(
                    "Customer already registered with mobile number " +
                            customerDto.getMobileNumber());
        }

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Customer saved    = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(saved));
    }

    private Accounts createNewAccount(Customer customer) {
        Accounts acc = new Accounts();
        acc.setCustomerId(customer.getCustomerId());
        acc.setAccountNumber(1000000000L + new Random().nextInt(900000000));
        acc.setAccountType(AccountsConstants.SAVINGS);
        acc.setBranchAddress(AccountsConstants.ADDRESS);
        return acc;
    }

    /* ── READ ───────────────────────────────────────── */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account", "customerId",
                                customer.getCustomerId().toString()));

        CustomerDto dto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        dto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        return dto;
    }

    /* ── UPDATE ─────────────────────────────────────── */
    @Override
    public boolean updateAccount(CustomerDto dto) {
        AccountsDto accDto = dto.getAccountsDto();
        if (accDto == null) return false;

        Accounts acc = accountsRepository.findById(accDto.getAccountNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account", "accountNumber",
                                accDto.getAccountNumber().toString()));
        AccountsMapper.mapToAccounts(accDto, acc);
        accountsRepository.save(acc);

        Customer cust = customerRepository.findById(acc.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", "customerId",
                                acc.getCustomerId().toString()));
        CustomerMapper.mapToCustomer(dto, cust);
        customerRepository.save(cust);
        return true;
    }

    /* ── DELETE ─────────────────────────────────────── */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }
}
