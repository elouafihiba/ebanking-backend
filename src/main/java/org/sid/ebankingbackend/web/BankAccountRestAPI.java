package org.sid.ebankingbackend.web;

import org.sid.ebankingbackend.dtos.BankAccountDTO;
import org.sid.ebankingbackend.exception.BankAccountNotFoundException;
import org.sid.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    public BankAccountRestAPI(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }
 @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
    return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts(){
        return bankAccountService.bankAccountList();
    }
}
