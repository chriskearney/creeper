package events;

import com.comandante.creeper.Creeper;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("application/json")
public class CreeperEventMessageBodyWriter implements MessageBodyWriter<CreeperEvent> {

    private final ObjectMapper objectMapper = Creeper.registerJdkModuleAndGetMapper();

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == CreeperEvent.class;
    }

    @Override
    public long getSize(CreeperEvent creeperEvent, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(CreeperEvent creeperEvent, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        OutputStreamWriter osw = new OutputStreamWriter(entityStream, "UTF-8");
        osw.write(objectMapper.writeValueAsString(creeperEvent));
    }
}
