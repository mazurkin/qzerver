package org.qzerver.system.jackson;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.PostConstruct;

import java.util.Map;

/**
 * Custom factory bean for ObjectMapper allows to specify features via context configuration
 */
public class JacksonObjectMapperFactoryBean implements FactoryBean<ObjectMapper> {

    private ObjectMapper objectMapper;

    private Map<SerializationConfig.Feature, Boolean> serializationFeatures;

    private Map<DeserializationConfig.Feature, Boolean> deserializationFeatures;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        objectMapper = new ObjectMapper();

        // Serialization configuration
        if (MapUtils.isNotEmpty(serializationFeatures)) {
            SerializationConfig serializationConfig = objectMapper.copySerializationConfig();
            for (Map.Entry<SerializationConfig.Feature, Boolean> entry : serializationFeatures.entrySet()) {
                SerializationConfig.Feature feature = entry.getKey();
                boolean enabled = BooleanUtils.isTrue(entry.getValue());
                if (enabled) {
                    serializationConfig = serializationConfig.with(feature);
                } else {
                    serializationConfig = serializationConfig.without(feature);
                }
            }
            objectMapper.setSerializationConfig(serializationConfig);
        }

        // Deserialization configuration
        if (MapUtils.isNotEmpty(deserializationFeatures)) {
            DeserializationConfig deserializationConfig = objectMapper.copyDeserializationConfig();
            for (Map.Entry<DeserializationConfig.Feature, Boolean> entry : deserializationFeatures.entrySet()) {
                DeserializationConfig.Feature feature = entry.getKey();
                boolean enabled = BooleanUtils.isTrue(entry.getValue());
                if (enabled) {
                    deserializationConfig = deserializationConfig.with(feature);
                } else {
                    deserializationConfig = deserializationConfig.without(feature);
                }
            }
            objectMapper.setDeserializationConfig(deserializationConfig);
        }
    }

    @Override
    public ObjectMapper getObject() throws Exception {
        return objectMapper;
    }

    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setSerializationFeatures(Map<SerializationConfig.Feature, Boolean> serializationFeatures) {
        this.serializationFeatures = serializationFeatures;
    }

    public void setDeserializationFeatures(Map<DeserializationConfig.Feature, Boolean> deserializationFeatures) {
        this.deserializationFeatures = deserializationFeatures;
    }

}
