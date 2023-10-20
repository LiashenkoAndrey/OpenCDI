package cdi;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.openCDI.annotations.Component;


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
