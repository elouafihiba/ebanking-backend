package org.sid.ebankingbackend.services;

import org.sid.ebankingbackend.dtos.BankAccountDTO;
import org.sid.ebankingbackend.dtos.CurrentBankAccountDTO;
import org.sid.ebankingbackend.dtos.CustomerDTO;
import org.sid.ebankingbackend.dtos.SavingBankAccountDTO;
import org.sid.ebankingbackend.entities.BankAccount;
import org.sid.ebankingbackend.entities.CurrentAccount;
import org.sid.ebankingbackend.entities.Customer;
import org.sid.ebankingbackend.entities.SavingAccount;
import org.sid.ebankingbackend.exception.BankAccountNotFoundException;
import org.sid.ebankingbackend.exception.BlanceNotSuficientException;
import org.sid.ebankingbackend.exception.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId,double amount,String description) throws BlanceNotSuficientException, BankAccountNotFoundException;
    void credit(String accountId,double amount,String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource,String accountIdDestination,double amount) throws BankAccountNotFoundException, BlanceNotSuficientException;
    List<BankAccountDTO> bankAccountList();
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);
}
