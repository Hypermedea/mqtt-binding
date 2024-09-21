package org.hypermedea.mqtt;

import jason.asSyntax.ASSyntax;
import org.hypermedea.ct.RepresentationHandlers;
import org.hypermedea.op.Operation;
import org.hypermedea.op.ProtocolBindings;
import org.hypermedea.op.Response;
import org.hypermedea.op.ResponseCallback;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MqttBindingTest {

    public static final String TOPIC_URI = "mqtt://test.mosquitto.org/hypermedea_test";

    public static final String TOPIC_MSG = "testing";

    private static class CountingCallback implements ResponseCallback {

        public int count = 0;

        @Override
        public void onResponse(Response response) {
            if (response.getStatus().equals(Response.ResponseStatus.OK)) count++;
        }

        @Override
        public void onError() {
            // do nothing
        }

    }

    @Test
    public void testPubSub() throws IOException {
        Map<String, Object> subForm = new HashMap<>();
        subForm.put(Operation.METHOD_NAME_FIELD, Operation.GET);

        Operation sub = ProtocolBindings.bind(TOPIC_URI, subForm);
        sub.sendRequest();

        Map<String, Object> pubForm = new HashMap<>();
        pubForm.put(Operation.METHOD_NAME_FIELD, Operation.PUT);

        Operation pub = ProtocolBindings.bind(TOPIC_URI, pubForm);
        pub.setPayload(ASSyntax.createLiteral("text", ASSyntax.createString(TOPIC_MSG)));
        pub.sendRequest();
        Response pubRes = pub.getResponse();

        assertEquals(Response.ResponseStatus.OK, pubRes.getStatus());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Response subRes = sub.getResponse();
        RepresentationHandlers.serialize(subRes.getPayload(), out, TOPIC_URI);

        assertEquals(out.toString(), TOPIC_MSG);
    }

    @Test
    public void testLongSub() throws IOException, InterruptedException {
        Map<String, Object> subForm = new HashMap<>();
        subForm.put(Operation.METHOD_NAME_FIELD, Operation.WATCH);

        Operation sub = ProtocolBindings.bind(TOPIC_URI, subForm);
        CountingCallback cb = new CountingCallback();
        sub.registerResponseCallback(cb);
        sub.sendRequest();

        int count = 5;
        for (int i = 0; i < count; i++) {
            Map<String, Object> pubForm = new HashMap<>();
            pubForm.put(Operation.METHOD_NAME_FIELD, Operation.PUT);

            Operation pub = ProtocolBindings.bind(TOPIC_URI, pubForm);
            pub.setPayload(ASSyntax.createLiteral("text", ASSyntax.createString(TOPIC_MSG)));
            pub.sendRequest();
            Response pubRes = pub.getResponse();

            assertEquals(Response.ResponseStatus.OK, pubRes.getStatus());
        }

        Thread.sleep(1000l); // wait for all messages to be distributed

        assertEquals(count, cb.count);
    }

}
