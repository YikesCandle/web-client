package cz.cvut.fit.tjv.bednaji2.webClient.Dto;

import lombok.Data;

@Data
public class PersonDtoSummary {
    Long personId;
    String name;
    int numberOfAccounts;
}
