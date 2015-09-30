package org.qzerver.base;

import com.gainmatrix.lib.time.impl.StubChronometer;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Abstract non-transactional test with context
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = {
                "classpath:/org/qzerver/resources/configuration/model/context/root.xml",
                "classpath:/test/context/test-model.xml"
        }
)
public abstract class AbstractModelTest {

    @Resource
    private StubChronometer chronometer;

    @Before
    public void setUpModelTest() throws Exception {
        chronometer.now();
    }

}
