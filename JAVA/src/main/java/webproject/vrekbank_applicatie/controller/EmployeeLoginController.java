package webproject.vrekbank_applicatie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webproject.vrekbank_applicatie.model.Employee;
import webproject.vrekbank_applicatie.model.PersonalAccount;
import webproject.vrekbank_applicatie.service.EmployeeValidator;
import webproject.vrekbank_applicatie.service.PersonalAccountValidator;

import java.util.List;

@Controller
@SessionAttributes({"name", "firstName"})
public class EmployeeLoginController {

    private final String FUNCTION_HEAD_PERSONAL = "Hoofd Particulieren";
    private final String FUNCTION_HEAD_MKB = "Hoofd MKB";
    private final String FUNCTION_ACCOUNT_MANAGER = "AccountManager";

    @Autowired
    EmployeeValidator employeeValidator;

    @Autowired
    PersonalAccountValidator personalAccountValidator;

    // handler voor checken of ingegeven login gegevens kloppen
    @PostMapping(value = "employeeOverview")
    public String EmployeeOverview(@ModelAttribute Employee loggedInEmployee, Model model) {
        // find employee based on inlog-information (userName en PW)
        Employee employee = employeeValidator.findEmployeeByUserName(loggedInEmployee.getUserName());
        model.addAttribute("name", loggedInEmployee.getUserName());
        model.addAttribute("firstName", employee.getFirstName());

        // if function = Hoofd Particuliern, show 10 accounts with highest balance
        if (employee.getTypeFunction().equals(FUNCTION_HEAD_PERSONAL)) {
            List<PersonalAccount> top10 = personalAccountValidator.showTop10PersonalAccounts();
            model.addAttribute("top10", top10);
            return "EmployeeOverview";
        }


        if (employee.getTypeFunction().equals(FUNCTION_HEAD_MKB)) {
            return "MKBOverview";
        }

        if (employee.getTypeFunction().equals(FUNCTION_ACCOUNT_MANAGER)){
            return "AccountManagerOverview";
        }
        return "EmployeeOverview";
    }

    @GetMapping(value="employeeMkbWithName")
    public String employeeWithNameHandler (@SessionAttribute ("name") String employeeName, Model model) {
        Employee employee = employeeValidator.findEmployeeByUserName(employeeName);
        EmployeeOverview(employee, model);

        return "MKBOverview";
    }


}
