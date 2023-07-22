package cdi.util.inheritance;

import cdi.util.Person;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.open.cdi.annotations.Component;
import org.open.cdi.annotations.Inject;

@Component
@Getter
@Setter
@NoArgsConstructor
public  class ParentBean {

    @Inject
    public Person person;
}
