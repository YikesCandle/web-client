package cz.cvut.fit.tjv.bednaji2.webClient.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSummaryDto {
    public String nick;
    public Long accountId;
    public String rank;
}
