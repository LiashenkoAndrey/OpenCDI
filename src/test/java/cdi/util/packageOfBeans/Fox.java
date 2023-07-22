package cdi.util.packageOfBeans;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.open.cdi.annotations.Component;

@Component
@EqualsAndHashCode
@NoArgsConstructor
public class Fox {

    public void doSound() {
        System.out.println("Fox sound....");
    }
}
