package entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;

@Data @AllArgsConstructor
public class Student {
    private Departement departement;
    private String firstName;
    private String lastName;
    private ChronoLocalDate graduationYear;
    private String mosaicYear;

    @Override
    public String toString() {
        return String.format("%s %s, %s %s", firstName, lastName, departement.directory, graduationYear.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
    }
}
