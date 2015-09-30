package org.qzerver.model.agent.action.providers.marshaller;

import com.gainmatrix.lib.time.ChronometerUtils;
import junit.framework.Assert;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionResult;

import javax.annotation.Resource;

import java.util.Date;

public class ActionResultMarshallerTest extends AbstractModelTest {

    @Resource
    private ActionResultMarshaller actionResultMarshaller;

    @Test
    public void testMarshalling() throws Exception {
        StubActionResult result = new StubActionResult();
        result.setValue1("test1");
        result.setValue2(13445);
        result.setValue3(ChronometerUtils.parseMoment("2010-02-12 12:32:10.000 UTC"));

        byte[] data = actionResultMarshaller.marshall(result);
        Assert.assertNotNull(data);
        Assert.assertTrue(data.length > 0);

        StubActionResult resultLoaded =
            (StubActionResult) actionResultMarshaller.unmarshall(StubActionResult.class, data);
        Assert.assertNotNull(resultLoaded);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(result, resultLoaded));
    }

    private static class StubActionResult implements ActionResult {

        private String value1;

        private long value2;

        private Date value3;

        @Override
        public boolean isSucceed() {
            return true;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public long getValue2() {
            return value2;
        }

        public void setValue2(long value2) {
            this.value2 = value2;
        }

        public Date getValue3() {
            return value3;
        }

        public void setValue3(Date value3) {
            this.value3 = value3;
        }
    }

}
