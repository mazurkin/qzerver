package org.qzerver.model.agent.action.providers.executor.jmx;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionPlaceholders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Validator;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JmxActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxActionExecutor.class);

    private Validator beanValidator;

    @Override
    public JmxActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        Preconditions.checkNotNull(actionDefinition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        JmxActionDefinition definition = (JmxActionDefinition) actionDefinition;

        LOGGER.debug("Execute jmx call action on node [{}]", nodeAddress);

        String effectiveUrl = definition.getUrl();
        effectiveUrl = ActionPlaceholders.substituteNode(effectiveUrl, nodeAddress);
        effectiveUrl = ActionPlaceholders.substituteExecution(effectiveUrl, scheduleExecutionId);

        try {
            JMXServiceURL jmxUrl = new JMXServiceURL(effectiveUrl);

            return processJmxUrl(jmxUrl, definition);
        } catch (Exception e) {
            LOGGER.debug("Fail to execute jmx call", e);
            return produceExceptionalResult(e);
        }
    }

    private JmxActionResult produceExceptionalResult(Exception e) {
        JmxActionResult result = new JmxActionResult();
        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());
        result.setStatus(JmxActionResultStatus.EXCEPTION);
        result.setResult(null);

        return result;
    }

    private JmxActionResult processJmxUrl(JMXServiceURL jmxUrl, JmxActionDefinition definition)
        throws Exception
    {
        Map<String, Object> environment = new HashMap<String, Object>();

        if (definition.getUsername() != null) {
            String[] credentials = new String[] {
                definition.getUsername(),
                definition.getPassword()
            };
            environment.put(JMXConnector.CREDENTIALS, credentials);
        }

        JMXConnector jmxConnector = JMXConnectorFactory.newJMXConnector(jmxUrl, environment);
        jmxConnector.connect(environment);

        try {
            return processJmxConnector(jmxConnector, definition);
        } finally {
            jmxConnector.close();
        }
    }

    private JmxActionResult processJmxConnector(JMXConnector jmxConnector, JmxActionDefinition definition)
        throws Exception
    {
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

        List<String> callParameters = definition.getParameters();
        String callBean = definition.getBean();
        String callMethod = definition.getMethod();

        ObjectName objectName = new ObjectName(callBean);

        // Find MBean
        MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
        if (mBeanInfo == null) {
            throw new IllegalArgumentException("MBean with name " + callBean + " is not found");
        }

        // Find operation
        MBeanOperationInfo mBeanOperationInfo = findMBeanOperation(mBeanInfo, definition.getMethod());
        if (mBeanOperationInfo == null) {
            throw new IllegalArgumentException("Operation [" + callMethod + "] is not found for bean " + callBean);
        }

        // Get arguments type
        MBeanParameterInfo[] mBeanParameterInfos = mBeanOperationInfo.getSignature();

        int realCount = (mBeanParameterInfos != null) ? mBeanParameterInfos.length : 0;
        int haveCount = (callParameters != null) ? callParameters.size() : 0;

        if (realCount != haveCount) {
            throw new IllegalArgumentException("Method " + callMethod + " of bean " + callBean +
                " has " + realCount + " arguments but there are " + haveCount + " values specified");
        }

        // Compose parameters
        Object[] parameterArray = null;
        String[] signatureArray = null;

        if ((callParameters != null) && (mBeanParameterInfos != null) && (realCount > 0)) {
            parameterArray = new Object[realCount];
            signatureArray = new String[realCount];

            SimpleTypeConverter typeConverter = new SimpleTypeConverter();

            for (int i = 0; i < realCount; i++) {
                String value = callParameters.get(i);

                String requiredType = mBeanParameterInfos[i].getType();
                Class<?> requiredClass = ClassUtils.forName(requiredType, this.getClass().getClassLoader());

                parameterArray[i] = typeConverter.convertIfNecessary(value, requiredClass);
                signatureArray[i] = requiredType;
            }
        }

        // Make call
        Object resultObject = mBeanServerConnection.invoke(objectName, mBeanOperationInfo.getName(),
            parameterArray, signatureArray);

        // Convert object to text
        String resultText = String.valueOf(resultObject);

        // Compose result
        JmxActionResult result = new JmxActionResult();
        result.setResult(resultText);
        result.setStatus(JmxActionResultStatus.CALLED);

        return result;
    }

    private static MBeanOperationInfo findMBeanOperation(MBeanInfo mBeanInfo, String method) {
        MBeanOperationInfo[] mBeanOperationInfos = mBeanInfo.getOperations();

        for (MBeanOperationInfo mBeanOperationInfo : mBeanOperationInfos) {
            String operationName = mBeanOperationInfo.getName();

            // Compare short names
            if (method.equals(operationName)) {
                return mBeanOperationInfo;
            }

            // Compare fully qualified names
            MBeanParameterInfo[] mBeanParameterInfos = mBeanOperationInfo.getSignature();
            if (mBeanParameterInfos == null) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            for (MBeanParameterInfo mBeanParameterInfo : mBeanParameterInfos) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(mBeanParameterInfo.getType());
            }

            String operationSignature = String.format("%s(%s)", operationName, sb.toString());
            if (method.equals(operationSignature)) {
                return mBeanOperationInfo;
            }
        }

        return null;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }
}
