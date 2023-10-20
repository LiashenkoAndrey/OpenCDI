package cdi.inheritance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openCDI.annotations.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public  class ChildBean extends ParentBean {

//    public int i = 1;
//    public int i1 = 2;
//    private int i3 = 3;

}
