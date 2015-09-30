package org.qzerver.system.db.validation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.system.validation.Cron;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

public class CronValidatorTest  {

    private Validator validator;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNull() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron(null);

        Set<ConstraintViolation<TestBean>> constraintViolations = validator.validate(bean);
        Assert.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testGood1() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron("0 * * * * ?");

        Set<ConstraintViolation<TestBean>> constraintViolations = validator.validate(bean);
        Assert.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testGood2() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron("0,2,3 * 1 * * ?");

        Set<ConstraintViolation<TestBean>> constraintViolations = validator.validate(bean);
        Assert.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testBad1() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron("* * 1 ff * ?");

        Set<ConstraintViolation<TestBean>> constraintViolations = validator.validate(bean);
        Assert.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testBad2() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron("grrgr");

        Set<ConstraintViolation<TestBean>> constraintViolations = validator.validate(bean);
        Assert.assertEquals(1, constraintViolations.size());
    }

    public static class TestBean {

        @Cron
        private String cron;

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }
}

