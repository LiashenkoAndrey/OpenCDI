package cdi.util;

import lombok.*;
import org.open.cdi.annotations.BeanScope;
import org.open.cdi.annotations.DIBean;
import org.open.cdi.annotations.InjectBean;

import java.util.List;
import java.util.Random;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@DIBean
@Builder
@AllArgsConstructor
public class Person {

    @InjectBean
    private Address address;

    private int age;
    private String name;
    private Long id;


    public static Person random() {
        List<String> names = List.of("Tom", "Liza", "Ann", "Toma", "katia", "Alina", "Bob", "Olivia");
        return Person.builder()
                .address(new Address())
                .age(new Random().nextInt(16, 50))
                .name(names.get(new Random().nextInt(0, names.size()-1)))
                .id(new Random().nextLong(1, 100000))
                .build();
    }
}
