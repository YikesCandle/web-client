package cz.cvut.fit.tjv.bednaji2.webClient.Controller;


import cz.cvut.fit.tjv.bednaji2.webClient.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("tournamentList")
public class TournamentController {
    @Autowired
    WebClient webClient;

    @GetMapping
    public ModelAndView getTournaments() {
        ModelAndView mav = new ModelAndView("tournamentList");

        Mono<TournamentDto[]> response = webClient.get()
                .uri("/tournament")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(TournamentDto[].class);
        TournamentDto[] result = response.block();
        assert result != null;
        mav.addObject("tournamentList", Arrays.stream(result).collect(Collectors.toList()));

        return mav;
    }

    @GetMapping(value = "/checkout")
    public ModelAndView checkoutTournament(@RequestParam Long tournamentId) {
        ModelAndView mav = new ModelAndView("checkoutTournament");

        TournamentDto response = webClient.get()
                .uri("/tournament/" + tournamentId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(TournamentDto.class)
                .block();
        mav.addObject("tournament", response);

        assert response != null;
        AccountDto winner = webClient.get()
                .uri("/account/" + response.winner)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(AccountDto.class)
                .block();
        mav.addObject("winner",winner);

        Mono<AccountDto[]> competitors = webClient.get()
                .uri("/tournament/" + tournamentId + "/account")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(AccountDto[].class);
        AccountDto[] result = competitors.block();
        assert result != null;
        mav.addObject("accountList", Arrays.stream(result).collect(Collectors.toList()));
        return mav;
    }
    @GetMapping("/add")
    public ModelAndView addTournamentForm() {
        ModelAndView mov = new ModelAndView("newTournamentForm");
        TournamentDto tmpTournament = new TournamentDto();
        mov.addObject("tournament", tmpTournament);

        Mono<AccountSummaryDto[]> response = webClient.get()
                .uri("/account")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(AccountSummaryDto[].class);
        AccountSummaryDto[] result = response.block();
        assert result != null;
        mov.addObject("accountList", Arrays.stream(result).collect(Collectors.toList()));

        return mov;
    }

    @PostMapping("/add")
    public ModelAndView addTournament(
                                @RequestParam Map<String, String> params,
                                @Valid @ModelAttribute("tournament") TournamentDto tournamentDto,
                                Errors errors) {
        NewTournamentDto newTournament = NewTournamentDto.getEmpty();
        newTournament.tournament = tournamentDto;
        for (String id : params.keySet()) {
            if (id.equals("winner")) {
                Long winnerId = Long.parseLong(params.get(id));
                if (!newTournament.competitors.contains(winnerId))
                    newTournament.competitors.add(winnerId);
                newTournament.tournament.setWinner(winnerId);
            }
            else if (params.get(id).equals("c")) {
                Long competitorId = Long.parseLong(id);
                if (!newTournament.competitors.contains(competitorId))
                    newTournament.competitors.add(competitorId);
            }
        }

        boolean isDateOk = false;
        try {
            isDateOk = LocalDate.parse(tournamentDto.date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now());
        } catch (Exception ignored){};
        if (errors.hasErrors() || !isDateOk ||
                newTournament.tournament.winner == null || newTournament.competitors.size() < 2) {
            ModelAndView mov = new ModelAndView("newTournamentForm");
            mov.addObject("tournament", tournamentDto);

            Mono<AccountSummaryDto[]> response = webClient.get()
                    .uri("/account")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(AccountSummaryDto[].class);
            AccountSummaryDto[] result = response.block();
            assert result != null;
            mov.addObject("accountList", Arrays.stream(result).collect(Collectors.toList()));

            return mov;
        }

        TournamentDto optionalTournament = webClient.post()
                .uri("/tournament")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(newTournament), NewTournamentDto.class)
                .retrieve()
                .bodyToMono(TournamentDto.class)
                .block();

        return new ModelAndView("redirect:");
    }
}
