package cdi;

import lombok.*;
import org.openCDI.annotations.Component;

@NoArgsConstructor
@Setter
@Getter
@Component
@AllArgsConstructor
@EqualsAndHashCode
public class Address {


    private String address;
}
