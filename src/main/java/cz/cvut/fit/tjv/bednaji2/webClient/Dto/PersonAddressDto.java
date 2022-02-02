package cz.cvut.fit.tjv.bednaji2.webClient.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonAddressDto {
    public Long personId;
    @NotBlank(message = "name is required")
    @Size(max = 32, min = 3, message = "size 3-32")
    public String name;
    public String gender;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotBlank(message = "day of birth is required")
    public String dayOfBirth;
    public Integer age;
    public Integer numberOfAccounts;
    public String street;
    public String city;
    public String postalCode;
    @NotBlank(message = "phone number is required")
    @Size(min =9, max = 14, message = "size 9-14")
    public String phoneNumber;

    public PersonDto toPersonDto() {
        Address address = Address.builder()
                .city(city)
                .street(street)
                .postalCode(postalCode)
                .phoneNumber(phoneNumber)
                .build();
        return PersonDto.builder()
                .personId(personId)
                .name(name)
                .gender(gender)
                .dayOfBirth(dayOfBirth)
                .numberOfAccounts(numberOfAccounts)
                .address(address)
                .build();
    }

}
