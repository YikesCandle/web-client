package cz.cvut.fit.tjv.bednaji2.webClient.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDto {
    public Long personId;
    public String name;
    public String gender;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    public String dayOfBirth;
    public Integer age;
    public Integer numberOfAccounts;
    public Address address;

    @JsonIgnore
    public String getStreet() {
        return address == null ? null : address.getStreet();
    }
    @JsonIgnore
    public String getCity() {
        return address == null ? null : address.getCity();
    }
    @JsonIgnore
    public String getPostalCode() {
        return address == null ? null : address.getPostalCode();
    }
    @JsonIgnore
    public String getPhoneNumber() {
        return address == null ? null : address.getPhoneNumber();
    }

    public PersonAddressDto toPersonAddressDto() {
        return PersonAddressDto.builder()
                .personId(personId)
                .name(name)
                .gender(gender)
                .age(age)
                .street(getStreet())
                .city(getCity())
                .postalCode(getPostalCode())
                .phoneNumber(getPhoneNumber())
                .numberOfAccounts(numberOfAccounts)
                .dayOfBirth(dayOfBirth)
                .build();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Address {
    public String street;
    public String city;
    public String postalCode;
    public String phoneNumber;

    public String getStreet() {
        if (street == null) return null;
        return street.isBlank() ? null : street;
    }

    public String getCity() {
        if (city == null) return null;
        return city.isBlank() ? null : city;
    }

    public String getPostalCode() {
        if (postalCode == null) return null;
        return postalCode.isBlank() ? null : postalCode;
    }

    public String getPhoneNumber() {
        if (phoneNumber == null) return null;
        return phoneNumber.isBlank() ? null : phoneNumber;
    }
}
