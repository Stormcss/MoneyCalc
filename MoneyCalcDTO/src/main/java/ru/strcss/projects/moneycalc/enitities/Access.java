package ru.strcss.projects.moneycalc.enitities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"Access\"")
public class Access {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String login;
    private String password;
    private String email;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login == null || login.isEmpty()) reasons.add("login is empty");
        if (password == null || password.isEmpty()) reasons.add("password is empty");
        if (email == null  || email.isEmpty()) reasons.add("email is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
