package cz.cvut.fit.tjv.bednaji2.webClient.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewTournamentDto {
    public TournamentDto tournament;
    public List<Long> competitors;

    public static NewTournamentDto getEmpty() {
        return NewTournamentDto.builder()
                .tournament(new TournamentDto())
                .competitors(new ArrayList<>())
                .build();
    }
}
