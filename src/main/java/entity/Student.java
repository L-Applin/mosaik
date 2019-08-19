package entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Data @AllArgsConstructor
public class Student implements Comparable<Student>, Comparator<Student> {
    private Departement departement;
    private String firstName;
    private String lastName;
    private ChronoLocalDate graduationYear;
    private String mosaicYear;

    @Override
    public String toString() {
        return String.format("%s %s, %s %s", firstName, lastName, departement.directory, graduationYear.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
    }

    @Override
    public int compareTo(Student o) {
        return compare(this, o);
    }

    @Override
    public int compare(Student o1, Student o2) {
        return String.CASE_INSENSITIVE_ORDER.compare(o1.lastName, o2.lastName);
    }
}
