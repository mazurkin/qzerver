package org.qzerver.model.agent.action.providers.marshaller;

import com.gainmatrix.lib.time.ChronometerUtils;
import junit.framework.Assert;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.annotation.Resource;

import java.util.Date;

public class ActionDefinitionMarshallerTest extends AbstractModelTest {

    @Resource
    private ActionDefinitionMarshaller actionDefinitionMarshaller;

    @Test
    public void testMarshalling() throws Exception {
        StubActionDefinition definition = new StubActionDefinition();
        definition.setValue1("test1");
        definition.setValue2(13445);
        definition.setValue3(ChronometerUtils.parseMoment("2010-02-12 12:32:10.000 UTC"));

        byte[] data = actionDefinitionMarshaller.marshall(definition);
        Assert.assertNotNull(data);
        Assert.assertTrue(data.length > 0);

        StubActionDefinition definitionLoaded =
            (StubActionDefinition) actionDefinitionMarshaller.unmarshall(StubActionDefinition.class, data);
        Assert.assertNotNull(definitionLoaded);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(definition, definitionLoaded));
    }

    private static class StubActionDefinition implements ActionDefinition {

        private String value1;

        private long value2;

        private Date value3;

        @Override
        public ActionIdentifier getIdentifier() {
            return ActionIdentifier.HTTP;
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
