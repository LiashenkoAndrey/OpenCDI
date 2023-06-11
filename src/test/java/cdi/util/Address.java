package cdi.util;

import lombok.*;
import org.open.cdi.annotations.BeanScope;
import org.open.cdi.annotations.DIBean;

@NoArgsConstructor
@Setter
@Getter
@DIBean
@AllArgsConstructor
@EqualsAndHashCode
public class Address {


    private String address;
}
