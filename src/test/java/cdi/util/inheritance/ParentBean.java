package cdi.util.inheritance;

import cdi.util.Person;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.open.cdi.annotations.DIBean;
import org.open.cdi.annotations.InjectBean;

@DIBean
@Getter
@Setter
@NoArgsConstructor
public class ParentBean {

    @InjectBean
    public Person person;
}
