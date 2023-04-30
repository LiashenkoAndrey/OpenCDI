package cdi.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.open.cdi.annotations.BeanScope;
import org.open.cdi.annotations.DIBean;

@NoArgsConstructor
@Setter
@Getter
@DIBean(scope = BeanScope.PROTOTYPE)
public class Address {
    private String address;
}
