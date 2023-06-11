package cdi.util;

import lombok.*;
import org.open.cdi.annotations.BeanScope;
import org.open.cdi.annotations.DIBean;

import java.util.List;
import java.util.Random;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@DIBean(scope = BeanScope.PROTOTYPE)
public class PrototypePerson {

    private int age;
    private String name;
    private Long id;


    public static PrototypePerson random() {
        List<String> names = List.of("Tom", "Liza", "Ann", "Toma", "katia", "Alina", "Bob", "Olivia");
        return PrototypePerson.builder()
                .age(new Random().nextInt(16, 50))
                .name(names.get(new Random().nextInt(0, names.size()-1)))
                .id(new Random().nextLong(1, 100000))
                .build();
    }
}
