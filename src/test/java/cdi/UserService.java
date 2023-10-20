package cdi;

import lombok.NoArgsConstructor;
import org.openCDI.annotations.Component;

@NoArgsConstructor
@Component
public class UserService {

    private int value;

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
