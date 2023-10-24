package org.hypermedea.mqtt;

import jason.asSyntax.ASSyntax;
import org.hypermedea.ct.RepresentationHandlers;
import org.hypermedea.op.Operation;
import org.hypermedea.op.ProtocolBindings;
import org.hypermedea.op.Response;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MqttBindingTest {

    public static final String TOPIC_URI = "mqtt://test.mosquitto.org/hypermedea_test";

    public static final String TOPIC_MSG = "testing";

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

        assertEquals(new String(out.toByteArray()), TOPIC_MSG);
    }

}
