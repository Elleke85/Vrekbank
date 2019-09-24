package webproject.vrekbank_applicatie.controller;

import org.springframework.web.bind.annotation.*;
import webproject.vrekbank_applicatie.model.BusinessAccount;
import webproject.vrekbank_applicatie.model.PinMachine;
import webproject.vrekbank_applicatie.service.BusinessAccountValidator;
import webproject.vrekbank_applicatie.service.PinMachineService;

@RestController
public class PinController {

    BusinessAccountValidator businessAccountValidator;

    PinMachineService pinMachineService;

    @GetMapping(value = "/businessAccount/{dailyConnectIdentifier}")
    public String getAttachedMKBAccount(@PathVariable int dailyConnectIdentifier) {
        BusinessAccount shopholdersAccount = businessAccountValidator.findByPinMachine
                (pinMachineService.findByDailyConnectIdentifier(dailyConnectIdentifier));
        String json = businessAccountValidator.serialize(shopholdersAccount);
        return json;
    }

//        @GetMapping(value = "/members/new")
//        public String putMember(@RequestParam String json) {
//            Member member = memberService.deserialize(json);
//            return member.getName() + " OK";
//        }

    @GetMapping(value = "/businessAccount/exists/{clientPinMachine}")
    public String addPinMachine(@PathVariable String json) {
        PinMachine clientPinMachine = pinMachineService.deserialize(json);

        //controle of er een pinmachine bestaat
        if (businessAccountValidator.pinMachineExists(clientPinMachine.getBusinessAccount().getIban())) {
            //check controlegetal
            if (pinMachineService.addIdentifierIsCorrect(clientPinMachine.getAddIdentifier())) {
                //stuur daily connect identifier retour (8cijferige code)
                PinMachine pinMachineinDb = pinMachineService.findByAddIdentifier(clientPinMachine.getAddIdentifier());
                int dailyConnectIdentifier = pinMachineinDb.getDailyConnectIdentifier();
                String dailyConnectIdentifierInString = Integer.toString(dailyConnectIdentifier);
                return dailyConnectIdentifierInString;
            } else return "Helaas, addIdentifier is niet correct";
        }else return "Helaas, bij deze rekening is geen verzoek tot pin toevoegen gedaan door de bank.";
    }
}