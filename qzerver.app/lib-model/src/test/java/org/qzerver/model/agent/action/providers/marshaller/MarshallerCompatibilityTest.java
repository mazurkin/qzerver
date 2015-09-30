package org.qzerver.model.agent.action.providers.marshaller;

import com.gainmatrix.lib.time.ChronometerUtils;
import junit.framework.Assert;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;

import javax.annotation.Resource;

import java.util.Date;

public class MarshallerCompatibilityTest extends AbstractModelTest {

    @Resource
    private ObjectMapper objectMapper;

    @Test
    public void testUpgrade() throws Exception {
        MockBeanV1 bean1 = new MockBeanV1();
        bean1.setValue1("12345");
        bean1.setValue2(12345);
        bean1.setValue3(ChronometerUtils.parseMoment("2012-02-12 12:11:21.000 MSK"));
        bean1.setValue4(3.2);
        bean1.setValue5(MockBeanEnum.VALUE1);

        byte[] data = objectMapper.writeValueAsBytes(bean1);

        String dataText = new String(data, "UTF-8");
        Assert.assertNotNull(dataText);

        MockBeanV1 bean1Loaded = objectMapper.readValue(data, MockBeanV1.class);
        Assert.assertNotNull(bean1Loaded);
        Assert.assertEquals(bean1.getValue1(), bean1Loaded.getValue1());
        Assert.assertEquals(bean1.getValue2(), bean1Loaded.getValue2());
        Assert.assertEquals(bean1.getValue3(), bean1Loaded.getValue3());
        Assert.assertEquals(bean1.getValue4(), bean1Loaded.getValue4());
        Assert.assertEquals(bean1.getValue5(), bean1Loaded.getValue5());

        MockBeanV2 bean2Loaded = objectMapper.readValue(data, MockBeanV2.class);
        Assert.assertNotNull(bean2Loaded);
        Assert.assertEquals(bean1.getValue1(), bean2Loaded.getValue1());
        Assert.assertEquals(bean1.getValue2(), bean2Loaded.getValue2());
        Assert.assertEquals(bean1.getValue4(), bean2Loaded.getValue4());
        Assert.assertEquals(bean1.getValue5(), bean2Loaded.getValue5());
        Assert.assertEquals(MockBeanV2.DEFAULT_VALUE6, bean2Loaded.getValue6());
    }

    @Test
    public void testDowngrade() throws Exception {
        MockBeanV2 bean2 = new MockBeanV2();
        bean2.setValue1("12345");
        bean2.setValue2(12345);
        bean2.setValue4(4.2);
        bean2.setValue5(MockBeanEnum.VALUE1);
        bean2.setValue6(78900);

        byte[] data = objectMapper.writeValueAsBytes(bean2);

        String dataText = new String(data, "UTF-8");
        Assert.assertNotNull(dataText);

        MockBeanV2 bean2Loaded = objectMapper.readValue(data, MockBeanV2.class);
        Assert.assertNotNull(bean2Loaded);
        Assert.assertEquals(bean2.getValue1(), bean2Loaded.getValue1());
        Assert.assertEquals(bean2.getValue2(), bean2Loaded.getValue2());
        Assert.assertEquals(bean2.getValue4(), bean2Loaded.getValue4());
        Assert.assertEquals(bean2.getValue5(), bean2Loaded.getValue5());
        Assert.assertEquals(bean2.getValue6(), bean2Loaded.getValue6());

        MockBeanV1 bean1Loaded = objectMapper.readValue(data, MockBeanV1.class);
        Assert.assertNotNull(bean1Loaded);
        Assert.assertEquals(bean2.getValue1(), bean1Loaded.getValue1());
        Assert.assertEquals(bean2.getValue2(), bean1Loaded.getValue2());
        Assert.assertEquals(MockBeanV1.DEFAULT_VALUE3, bean1Loaded.getValue3());
        Assert.assertEquals(bean2.getValue4(), bean1Loaded.getValue4());
        Assert.assertEquals(bean2.getValue5(), bean1Loaded.getValue5());
    }

    private static enum MockBeanEnum {

        VALUE1, VALUE2, VALUE3;

    }

    private static class MockBeanV1 {

        public static final String DEFAULT_VALUE1 = "xxxx";

        public static final int DEFAULT_VALUE2 = 30000;

        public static final Date DEFAULT_VALUE3 = ChronometerUtils.parseMoment("2000-01-01 10:00:00.000 UTC");

        public static final double DEFAULT_VALUE4 = 1.2;

        public static final MockBeanEnum DEFAULT_VALUE5 = MockBeanEnum.VALUE2;

        private String value1;

        private int value2;

        private Date value3;

        private double value4;

        private MockBeanEnum value5;

        private MockBeanV1() {
            this.value1 = DEFAULT_VALUE1;
            this.value2 = DEFAULT_VALUE2;
            this.value3 = DEFAULT_VALUE3;
            this.value4 = DEFAULT_VALUE4;
            this.value5 = DEFAULT_VALUE5;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public int getValue2() {
            return value2;
        }

        public void setValue2(int value2) {
            this.value2 = value2;
        }

        public Date getValue3() {
            return value3;
        }

        public void setValue3(Date value3) {
            this.value3 = value3;
        }

        private String getReadOnlyValue() {
            return "12345";
        }

        public double getValue4() {
            return value4;
        }

        public void setValue4(double value4) {
            this.value4 = value4;
        }

        public MockBeanEnum getValue5() {
            return value5;
        }

        public void setValue5(MockBeanEnum value5) {
            this.value5 = value5;
        }
    }

    private static class MockBeanV2 {

        public static final String DEFAULT_VALUE1 = "zzzz";

        public static final int DEFAULT_VALUE2 = 10000;

        public static final double DEFAULT_VALUE4 = 1.4;

        public static final MockBeanEnum DEFAULT_VALUE5 = MockBeanEnum.VALUE3;

        public static final long DEFAULT_VALUE6 = 20000;

        private String value1;

        private int value2;

        private double value4;

        private MockBeanEnum value5;

        private long value6;

        private MockBeanV2() {
            this.value1 = DEFAULT_VALUE1;
            this.value2 = DEFAULT_VALUE2;
            this.value4 = DEFAULT_VALUE4;
            this.value5 = DEFAULT_VALUE5;
            this.value6 = DEFAULT_VALUE6;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public int getValue2() {
            return value2;
        }

        public void setValue2(int value2) {
            this.value2 = value2;
        }

        public long getValue6() {
            return value6;
        }

        public void setValue6(long value6) {
            this.value6 = value6;
        }

        public void setWriteOnlyValue(long value) {
            // ignore
        }

        public double getValue4() {
            return value4;
        }

        public void setValue4(double value4) {
            this.value4 = value4;
        }

        public MockBeanEnum getValue5() {
            return value5;
        }

        public void setValue5(MockBeanEnum value5) {
            this.value5 = value5;
        }
    }

}
