package cdi.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.open.cdi.annotations.DIBean;


@Getter
@Setter
@EqualsAndHashCode
@DIBean("singletonPerson")
public class SingeltonPerson {

    public SingeltonPerson() {
    }

    private int age;

    private String name;

    private Long id;

}
