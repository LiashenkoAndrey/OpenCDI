package cdi.util;

import lombok.*;
import org.open.cdi.annotations.Component;

@NoArgsConstructor
@Setter
@Getter
@Component
@AllArgsConstructor
@EqualsAndHashCode
public class Address {


    private String address;
}
