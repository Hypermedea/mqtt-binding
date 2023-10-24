package org.hypermedea.mqtt;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.hypermedea.op.BaseOperation;
import org.hypermedea.op.InvalidFormException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

public abstract class MqttOperation extends BaseOperation {

    protected final String topic;

    protected final MqttClient client;

    public MqttOperation(String resourceURI, Map<String, Object> formFields) {
        super(resourceURI, formFields);

        try {
            URI uri = new URI(target);
            topic = uri.getPath();

            URI serverURI = getServerURI(uri);

            // FIXME file persistence raises exception?
            String id = "Hypermedea-" + UUID.randomUUID();
            client = new MqttClient(serverURI.toString(), id, null);
        } catch (URISyntaxException e) {
            throw new InvalidFormException(e);
        } catch (MqttException e) {
            // TODO can be a client error or server error
            throw new RuntimeException(e);
        }
    }

    private URI getServerURI(URI mqttURI) throws URISyntaxException {
        String scheme = mqttURI.getScheme().equals("mqtts") ? "ssl" : "tcp";
        return new URI(scheme, null, mqttURI.getHost(), mqttURI.getPort(), null, null, null);
    }

}
