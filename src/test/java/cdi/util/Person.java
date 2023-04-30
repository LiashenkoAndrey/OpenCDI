package cdi.util;

import lombok.*;
import org.open.cdi.annotations.BeanScope;
import org.open.cdi.annotations.DIBean;
import org.open.cdi.annotations.InjectBean;

import java.util.Random;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@DIBean(value = "person", scope = BeanScope.PROTOTYPE)
public class Person {

    @InjectBean
    private Address address;

    private int age;
    private String name;
    private Long id;
}
