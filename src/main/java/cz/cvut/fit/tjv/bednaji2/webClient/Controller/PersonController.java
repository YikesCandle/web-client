package cz.cvut.fit.tjv.bednaji2.webClient.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.bednaji2.webClient.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import javax.swing.text.DateFormatter;
import javax.validation.Valid;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@Controller
@RequestMapping(path = "personList")
public class PersonController {

    @Autowired
    WebClient webClient;

    @GetMapping
    public ModelAndView getPersons() {
        ModelAndView mav = new ModelAndView("personList");

        Mono<PersonDtoSummary[]> response = webClient.get()
                .uri("/person")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(PersonDtoSummary[].class);
        PersonDtoSummary[] result = response.block();
        assert result != null;
        mav.addObject("personList", Arrays.stream(result).collect(Collectors.toList()));

        return mav;
    }

    @GetMapping(value = "/checkout")
    public ModelAndView getOnePerson(@RequestParam Long personId) {
        ModelAndView mav = new ModelAndView("checkoutPerson");

        PersonDto response = webClient.get()
                .uri("/person/" + personId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(PersonDto.class)
                .block();
        mav.addObject("person", response);

        Mono<AccountSummaryDto[]> accountsResponse = webClient.get()
                .uri("/person/" + personId + "/account")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(AccountSummaryDto[].class);
        AccountSummaryDto[] accountsResult = accountsResponse.block();
        assert accountsResult != null;
        mav.addObject("accountList", Arrays.stream(accountsResult).collect(Collectors.toList()));

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView addPersonForm() {
        ModelAndView mov = new ModelAndView("newPersonForm");
        PersonAddressDto tmpPerson = new PersonAddressDto();
        mov.addObject("newPerson", tmpPerson);
        return mov;
    }

    @PostMapping("/add")
    public ModelAndView addPerson(@Valid @ModelAttribute("newPerson") PersonAddressDto newPerson, Errors errors) {
        boolean isDateOk = false;
        try {
            isDateOk = LocalDate.parse(newPerson.dayOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now());
        } catch (Exception ignored){};
        if (errors.hasErrors() || !isDateOk) {
            ModelAndView mov = new ModelAndView("newPersonForm");
            mov.addObject("newPerson", newPerson);
            return mov;
        }
        PersonDto body = newPerson.toPersonDto();
        PersonDto optionalPerson = webClient.post()
                .uri("/person")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(body), PersonDto.class)
                .retrieve()
                .bodyToMono(PersonDto.class)
                .block();

        return new ModelAndView("redirect:");
    }

    @GetMapping("/delete")
    public String deletePerson(@RequestParam Long personId) {
        webClient.delete().uri("/person/" + personId).retrieve().bodyToMono(Void.class).block();
        return "redirect:";
    }

    @GetMapping("/update")
    public ModelAndView updatePersonForm(@RequestParam Long personId) {
        ModelAndView mov = new ModelAndView("updatePersonForm");

        PersonDto originPerson = webClient.get()
                .uri("/person/" + personId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(PersonDto.class)
                .block();
        assert originPerson != null;
        PersonAddressDto tmpPerson = originPerson.toPersonAddressDto();
        mov.addObject("newPerson", tmpPerson);
        return mov;
    }

    @PostMapping("/update")
    public ModelAndView updatePerson(@RequestParam Long personId, @Valid @ModelAttribute("newPerson") PersonAddressDto newPerson, Errors errors) {
        boolean isDateOk = false;
        try {
            isDateOk = LocalDate.parse(newPerson.dayOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now());
        } catch (Exception ignored){};
        if (errors.hasErrors() || !isDateOk) {
            ModelAndView mov = new ModelAndView("updatePersonForm");
            mov.addObject("newPerson", newPerson);
            return mov;
        }
        PersonDto body = newPerson.toPersonDto();
        PersonDto optionalPerson = webClient.put()
                .uri("/person/" + personId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(body), PersonDto.class)
                .retrieve()
                .bodyToMono(PersonDto.class)
                .block();

        return new ModelAndView("redirect:checkout?personId=" + personId);
    }

    @GetMapping("/tournaments")
    public ModelAndView checkoutTournaments(@RequestParam Long personId) {
        ModelAndView mav = new ModelAndView("personTournamentList");

        Mono<TournamentDto[]> response = webClient.get()
                .uri("/person/" + personId + "/tournament")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(TournamentDto[].class);
        TournamentDto[] result = response.block();
        assert result != null;
        mav.addObject("tournamentList", Arrays.stream(result).collect(Collectors.toList()));
        mav.addObject("personId", personId);

        return mav;
    }
}


