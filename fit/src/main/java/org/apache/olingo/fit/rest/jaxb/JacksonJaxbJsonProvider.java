package org.apache.olingo.fit.rest.jaxb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jakarta.rs.cfg.Annotations;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

@Provider
@Consumes({"*/*"})
@Produces({"*/*"})
public class JacksonJaxbJsonProvider extends JacksonJsonProvider {
    public static final Annotations[] DEFAULT_ANNOTATIONS;

    public JacksonJaxbJsonProvider() {
        this((ObjectMapper)null, DEFAULT_ANNOTATIONS);
    }

    public JacksonJaxbJsonProvider(Annotations... annotationsToUse) {
        this((ObjectMapper)null, annotationsToUse);
    }

    public JacksonJaxbJsonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
    }

    static {
        DEFAULT_ANNOTATIONS = new Annotations[]{Annotations.JACKSON, Annotations.JAKARTA_XML_BIND};
    }
}
