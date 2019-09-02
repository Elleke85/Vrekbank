package webproject.vrekbank_applicatie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OverviewController {

    @GetMapping(value = "openaccount")
    public String overviewOpenAccountHandler() {
        return "OpenAccount";
    }

    //Tonen overzicht van recente transacties
    @GetMapping(value = "rekening/{nr}")
    public String loginOverviewHandler (@PathVariable int nr, Model model) {
        //meegeven van naam van de persoon die is ingelogd in Inlog.html
        //meegeven van de account die geselecteerd is in Overview.html
        //Check transactie history voor deze iban

        return "AccountSummary";
    }

}