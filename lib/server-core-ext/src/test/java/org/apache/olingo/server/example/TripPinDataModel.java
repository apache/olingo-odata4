/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.core.deserializer.json.ODataJsonDeserializer;
import org.apache.olingo.server.core.responses.EntityResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TripPinDataModel {
  private final ServiceMetadata metadata;
  private HashMap<String, EntityCollection> entitySetMap = new HashMap<String, EntityCollection>();
  private Map<Integer, Map<String,Object>> tripLinks = new HashMap<Integer, Map<String,Object>>();
  private Map<String, Map<String,Object>> peopleLinks = new HashMap<String, Map<String,Object>>();
  private Map<Integer, Map<String, Object>> flightLinks = new HashMap<Integer, Map<String, Object>>();

  public TripPinDataModel(ServiceMetadata metadata) throws Exception {
    this.metadata = metadata;
    loadData();
  }

  @SuppressWarnings("unchecked")
  public void loadData() throws Exception {
    EdmEntityContainer ec = metadata.getEdm().getEntityContainer(null);
    for (EdmEntitySet edmEntitySet : ec.getEntitySets()) {
      String entitySetName = edmEntitySet.getName();
      EntityCollection set = loadEnities(entitySetName, edmEntitySet.getEntityType());
      if (set != null) {
        this.entitySetMap.put(entitySetName, set);
      }
    }

    EdmEntityType type = metadata.getEdm().getEntityType(
        new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin", "Trip"));
    this.entitySetMap.put("Trip", loadEnities("Trip", type));

    type = metadata.getEdm().getEntityType(
        new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin", "Flight"));
    this.entitySetMap.put("Flight", loadEnities("Flight", type));

    type = metadata.getEdm().getEntityType(
        new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin", "Event"));
    this.entitySetMap.put("Event", loadEnities("Event", type));

    ObjectMapper mapper = new ObjectMapper();
    Map<String, List<Object>> tripLinks = mapper.readValue(new FileInputStream(new File(
        "src/test/resources/trip-links.json")), Map.class);
    for (Object link : tripLinks.get("value")) {
      Map<String, Object> map = (Map<String, Object>) link;
      this.tripLinks.put((Integer) map.get("TripId"), map);
    }

    Map<String, List<Object>> peopleLinks = mapper.readValue(new FileInputStream(new File(
        "src/test/resources/people-links.json")), Map.class);
    for (Object link : peopleLinks.get("value")) {
      Map<String, Object> map = (Map<String, Object>) link;
      this.peopleLinks.put((String) map.get("UserName"), map);
    }

    Map<String, List<Object>> flightLinks = mapper.readValue(new FileInputStream(new File(
        "src/test/resources/flight-links.json")), Map.class);
    for (Object link : flightLinks.get("value")) {
      Map<String, Object> map = (Map<String, Object>) link;
      this.flightLinks.put((Integer) map.get("PlanItemId"), map);
    }
  }

  private EntityCollection loadEnities(String entitySetName, EdmEntityType type) {
    try {
      ODataJsonDeserializer deserializer = new ODataJsonDeserializer(ContentType.JSON, this.metadata);

      EntityCollection set = deserializer.entityCollection(new FileInputStream(new File(
          "src/test/resources/" + entitySetName.toLowerCase() + ".json")), type).getEntityCollection();
      // TODO: the count needs to be part of deserializer
      set.setCount(set.getEntities().size());
      for (Entity entity : set.getEntities()) {
        entity.setETag(UUID.randomUUID().toString());
        entity.setId(new URI(EntityResponse.buildLocation("", entity, entitySetName,
            type)));
        entity.setType(type.getFullQualifiedName().getFullQualifiedNameAsString());
      }
      return set;
    } catch (FileNotFoundException e) {
      // keep going
      e.printStackTrace();
    } catch (DeserializerException e) {
      // keep going
      e.printStackTrace();
    } catch (URISyntaxException e) {
      // keep going
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public EntityCollection getEntitySet(String name) {
    return getEntitySet(name, -1, -1);
  }

  public EntityCollection getEntitySet(String name, int skip, int pageSize) {
    EntityCollection set = this.entitySetMap.get(name);
    if (set == null) {
      return null;
    }

    EntityCollection modifiedES = new EntityCollection();
    int i = 0;
    for (Entity e : set.getEntities()) {
      if (skip >= 0 && i >= skip && modifiedES.getEntities().size() < pageSize) {
        modifiedES.getEntities().add(e);
      }
      i++;
    }
    modifiedES.setCount(i);
    set.setCount(i);

    if (skip == -1 && pageSize == -1) {
      return set;
    }
    return modifiedES;
  }

  private List<Entity> getMatch(UriParameter param, List<Entity> es)
      throws ODataApplicationException {
    ArrayList<Entity> list = new ArrayList<Entity>();
    for (Entity entity : es) {

      EdmEntityType entityType = this.metadata.getEdm().getEntityType(
          new FullQualifiedName(entity.getType()));

      EdmProperty property = (EdmProperty) entityType.getProperty(param.getName());
      EdmType type = property.getType();
      if (type.getKind() == EdmTypeKind.PRIMITIVE) {
        Object match = readPrimitiveValue(property, param.getText());
        Property entityValue = entity.getProperty(param.getName());
        if (match.equals(entityValue.asPrimitive())) {
          list.add(entity);
        }
      } else {
        throw new RuntimeException("Can not compare complex objects");
      }
    }
    return list;
  }

  static Object readPrimitiveValue(EdmProperty edmProperty, String value)
      throws ODataApplicationException {
    if (value == null) {
      return null;
    }
    try {
      if (value.startsWith("'") && value.endsWith("'")) {
        value = value.substring(1,value.length()-1);
      }
      EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmProperty.getType();
      Class<?> javaClass = getJavaClassForPrimitiveType(edmProperty, edmPrimitiveType);
      return edmPrimitiveType.valueOfString(value, edmProperty.isNullable(),
          edmProperty.getMaxLength(), edmProperty.getPrecision(), edmProperty.getScale(),
          edmProperty.isUnicode(), javaClass);
    } catch (EdmPrimitiveTypeException e) {
      throw new ODataApplicationException("Invalid value: " + value + " for property: "
          + edmProperty.getName(), 500, Locale.getDefault());
    }
  }

  static Class<?> getJavaClassForPrimitiveType(EdmProperty edmProperty, EdmPrimitiveType edmPrimitiveType) {
    Class<?> javaClass = null;
    if (edmProperty.getMapping() != null && edmProperty.getMapping().getMappedJavaClass() != null) {
      javaClass = edmProperty.getMapping().getMappedJavaClass();
    } else {
      javaClass = edmPrimitiveType.getDefaultType();
    }

    edmPrimitiveType.getDefaultType();
    return javaClass;
  }

  public Entity getEntity(String name, List<UriParameter> keys) throws ODataApplicationException {
    EntityCollection es = getEntitySet(name);
    return getEntity(es, keys);
  }

  public Entity getEntity(EntityCollection es, List<UriParameter> keys) throws ODataApplicationException {
    List<Entity> search = es.getEntities();
    for (UriParameter param : keys) {
      search = getMatch(param, search);
    }
    if (search.isEmpty()) {
      return null;
    }
    return search.get(0);
  }

  @SuppressWarnings("unchecked")
  private EntityCollection getFriends(String userName) {
    Map<String, Object> map = this.peopleLinks.get(userName);
    if (map == null) {
      return null;
    }
    ArrayList<String> friends = (ArrayList<String>) map.get("Friends");
    EntityCollection set = getEntitySet("People");

    EntityCollection result = new EntityCollection();
    int i = 0;
    if (friends != null) {
      for (String friend : friends) {
        for (Entity e : set.getEntities()) {
          if (e.getProperty("UserName").getValue().equals(friend)) {
            result.getEntities().add(e);
            i++;
            break;
          }
        }
      }
    }
    result.setCount(i);
    return result;
  }

  @SuppressWarnings("unchecked")
  private EntityCollection getTrips(String userName) {
    Map<String, Object> map = this.peopleLinks.get(userName);
    if (map == null) {
      return null;
    }

    ArrayList<Integer> trips = (ArrayList<Integer>) map.get("Trips");
    EntityCollection set = getEntitySet("Trip");

    EntityCollection result = new EntityCollection();
    int i = 0;
    if (trips != null) {
      for (int trip : trips) {
        for (Entity e : set.getEntities()) {
          if (e.getProperty("TripId").getValue().equals(trip)) {
            result.getEntities().add(e);
            i++;
            break;
          }
        }
      }
    }
    result.setCount(i);
    return result;
  }

  private Entity getPhoto(String userName) {
    Map<String, Object> map = this.peopleLinks.get(userName);
    if (map == null) {
      return null;
    }

    Integer photoID = (Integer) map.get("Photo");
    EntityCollection set = getEntitySet("Photos");
    if (photoID != null) {
      for (Entity e : set.getEntities()) {
        if (e.getProperty("Id").getValue().equals(photoID.longValue())) {
          return e;
        }
      }
    }
    return null;
  }

  private EntityCollection getPlanItems(int tripId, EntityCollection result) {
    getFlights(tripId, result);
    getEvents(tripId, result);
    return result;
  }

  @SuppressWarnings("unchecked")
  private EntityCollection getEvents(int tripId, EntityCollection result) {
    Map<String, Object> map = this.tripLinks.get(tripId);
    if (map == null) {
      return null;
    }

    ArrayList<Integer> events = (ArrayList<Integer>) map.get("Events");
    EntityCollection set = getEntitySet("Event");
    int i = result.getEntities().size();
    if (events != null) {
      for (int event : events) {
        for (Entity e : set.getEntities()) {
          if (e.getProperty("PlanItemId").getValue().equals(event)) {
            result.getEntities().add(e);
            i++;
            break;
          }
        }
      }
    }
    result.setCount(i);
    return result;
  }

  @SuppressWarnings("unchecked")
  private EntityCollection getFlights(int tripId, EntityCollection result) {
    Map<String, Object> map = this.tripLinks.get(tripId);
    if (map == null) {
      return null;
    }

    ArrayList<Integer> flights = (ArrayList<Integer>) map.get("Flights");
    EntityCollection set = getEntitySet("Flight");
    int i = result.getEntities().size();
    if (flights != null) {
      for (int flight : flights) {
        for (Entity e : set.getEntities()) {
          if (e.getProperty("PlanItemId").getValue().equals(flight)) {
            result.getEntities().add(e);
            i++;
            break;
          }
        }
      }
    }
    result.setCount(i);
    return result;
  }

  @SuppressWarnings("unchecked")
  private EntityCollection getTripPhotos(int tripId) {
    Map<String, Object> map = this.tripLinks.get(tripId);
    if (map == null) {
      return null;
    }

    ArrayList<Integer> photos = (ArrayList<Integer>) map.get("Photos");

    EntityCollection set = getEntitySet("Photos");
    EntityCollection result = new EntityCollection();
    int i = 0;
    if (photos != null) {
      for (int photo : photos) {
        for (Entity e : set.getEntities()) {
          if (e.getProperty("Id").getValue().equals(photo)) {
            result.getEntities().add(e);
            i++;
            break;
          }
        }
      }
    }
    result.setCount(i);
    return result;
  }

  private Entity getFlightFrom(int flighID) {
    Map<String, Object> map = this.flightLinks.get(flighID);
    if (map == null) {
      return null;
    }

    String from = (String) map.get("From");
    EntityCollection set = getEntitySet("Airports");

    if (from != null) {
      for (Entity e : set.getEntities()) {
        if (e.getProperty("IataCode").getValue().equals(from)) {
          return e;
        }
      }
    }
    return null;
  }

  private Entity getFlightTo(int flighID) {
    Map<String, Object> map = this.flightLinks.get(flighID);
    if (map == null) {
      return null;
    }

    String to = (String) map.get("To");
    EntityCollection set = getEntitySet("Airports");

    if (to != null) {
      for (Entity e : set.getEntities()) {
        if (e.getProperty("IataCode").getValue().equals(to)) {
          return e;
        }
      }
    }
    return null;
  }

  private Entity getFlightAirline(int flighID) {
    Map<String, Object> map = this.flightLinks.get(flighID);
    if (map == null) {
      return null;
    }

    String airline = (String) map.get("Airline");
    EntityCollection set = getEntitySet("Airlines");

    if (airline != null) {
      for (Entity e : set.getEntities()) {
        if (e.getProperty("AirlineCode").getValue().equals(airline)) {
          return e;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public void addNavigationLink(String navigation, Entity parentEntity, Entity childEntity) {

    EdmEntityType type = this.metadata.getEdm().getEntityType(
        new FullQualifiedName(parentEntity.getType()));
    String key = type.getKeyPredicateNames().get(0);
    if (type.getName().equals("Person") && navigation.equals("Friends")) {
      Map<String, Object> map = this.peopleLinks.get(parentEntity.getProperty(key).getValue());
      if (map == null) {
        map = new HashMap<String, Object>();
        this.peopleLinks.put((String) parentEntity.getProperty(key).getValue(), map);
      }

      ArrayList<String> friends = (ArrayList<String>) map.get("Friends");
      if (friends == null) {
        friends = new ArrayList<String>();
        map.put("Friends", friends);
      }
      friends.add((String) childEntity.getProperty(key).getValue());
      setLink(parentEntity, navigation, childEntity);
    } else if (type.getName().equals("Person") && navigation.equals("Trips")) {
      Map<String, Object> map = this.peopleLinks.get(parentEntity.getProperty(key).getValue());
      if (map == null) {
        map = new HashMap<String, Object>();
        this.peopleLinks.put((String) parentEntity.getProperty(key).getValue(), map);
      }

      ArrayList<Integer> trips = (ArrayList<Integer>) map.get("Trips");
      if (trips == null) {
        trips = new ArrayList<Integer>();
        map.put("Trips", trips);
      }
      trips.add((Integer) childEntity.getProperty(key).getValue());
      setLink(parentEntity, navigation, childEntity);
    } else if (type.getName().equals("Person") && navigation.equals("Photo")) {
      Map<String, Object> map = this.peopleLinks.get(parentEntity.getProperty(key).getValue());
      if (map == null) {
        map = new HashMap<String, Object>();
        this.peopleLinks.put((String) parentEntity.getProperty(key).getValue(), map);
      }
      map.put("Photo", childEntity.getProperty(key).getValue());
      setLink(parentEntity, navigation, childEntity);
    } else if (type.getName().equals("Trip") && navigation.equals("PlanItems")) {
      Map<String, Object> map = this.tripLinks.get(parentEntity.getProperty(key).getValue());
      if (map == null) {
        map = new HashMap<String, Object>();
        this.tripLinks.put((Integer) parentEntity.getProperty(key).getValue(), map);
      }
      if (childEntity.getType().equals("Flight")) {
        ArrayList<Integer> flights = (ArrayList<Integer>) map.get("Flights");
        if (flights == null) {
          flights = new ArrayList<Integer>();
          map.put("Flights", flights);
        }
        flights.add((Integer) childEntity.getProperty(key).getValue());
      } else {
        ArrayList<Integer> events = (ArrayList<Integer>) map.get("Events");
        if (events == null) {
          events = new ArrayList<Integer>();
          map.put("Events", events);
        }
        events.add((Integer) childEntity.getProperty(key).getValue());
      }
      setLink(parentEntity, navigation, childEntity);
    } else if (type.getName().equals("Trip") && navigation.equals("Photo")) {
      Map<String, Object> map = this.tripLinks.get(parentEntity.getProperty(key).getValue());
      if (map == null) {
        map = new HashMap<String, Object>();
        this.tripLinks.put((Integer) parentEntity.getProperty(key).getValue(), map);
      }
      ArrayList<Integer> photos = (ArrayList<Integer>) map.get("Photos");
      if (photos == null) {
        photos = new ArrayList<Integer>();
        map.put("Photos", photos);
      }
      photos.add((Integer) childEntity.getProperty(key).getValue());
      setLink(parentEntity, navigation, childEntity);
    } else if (type.getName().equals("Flight") && navigation.equals("From")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map == null) {
        map = new HashMap<String, Object>();
        this.flightLinks.put((Integer) parentEntity.getProperty(key).getValue(), map);
      }
      map.put("From", childEntity.getProperty(key).getValue());
      setLink(parentEntity, navigation, childEntity);
    } else if (type.getName().equals("Flight") && navigation.equals("To")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map == null) {
        map = new HashMap<String, Object>();
        this.flightLinks.put((Integer) parentEntity.getProperty(key).getValue(), map);
      }
      map.put("To", childEntity.getProperty(key).getValue());
      setLink(parentEntity, navigation, childEntity);
    } else if (type.getName().equals("Flight") && navigation.equals("Airline")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map == null) {
        map = new HashMap<String, Object>();
        this.flightLinks.put((Integer) parentEntity.getProperty(key).getValue(), map);
      }
      map.put("Airline", childEntity.getProperty(key).getValue());
      setLink(parentEntity, navigation, childEntity);
    } else {
      throw new RuntimeException("unknown relation");
    }
  }

  protected static void setLink(Entity entity, final String navigationPropertyName,
      final Entity target) {
    Link link = new Link();
    link.setTitle(navigationPropertyName);
    link.setInlineEntity(target);
    entity.getNavigationLinks().add(link);
  }

  public boolean updateNavigationLink(String navigationProperty, Entity parentEntity,
      Entity updateEntity) {
    boolean updated = false;
    EdmEntityType type = this.metadata.getEdm().getEntityType(
        new FullQualifiedName(parentEntity.getType()));
    String key = type.getKeyPredicateNames().get(0);

    EdmEntityType updateType = this.metadata.getEdm().getEntityType(
        new FullQualifiedName(updateEntity.getType()));
    String updateKey = updateType.getKeyPredicateNames().get(0);

    if (type.getName().equals("Person") && navigationProperty.equals("Photo")) {
      Map<String, Object> map = this.peopleLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        map.put("Photo", ((Long) updateEntity.getProperty(updateKey).getValue()).intValue());
        updated = true;
      }
    } else if (type.getName().equals("Flight") && navigationProperty.equals("From")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        map.put("From", updateEntity.getProperty(updateKey).getValue());
        updated = true;
      }
    } else if (type.getName().equals("Flight") && navigationProperty.equals("To")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        map.put("To", updateEntity.getProperty(updateKey).getValue());
        updated = true;
      }
    } else if (type.getName().equals("Flight") && navigationProperty.equals("Airline")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        map.put("Airline", updateEntity.getProperty(updateKey).getValue());
        updated = true;
      }
    } else {
      throw new RuntimeException("unknown relation");
    }
    return updated;
  }

  public Entity createEntity(EdmEntitySet edmEntitySet, Entity entity, String baseURL)
      throws ODataApplicationException {

    EntityCollection set = this.entitySetMap.get(edmEntitySet.getName());
    Entity copy = new Entity();
    copy.setType(entity.getType());
    for (Property p : entity.getProperties()) {
      copy.addProperty(p);
    }

    try {
      copy.setId(new URI(EntityResponse.buildLocation(baseURL, entity, edmEntitySet.getName(), edmEntitySet
          .getEntityType())));
      copy.setETag(UUID.randomUUID().toString());
    } catch (URISyntaxException e) {
      throw new ODataApplicationException("Failed to create ID for entity", 500,
          Locale.getDefault());
    } catch (EdmPrimitiveTypeException e) {
      throw new ODataApplicationException("Failed to create ID for entity", 500,
          Locale.getDefault());
    }
    set.getEntities().add(copy);
    return copy;
  }

  public boolean updateEntity(EdmEntitySet edmEntitySet, String eTag, String key, Object keyValue, 
      boolean merge, Entity changes, String baseURL) throws ODataApplicationException {
    boolean updated = false;

    if (merge) {
      EntityCollection set = getEntitySet(edmEntitySet.getName());
      Iterator<Entity> it = set.getEntities().iterator();
      while (it.hasNext()) {
        Entity entity = it.next();
        if (entity.getProperty(key).getValue().equals(keyValue) && eTag.equals("*")
            || eTag.equals(entity.getETag())) {
          
          for (Property p :changes.getProperties()) {
            for (Property p1: entity.getProperties()) {
              if (p.getName().equals(p1.getName())) {
                p1.setValue(p1.getValueType(), p.getValue());
                updated = true;
                break;
              }
            }
          }
          break;
        }
      }
    } else {
      // this is delete, then insert      
      EntityCollection set = getEntitySet(edmEntitySet.getName());
      Iterator<Entity> it = set.getEntities().iterator();
      while (it.hasNext()) {
        Entity entity = it.next();
        if (entity.getProperty(key).getValue().equals(keyValue) && eTag.equals("*")
            || eTag.equals(entity.getETag())) {
          Property p = entity.getProperty(key);
          changes.addProperty(p);
          createEntity(edmEntitySet, changes, baseURL);
          updated = true;
        }
      }
    }
    return updated;
  }
  
  public boolean deleteEntity(String entitySetName, String eTag, String key, Object keyValue) {
    EntityCollection set = getEntitySet(entitySetName);
    Iterator<Entity> it = set.getEntities().iterator();
    boolean removed = false;
    while (it.hasNext()) {
      Entity entity = it.next();
      if (entity.getProperty(key).getValue().equals(keyValue) && eTag.equals("*")
          || eTag.equals(entity.getETag())) {
        it.remove();
        removed = true;
        break;
      }
    }
    return removed;
  }

  public boolean updateProperty(String entitySetName, String eTag, String key, Object keyValue,
      Property property) {
    EntityCollection set = getEntitySet(entitySetName);
    Iterator<Entity> it = set.getEntities().iterator();
    boolean replaced = false;
    while (it.hasNext()) {
      Entity entity = it.next();
      if (entity.getProperty(key).getValue().equals(keyValue) && eTag.equals("*")
          || eTag.equals(entity.getETag())) {
        entity.getProperty(property.getName()).setValue(property.getValueType(),
            property.getValue());
        replaced = true;
        break;
      }
    }
    return replaced;
  }

  public EntityCollection getNavigableEntitySet(Entity parentEntity, UriResourceNavigation navigation) {
    EdmEntityType type = this.metadata.getEdm().getEntityType(
        new FullQualifiedName(parentEntity.getType()));

    String key = type.getKeyPredicateNames().get(0);
    String linkName = navigation.getProperty().getName();

    EntityCollection results = null;
    if (type.getName().equals("Person") && linkName.equals("Friends")) {
      results = getFriends((String) parentEntity.getProperty(key).getValue());
    } else if (type.getName().equals("Person") && linkName.equals("Trips")) {
      results = getTrips((String) parentEntity.getProperty(key).getValue());
    } else if (type.getName().equals("Trip") && linkName.equals("PlanItems")) {
      EntityCollection planitems = new EntityCollection();
      if (navigation.getTypeFilterOnCollection() == null) {
        results = getPlanItems((Integer) parentEntity.getProperty(key).getValue(), planitems);
      } else if (navigation.getTypeFilterOnCollection().getName().equals("Flight")) {
        results = getFlights((Integer) parentEntity.getProperty(key).getValue(), planitems);
      } else if (navigation.getTypeFilterOnCollection().getName().equals("Event")) {
        results = getEvents((Integer) parentEntity.getProperty(key).getValue(), planitems);
      } else {
        throw new RuntimeException("unknown relation");
      }
    } else if (type.getName().equals("Trip") && linkName.equals("Photos")) {
      results = getTripPhotos((Integer) parentEntity.getProperty(key).getValue());
    }
    return results;
  }

  public Entity getNavigableEntity(Entity parentEntity, UriResourceNavigation navigation)
      throws ODataApplicationException {
    EdmEntityType type = this.metadata.getEdm().getEntityType(
        new FullQualifiedName(parentEntity.getType()));

    String key = type.getKeyPredicateNames().get(0);
    String linkName = navigation.getProperty().getName();

    EntityCollection results = null;
    if (navigation.getProperty().isCollection()) {
      results = getNavigableEntitySet(parentEntity, navigation);
      return this.getEntity(results, navigation.getKeyPredicates());
    }
    if (type.getName().equals("Person") && linkName.equals("Photo")) {
      return getPhoto((String) parentEntity.getProperty(key).getValue());
    } else if (type.getName().equals("Flight") && linkName.equals("From")) {
      return getFlightFrom((Integer) parentEntity.getProperty(key).getValue());
    } else if (type.getName().equals("Flight") && linkName.equals("To")) {
      return getFlightTo((Integer) parentEntity.getProperty(key).getValue());
    } else if (type.getName().equals("Flight") && linkName.equals("Airline")) {
      return getFlightAirline((Integer) parentEntity.getProperty(key).getValue());
    } else {
      throw new RuntimeException("unknown relation");
    }
  }

  @SuppressWarnings("unchecked")
  public boolean removeNavigationLink(String navigationProperty, Entity parentEntity,
      Entity deleteEntity) {
    boolean removed = false;
    EdmEntityType type = this.metadata.getEdm().getEntityType(
        new FullQualifiedName(parentEntity.getType()));
    String key = type.getKeyPredicateNames().get(0);

    if (type.getName().equals("Person") && navigationProperty.equals("Friends")) {
      Map<String, Object> map = this.peopleLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        ArrayList<String> friends = (ArrayList<String>) map.get("Friends");
        if (friends != null) {
          friends.remove(deleteEntity.getProperty(key).getValue());
          removed = true;
        }
      }
    } else if (type.getName().equals("Person") && navigationProperty.equals("Trips")) {
      Map<String, Object> map = this.peopleLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        ArrayList<Integer> trips = (ArrayList<Integer>) map.get("Trips");
        if (trips != null) {
          trips.remove(deleteEntity.getProperty(key).getValue());
          removed = true;
        }
      }
    } else if (type.getName().equals("Person") && navigationProperty.equals("Photo")) {
      Map<String, Object> map = this.peopleLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        map.remove("Photo");
        removed = true;
      }
    } else if (type.getName().equals("Trip") && navigationProperty.equals("PlanItems")) {
      Map<String, Object> map = this.tripLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        if (deleteEntity.getType().equals("Flight")) {
          ArrayList<Integer> flights = (ArrayList<Integer>) map.get("Flights");
          if (flights != null) {
            flights.remove(deleteEntity.getProperty(key).getValue());
            removed = true;
          }
        } else {
          ArrayList<Integer> events = (ArrayList<Integer>) map.get("Events");
          if (events != null) {
            events.remove(deleteEntity.getProperty(key).getValue());
            removed = true;
          }
        }
      }
    } else if (type.getName().equals("Trip") && navigationProperty.equals("Photo")) {
      Map<String, Object> map = this.tripLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        ArrayList<Integer> photos = (ArrayList<Integer>) map.get("Photos");
        if (photos != null) {
          photos.remove(deleteEntity.getProperty(key).getValue());
          removed = true;
        }
      }
    } else if (type.getName().equals("Flight") && navigationProperty.equals("From")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        map.remove("From");
        removed = true;
      }
    } else if (type.getName().equals("Flight") && navigationProperty.equals("To")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        map.remove("To");
        removed = true;
      }
    } else if (type.getName().equals("Flight") && navigationProperty.equals("Airline")) {
      Map<String, Object> map = this.flightLinks.get(parentEntity.getProperty(key).getValue());
      if (map != null) {
        map.remove("Airline");
        removed = true;
      }
    } else {
      throw new RuntimeException("unknown relation");
    }
    return removed;
  }

  // note these are not tied to entities for simplicity sake
  public boolean updateMedia(Entity entity, InputStream mediaContent)
      throws ODataApplicationException {
    checkForMedia(entity);
    return true;
  }

  //  note these are not tied to entities for simplicity sake
  public InputStream readMedia(Entity entity) throws ODataApplicationException {
    checkForMedia(entity);
    try {
      return new FileInputStream(new File("src/test/resources/OlingoOrangeTM.png"));
    } catch (FileNotFoundException e) {
      throw new ODataApplicationException("image not found", 500, Locale.getDefault());
    }
  }

  //  note these are not tied to entities for simplicity sake
  public boolean deleteMedia(Entity entity) throws ODataApplicationException {
    checkForMedia(entity);
    return true;
  }

  private void checkForMedia(Entity entity) throws ODataApplicationException {
    EdmEntityType type = this.metadata.getEdm().getEntityType(
        new FullQualifiedName(entity.getType()));
    if (!type.hasStream()) {
      throw new ODataApplicationException("No Media proeprty on the entity", 500,
          Locale.getDefault());
    }
  }

  public boolean deleteStream(Entity entity, EdmProperty property) {
    // should remove stream links
    return true;
  }

  public boolean updateStream(Entity entity, EdmProperty property, InputStream streamContent) {
    // should add stream links
    return true;
  }
}