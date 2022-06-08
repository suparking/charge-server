package net.suparking.chargeserver.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ObjectIdArrayDeserializer extends StdDeserializer<List<ObjectId>> {
    public ObjectIdArrayDeserializer(){
        this(null);
    }
    public ObjectIdArrayDeserializer(Class<List<ObjectId>> t) {
        super(t);
    }
    @Override
    public List<ObjectId> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        List<ObjectId> objectIds = new LinkedList<>();
        ArrayNode arrayNode = jsonParser.readValueAsTree();
        for (int i = 0; i < arrayNode.size(); ++i) {
            objectIds.add(new ObjectId(arrayNode.get(i).asText()));
        }
        return objectIds;
    }
}
