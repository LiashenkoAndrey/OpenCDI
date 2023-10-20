package cdi;

import lombok.*;
import org.openCDI.annotations.Scope;
import org.openCDI.annotations.Component;

import java.util.List;
import java.util.Random;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@Component(scope = Scope.PROTOTYPE)
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
