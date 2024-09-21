package org.hypermedea.mqtt;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.hypermedea.ct.RepresentationHandlers;
import org.hypermedea.op.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class PublishOperation extends MqttOperation {

    private class PublishCallback implements MqttCallback {

        @Override
        public void disconnected(MqttDisconnectResponse disconnectResponse) {
            // TODO log exception
            onError();
        }

        @Override
        public void mqttErrorOccurred(MqttException exception) {
            // TODO log exception
            onError();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            // ignored
        }

        @Override
        public void deliveryComplete(IMqttToken token) {
            onResponse(new MqttResponse(PublishOperation.this, Response.ResponseStatus.OK));
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            // ignored
        }

        @Override
        public void authPacketArrived(int reasonCode, MqttProperties properties) {
            // TODO authenticate
        }

    }

    public PublishOperation(String resourceURI, Map<String, Object> formFields) {
        super(resourceURI, formFields);
    }

    @Override
    protected void sendSingleRequest() throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            RepresentationHandlers.serialize(payload, out, target);

            MqttMessage msg = new MqttMessage(out.toByteArray());
            msg.setQos(0);
            // TODO QoS and other options from form

            client.setCallback(new PublishCallback());

            client.connect();
            client.publish(topic, msg);
        } catch (MqttException e) {
            throw new IOException(e);
        }
    }

}
