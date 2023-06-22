package org.sid.ebankingbackend.repositories;

import org.sid.ebankingbackend.entities.AccountOperation;
import org.sid.ebankingbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOpperationRepository extends JpaRepository<AccountOperation,Long> {
    public List<AccountOperation> findByBankAccountId(String accountId);
//3:10

}
