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
import org.hypermedea.op.NoResponseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SubscribeOperation extends MqttOperation {

    private class SubscribeCallback implements MqttCallback {

        private IMqttToken subscriptionToken = null;

        @Override
        public void disconnected(MqttDisconnectResponse disconnectResponse) {
            // TODO log
            onError();
            ack.offer(Optional.of(disconnectResponse.getException()));
        }

        @Override
        public void mqttErrorOccurred(MqttException exception) {
            // TODO log
            // TODO document error codes (2.4 Reason Code, in the MQTTv5 spec)
            // TODO some error codes are client errors, others are server errors
            // https://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html
            onError();
            ack.offer(Optional.of(exception));
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            ByteArrayInputStream in = new ByteArrayInputStream(message.getPayload());
            Collection<Literal> l = RepresentationHandlers.deserialize(in, target, PlainTextHandler.TXT_CT);

            onResponse(new MqttResponse(SubscribeOperation.this, l));
        }

        @Override
        public void deliveryComplete(IMqttToken token) {
            if (token.equals(subscriptionToken)) ack.offer(Optional.empty());
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            // TODO topic filter may not correspond to a single MQTT resource (topic)
            try {
                subscriptionToken = client.subscribe(topic, 0);
                if (subscriptionToken.isComplete()) ack.offer(Optional.empty());
            } catch (MqttException e) {
                onError();
            }
        }

        @Override
        public void authPacketArrived(int reasonCode, MqttProperties properties) {
            // TODO duplicate code with PublishOperation?
        }

    }

    private final BlockingDeque<Optional<MqttException>> ack = new LinkedBlockingDeque<>(1);

    public SubscribeOperation(String resourceURI, Map<String, Object> formFields) {
        super(resourceURI, formFields);
    }

    @Override
    protected void sendSingleRequest() throws IOException {
        try {
            client.setCallback(new SubscribeCallback());

            // TODO QoS and other options

            client.connect();

            Optional<MqttException> opt = ack.poll(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            if (opt == null) throw new NoResponseException();
            else if (opt.isPresent()) throw opt.get();
            // else, broker acknowledged subscription

            // TODO GET = wait for first response?
        } catch (MqttException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
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
