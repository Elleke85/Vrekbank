package webproject.vrekbank_applicatie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import webproject.vrekbank_applicatie.model.AddPinMachineRequest;
import webproject.vrekbank_applicatie.model.BusinessAccount;
import webproject.vrekbank_applicatie.service.BusinessAccountValidator;
import webproject.vrekbank_applicatie.service.PinMachineService;

@RestController
public class EmployeeOverviewController {

    @Autowired
    BusinessAccountValidator businessAccountValidator;

    @Autowired
    PinMachineService pinMachineService;

    // hier @ModelAttribute ook nodig?
    @PostMapping(value = "AddPinMachineConfirmation}")
    public String addPinMachineHandler(@ModelAttribute AddPinMachineRequest addPinMachineRequest, Model model) {
        if (businessAccountValidator.exists(addPinMachineRequest.getIban())) {
// generate pin with 5 digits

// check in db if it does not yet exists.

            //if so, redo stap 1 and 2

            // if it does not, save tot DB

            // return to confirmation screen.
            return "AddPinMachineConfirmation";
        }
        else return "AddPinMachineFailed";
    }


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
//

    @GetMapping(value = "/businessAccount/exists/{dailyConnectIdentifier}")
    public String existsBusinessAccount(@PathVariable int dailyConnectIdentifier) {
        boolean exists = pinMachineService.exists(dailyConnectIdentifier);
        if (exists) {
            return "yes";
        }
        return "no";
    }
}
