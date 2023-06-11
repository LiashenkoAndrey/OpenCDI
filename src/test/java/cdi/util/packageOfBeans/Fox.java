package cdi.util.packageOfBeans;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.open.cdi.annotations.DIBean;

@DIBean
@EqualsAndHashCode
@NoArgsConstructor
public class Fox {

    public void doSound() {
        System.out.println("Fox sound....");
    }
}
