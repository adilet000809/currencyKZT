import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "rates")
@XmlAccessorType(XmlAccessType.FIELD)
public class Root implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "item")
    private List<Rate> item;

    public Root() {
    }

    public Root(List<Rate> item) {
        this.item = item;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<Rate> getItem() {
        return item;
    }

    public void setItem(List<Rate> item) {
        this.item = item;
    }

}
