package cdi.util;

import lombok.NoArgsConstructor;
import org.open.cdi.annotations.DIBean;

@NoArgsConstructor
@DIBean
public class UserService {

    private int value;

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
