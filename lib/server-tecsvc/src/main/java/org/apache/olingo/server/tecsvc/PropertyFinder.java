package org.apache.olingo.server.tecsvc;

import org.apache.olingo.commons.api.data.Property;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyFinder {
    Map<String, Property> propertyMap = new ConcurrentHashMap<>();
    public PropertyFinder(List<Property> propertyList) {
        if(propertyList == null) return;
        for(Property property : propertyList) {
            propertyMap.put(property.getName(), property);
        }
    }

    public Property find(String property) {
        return propertyMap.getOrDefault(property, null);
    }
}
