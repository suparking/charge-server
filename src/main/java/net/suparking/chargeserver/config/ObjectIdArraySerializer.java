package net.suparking.chargeserver.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;

public class ObjectIdArraySerializer extends StdSerializer<List<ObjectId>> {
    public ObjectIdArraySerializer(){
        this(null);
    }
    public ObjectIdArraySerializer(Class<List<ObjectId>> t) {
        super(t);
    }
    @Override
    public void serialize(List<ObjectId> objectIds,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (ObjectId id: objectIds) {
            jsonGenerator.writeString(id.toString());
        }
        jsonGenerator.writeEndArray();
    }
}
