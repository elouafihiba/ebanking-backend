package org.sid.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.dtos.BankAccountDTO;
import org.sid.ebankingbackend.dtos.CurrentBankAccountDTO;
import org.sid.ebankingbackend.dtos.CustomerDTO;
import org.sid.ebankingbackend.dtos.SavingBankAccountDTO;
import org.sid.ebankingbackend.entities.*;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exception.BankAccountNotFoundException;
import org.sid.ebankingbackend.exception.BlanceNotSuficientException;
import org.sid.ebankingbackend.exception.CustomerNotFoundException;
import org.sid.ebankingbackend.mappers.BankAccountMapperImp;
import org.sid.ebankingbackend.repositories.AccountOpperationRepository;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j

public class BankAccountServiceImp implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOpperationRepository accountOpperationRepository;
    private BankAccountMapperImp dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatdAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatdAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDtos = customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        return customerDtos;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account not found"));
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }


    @Override
    public void debit(String accountId, double amount, String description) throws BlanceNotSuficientException, BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account not found"));
        if (bankAccount.getBalance() < amount)
            throw new BlanceNotSuficientException("Balance not sufficient");
        AccountOperation accountOpperation = new AccountOperation();
        accountOpperation.setType(OperationType.DEBIT);
        accountOpperation.setAmount(amount);
        accountOpperation.setDescription(description);
        accountOpperation.setOperationDate(new Date());
        accountOpperation.setBankAccount(bankAccount);
        accountOpperationRepository.save(accountOpperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);


    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account not found"));
        AccountOperation accountOpperation = new AccountOperation();
        accountOpperation.setType(OperationType.CREDIT);
        accountOpperation.setAmount(amount);
        accountOpperation.setDescription(description);
        accountOpperation.setOperationDate(new Date());
        accountOpperation.setBankAccount(bankAccount);
        accountOpperationRepository.save(accountOpperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BlanceNotSuficientException {

        debit(accountIdSource, amount, "Transfert");
        credit(accountIdDestination, amount, "Transefert from" + accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }

        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("update Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);

    }
}
