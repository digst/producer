package schema_test;

import avro.AvroSerializer;
import com.govcloud.digst.Organisation;
import org.junit.Test;



public class test {


    @Test
    public void test_schema_tostring()
    {

        System.out.println(Organisation.getClassSchema().toString());


    }

}
