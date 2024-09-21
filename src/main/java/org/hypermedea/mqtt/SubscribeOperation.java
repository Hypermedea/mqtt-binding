package org.hypermedea.mqtt;

import jason.asSyntax.Literal;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.hypermedea.ct.RepresentationHandlers;
import org.hypermedea.ct.txt.PlainTextHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class SubscribeOperation extends MqttOperation {

    private class SubscribeCallback implements MqttCallback {

        @Override
        public void disconnected(MqttDisconnectResponse disconnectResponse) {
            // TODO log
            onError();
//            ack.offer(Optional.of(disconnectResponse.getException()));
        }

        @Override
        public void mqttErrorOccurred(MqttException exception) {
            // TODO log
            // TODO document error codes (2.4 Reason Code, in the MQTTv5 spec)
            // TODO some error codes are client errors, others are server errors
            // https://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html
            onError();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            ByteArrayInputStream in = new ByteArrayInputStream(message.getPayload());
            Collection<Literal> l = RepresentationHandlers.deserialize(in, target, PlainTextHandler.TXT_CT);

            onResponse(new MqttResponse(SubscribeOperation.this, l));
        }

        @Override
        public void deliveryComplete(IMqttToken token) {
            // TODO only triggered if publish (which should raise an exception instead)
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            // ignored
        }

        @Override
        public void authPacketArrived(int reasonCode, MqttProperties properties) {
            // TODO duplicate code with PublishOperation?
        }

    }

    public SubscribeOperation(String resourceURI, Map<String, Object> formFields) {
        super(resourceURI, formFields);
    }

    @Override
    protected void sendSingleRequest() throws IOException {
        try {
            client.setCallback(new SubscribeCallback());

            // TODO QoS and other options

            client.connect();
            client.subscribe(topic, 0);

            // TODO GET = wait for first response?
        } catch (MqttException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void end() throws IOException {
        super.end();

        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            throw new IOException(e);
        }
    }

}
