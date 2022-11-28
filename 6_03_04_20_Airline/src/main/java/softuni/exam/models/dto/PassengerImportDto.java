package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerImportDto {

    @Size(min = 2)
    private String firstName;

    @Size(min = 2)
    private String lastName;

    @Positive
    private int age;

    private String phoneNumber;

    @Email
    private String email;

    private String town;
}