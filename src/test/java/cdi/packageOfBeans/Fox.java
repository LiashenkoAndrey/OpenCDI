package cdi.packageOfBeans;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openCDI.annotations.Component;

@Component
@EqualsAndHashCode
@NoArgsConstructor
public class Fox {

    public void doSound() {
        System.out.println("Fox sound....");
    }
}
