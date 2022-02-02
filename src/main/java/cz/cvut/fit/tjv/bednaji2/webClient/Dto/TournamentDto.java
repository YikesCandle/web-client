package cz.cvut.fit.tjv.bednaji2.webClient.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentDto {
    public Long tournamentId;
    @NotBlank(message = "name is required")
    public String name;
    @NotBlank(message = "date is required")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    public String date;
    public Long winner;
}
