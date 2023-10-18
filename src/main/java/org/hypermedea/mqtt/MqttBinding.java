package org.hypermedea.mqtt;

import org.hypermedea.op.BaseProtocolBinding;
import org.hypermedea.op.Operation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * See <a href="https://github.com/mqtt/mqtt.org/wiki/URI-Scheme">Using URIs to connect to a MQTT server</a>
 * for MQTT URI schemes.
 */
public class MqttBinding extends BaseProtocolBinding {

    public static final String MQTT_PROTOCOL = "MQTT";

    public static final Collection<String> MQTT_SCHEMES = new HashSet<>();

    static {
        MQTT_SCHEMES.add("mqtt");
        MQTT_SCHEMES.add("mqtts");
    }

    @Override
    public String getProtocol() {
        return MQTT_PROTOCOL;
    }

    @Override
    public Collection<String> getSupportedSchemes() {
        return MQTT_SCHEMES;
    }

    @Override
    protected Operation bindGet(String targetURI, Map<String, Object> formFields) {
        return super.bindGet(targetURI, formFields);
    }

    @Override
    protected Operation bindWatch(String targetURI, Map<String, Object> formFields) {
        return super.bindWatch(targetURI, formFields);
    }

    @Override
    protected Operation bindPut(String targetURI, Map<String, Object> formFields) {
        return super.bindPut(targetURI, formFields);
    }

}