package cz.cvut.fit.tjv.bednaji2.webClient.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
   public Long accountId;
   @NotBlank(message = "nickname is required")
   @Size(max = 32, min = 4, message = "size 4-32")
   public String nick;
   public String rank;
   @NotBlank(message = "email is required")
   @Size(max = 32, message = "size <32")
   public String email;
   public PersonDto person;
}
