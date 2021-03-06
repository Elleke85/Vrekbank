package webproject.vrekbank_applicatie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webproject.vrekbank_applicatie.model.*;
import webproject.vrekbank_applicatie.model.dao.AccountDao;
import webproject.vrekbank_applicatie.model.dao.AccountHolderConfirmationDataDao;
import webproject.vrekbank_applicatie.model.dao.BusinessAccountDao;
import webproject.vrekbank_applicatie.model.dao.CustomerDao;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountValidator {

    @Autowired
    AccountDao accountDao;

    @Autowired
    BusinessAccountDao businessAccountDao;

    @Autowired
    AccountHolderConfirmationDataDao accountHolderConfirmationDataDao;

    @Autowired
    CustomerValidator customerValidator;

    public AccountValidator() {
        super();
    }

    // methods to add, check, update, delete accounts

    public void saveAccount(Account account) {
        accountDao.save(account);
    }

    public Account findByIban(String iban) {
        return accountDao.findByIban(iban);
    }
    // Voor betaler

    // in volgende twee functies zit nog dubbele code
    public boolean debitDeductionIsAllowed(Transfer transfer) {
        //voorwaarde voldoende op rekening checken
        //1 Rekening betaler ophalen uit database
        Account payingAccount = accountDao.findByIban(transfer.getDebitIban());
        // 2 Huidige balans van betaler uitlezen
        double balance = payingAccount.getBalance();
        // 3 Nieuw saldo uitrekenen
        double newBalance = balance - transfer.getTransferAmount();
        if (newBalance >= payingAccount.getMinimumBalance()) {
            return true;
        } else return false;
    }

    public Account prepareDeduction(Transfer transfer) {
        //1 Rekening betaler ophalen uit database
        Account payingAccount = accountDao.findByIban(transfer.getDebitIban());
        // 2 Huidige balans van betaler uitlezen
        double balance = payingAccount.getBalance();
        // 3 Nieuw saldo uitrekenen
        double newBalance = balance - transfer.getTransferAmount();
        // 4 nieuw saldo in object zetten
        payingAccount.setBalance(newBalance);
        //5 geeft object met beoogd nieuw saldo terug
        return payingAccount;
    }

    public void updateDebitBalance(Transfer transfer) {
        accountDao.save(prepareDeduction(transfer));
    }

    // Voor ontvanger

    public boolean creditIbanDoesExist(String iban) {
        if (accountDao.findByIban(iban) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean receiverNameIsCorrect(Transfer transfer, Recipient recipient) {
        if (!creditIbanDoesExist(transfer.getCreditIban())) {
            return false;
        } else if (!accountDao.findByIban(transfer.getCreditIban()).isBusinessAccount() &&
                accountDao.findByIban(transfer.getCreditIban()).getOwner().getLastName().equals(recipient.getPersonalName())) {
            return true;
        } else if (businessAccountDao.findByIban(transfer.getCreditIban()).getCompanyName().equals(recipient.getCompanyName())) {
            return true;
        } else {
            return false;
        }
    }

    public Account prepareAddition(Transfer transfer) {
        // 1. Rekening ontvanger ophalen
        Account receivingAccount = accountDao.findByIban(transfer.getCreditIban());
        // 2. Hoeveel geld op rekening ontvanger bepalen
        double balance = receivingAccount.getBalance();
        // 3. Nieuw saldo = oud saldo + bijschrijving
        double newBalance = balance + transfer.getTransferAmount();
        // 4. Muteer het object en geef nieuwe versie terug
        receivingAccount.setBalance(newBalance);
        return receivingAccount;
    }

    public void updateCreditBalance(Transfer transfer) {
        accountDao.save(prepareAddition(transfer));
    }


    public List<Customer> findAllAccountHoldersByIban(String iban) {
        Account account = accountDao.findByIban(iban);
        List<Customer> accountHolders = account.getAccountHolders();
        return accountHolders;
    }

    public void saveAccountHolderConfirmationData(String nameNewAccountHolder, String iban,int securityCode){
        AccountHolderConfirmationData confirmationData = new AccountHolderConfirmationData();
        confirmationData.setAccountHolderName(nameNewAccountHolder);
        confirmationData.setAccountIban(iban);
        confirmationData.setSecurityCode(securityCode);
        accountHolderConfirmationDataDao.save(confirmationData);
    }

    public List<Account> findAccountsWhereCustomerIsAccountHolder(Customer customer) {
        List<Account> accounts = new ArrayList<>();
        accounts = accountDao.findAccountsByAccountHolders(customer);
        return accounts;
    }

    public boolean correctAccountHolderConfirmationData(String iban, String firstName, int securityCode) {
        AccountHolderConfirmationData confirmationData = new AccountHolderConfirmationData();
        confirmationData = accountHolderConfirmationDataDao.getAccountHolderConfirmationDataByAccountIban(iban);
        if (confirmationData.getAccountHolderName().equals(firstName)) {
            if (confirmationData.getSecurityCode() == securityCode) {
                return true;
            }
        }
        return false;
    }

    public void addAccountHolderToAccount (String iban, String name) {
        Account account = accountDao.findByIban(iban);
        Customer customer = customerValidator.findCustomerByUsername(name);
        account.addAccountHolder(customer);
    }

    public void deleteAccountHolderConfirmationData (String iban) {
        AccountHolderConfirmationData confirmationData = accountHolderConfirmationDataDao.getAccountHolderConfirmationDataByAccountIban(iban);
        accountHolderConfirmationDataDao.delete(confirmationData);
    }
}