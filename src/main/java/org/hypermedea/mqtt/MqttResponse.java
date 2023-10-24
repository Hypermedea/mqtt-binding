package org.hypermedea.mqtt;

import jason.asSyntax.Literal;
import org.hypermedea.op.BaseResponse;
import org.hypermedea.op.Operation;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

public class MqttResponse extends BaseResponse {

    private final ResponseStatus status;

    private final Collection<Literal> payload;

    public MqttResponse(Operation op, ResponseStatus status) {
        super(op);

        this.status = status;
        this.payload = new HashSet<>();
    }

    public MqttResponse(Operation op, Collection<Literal> payload) {
        super(op);

        this.status = ResponseStatus.OK;
        this.payload = payload;
    }

    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public Collection<Literal> getPayload() {
        return payload;
    }

}
