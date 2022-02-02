package cz.cvut.fit.tjv.bednaji2.webClient.Controller;

import cz.cvut.fit.tjv.bednaji2.webClient.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequestMapping("accountList")
public class AccountController {
    @Autowired
    WebClient webClient;

    @GetMapping
    public ModelAndView getAccounts() {
        ModelAndView mav = new ModelAndView("accountList");

        Mono<AccountSummaryDto[]> response = webClient.get()
                .uri("/account")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(AccountSummaryDto[].class);
        AccountSummaryDto[] result = response.block();
        assert result != null;
        mav.addObject("accountList", Arrays.stream(result).collect(Collectors.toList()));

        return mav;
    }

    @GetMapping(value = "/checkout")
    public ModelAndView getOneAccount(@RequestParam Long accountId) {
        ModelAndView mav = new ModelAndView("checkoutAccount");

        AccountDto response = webClient.get()
                .uri("/account/" + accountId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(AccountDto.class)
                .block();
        mav.addObject("account", response);

        assert response != null;
        PersonDto owner = response.person == null || response.person.getPersonId() == null ? new PersonDto() : webClient.get()
                .uri("/person/" + response.person.getPersonId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(PersonDto.class)
                .block();

        mav.addObject("owner",owner);
        return mav;
    }

    @GetMapping("/update")
    public ModelAndView updateAccountForm(@RequestParam Long accountId) {
        ModelAndView mov = new ModelAndView("updateAccountForm");

        AccountDto originAccount = webClient.get()
                .uri("/account/" + accountId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(AccountDto.class)
                .block();
        assert originAccount != null;
        mov.addObject("newAccount", originAccount);
        if (originAccount.person == null)
            return new ModelAndView("redirect:");
        return mov;
    }

    @PostMapping("/update")
    public ModelAndView updateAccount(@RequestParam("accountId") Long accountId, @RequestParam("personId") Long personId,
                               @Valid @ModelAttribute("newAccount") AccountDto newAccount, Errors errors) {
        if (errors.hasErrors()) {
            ModelAndView mov = new ModelAndView("updateAccountForm");

            AccountDto originAccount = webClient.get()
                    .uri("/account/" + accountId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(AccountDto.class)
                    .block();
            assert originAccount != null;
            newAccount.person = originAccount.person;
            mov.addObject("newAccount", newAccount);
            return mov;
        }
        try {
            PersonDto optionalAccount = webClient.put()
                    .uri("/person/" + personId + "/account/" + accountId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(newAccount), PersonDto.class)
                    .retrieve()
                    .bodyToMono(PersonDto.class)
                    .block();
        } catch (Exception e) {
            ModelAndView mov = new ModelAndView("updateAccountForm");

            AccountDto originAccount = webClient.get()
                    .uri("/account/" + accountId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(AccountDto.class)
                    .block();
            assert originAccount != null;
            newAccount.person = originAccount.person;
            mov.addObject("newAccount", newAccount);
            return mov;
        }

        return new ModelAndView("redirect:/personList/checkout?personId=" + personId);
    }

    @GetMapping("/add")
    public ModelAndView addAccountForm(@RequestParam Long personId) {
        ModelAndView mov = new ModelAndView("newAccountForm");
        AccountDto tmpAccount = new AccountDto();
        mov.addObject("newAccount", tmpAccount);
        mov.addObject("ownerId", personId);
        return mov;
    }

    @PostMapping("/add")
    public ModelAndView addAccount(@RequestParam Long personId, @Valid @ModelAttribute("newAccount") AccountDto newAccount, Errors errors) {

        if (errors.hasErrors()){
            ModelAndView mov = new ModelAndView("newAccountForm");
            mov.addObject("newAccount", newAccount);
            mov.addObject("ownerId", personId);
            return mov;
        }
        try {
            AccountDto optionalAccount = webClient.post()
                    .uri("/person/" + personId + "/account")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(newAccount), AccountDto.class)
                    .retrieve()
                    .bodyToMono(AccountDto.class)
                    .block();
        } catch (Exception e) {
            ModelAndView mov = new ModelAndView("newAccountForm");
            mov.addObject("newAccount", newAccount);
            mov.addObject("ownerId", personId);
            return mov;
        }

        return new ModelAndView("redirect:/personList/checkout?personId=" + personId);
    }
}
