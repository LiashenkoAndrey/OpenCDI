package cdi.inheritance;

import cdi.Person;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openCDI.annotations.Component;
import org.openCDI.annotations.Inject;

@Component
@Getter
@Setter
@NoArgsConstructor
public  class ParentBean {

    @Inject
    public Person person;
}
