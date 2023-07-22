package cdi.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.open.cdi.annotations.Component;


@Getter
@Setter
@EqualsAndHashCode
@Component("singletonPerson")
public class SingeltonPerson {

    public SingeltonPerson() {
    }

    private int age;

    private String name;

    private Long id;

}
