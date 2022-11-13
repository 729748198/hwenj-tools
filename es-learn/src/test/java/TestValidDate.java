import com.hwenj.es.learn.ValidDate;
import org.junit.jupiter.api.Test;

/**
 * @author hwenj
 * @since 2022/10/8
 */
public class TestValidDate {

    @Test
    public void testInt(){
        String s = ValidDate.byteArrayToString(ValidDate.intToBytes(536870912));
        System.out.println(s);
    }

    @Test
    public void testDay(){
        //有效天1
        int validDay9 = ValidDate.getDayInt("2022-10-8");
        validDay9|=ValidDate.getDayInt("2022-10-9");
        System.out.println(validDay9);
    }
}
