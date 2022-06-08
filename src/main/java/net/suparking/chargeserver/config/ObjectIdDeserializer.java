package net.suparking.chargeserver.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends StdDeserializer<ObjectId> {
    public ObjectIdDeserializer(){
        this(null);
    }
    public ObjectIdDeserializer(Class<ObjectId> t) {
        super(t);
    }
    @Override
    public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        String id = jsonParser.getValueAsString();
        return new ObjectId(id);
    }
}
