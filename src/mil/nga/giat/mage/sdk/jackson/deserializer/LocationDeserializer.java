package mil.nga.giat.mage.sdk.jackson.deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mil.nga.giat.mage.sdk.datastore.location.Location;
import mil.nga.giat.mage.sdk.datastore.location.LocationGeometry;
import mil.nga.giat.mage.sdk.datastore.location.LocationProperty;
import mil.nga.giat.mage.sdk.utils.DateUtility;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class LocationDeserializer extends Deserializer {

	private GeometryDeserializer geometryDeserializer = new GeometryDeserializer();

	public List<Location> parseUserLocations(InputStream is) throws JsonParseException, IOException {
		JsonParser parser = factory.createParser(is);

		List<Location> locations = new ArrayList<Location>();

		if (parser.nextToken() != JsonToken.START_ARRAY)
			return locations;

		while (parser.nextToken() != JsonToken.END_ARRAY) {
			locations.addAll(parseUserLocations(parser));
		}

		parser.close();

		return locations;
	}

	private Collection<Location> parseUserLocations(JsonParser parser) throws JsonParseException, IOException {
		Collection<Location> locations = new ArrayList<Location>();

		if (parser.getCurrentToken() != JsonToken.START_OBJECT)
			return locations;

		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String name = parser.getCurrentName();
			if ("locations".equals(name)) {
				locations.addAll(parseLocations(parser));
			} else {
				parser.nextToken();
				parser.skipChildren();
			}
		}

		return locations;
	}
	
	public List<Location> parseLocations(InputStream is) throws JsonParseException, IOException {
		JsonParser parser = factory.createParser(is);

		List<Location> locations = new ArrayList<Location>();
		locations.addAll(parseLocations(parser));
		parser.close();

		return locations;
	}

	
	private Collection<Location> parseLocations(JsonParser parser) throws JsonParseException, IOException {
		Collection<Location> locations = new ArrayList<Location>();
		parser.nextToken();
		while (parser.nextToken() != JsonToken.END_ARRAY) {
			locations.add(parseLocation(parser));
		}
		return locations;
	}

	private Location parseLocation(JsonParser parser) throws JsonParseException, IOException {
		Location location = new Location();

		if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
			return location;
		}

		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String name = parser.getCurrentName();
			if ("_id".equals(name)) {
				parser.nextToken();
				location.setRemoteId(parser.getText());
			} else if ("type".equals(name)) {
				parser.nextToken();
				location.setType(parser.getText());
			} else if ("geometry".equals(name)) {
				parser.nextToken();
				location.setLocationGeometry(new LocationGeometry(geometryDeserializer.parseGeometry(parser)));
			} else if ("properties".equals(name)) {
				parser.nextToken();
				location.setProperties(parseProperties(parser, location));
			} else {
				parser.nextToken();
				parser.skipChildren();
			}
		}

		Map<String, LocationProperty> properties = location.getPropertiesMap();

		// don't set the user at this time.  Set it later.

		// timestamp is special pull it out of properties and set it at the top level
		LocationProperty timestamp = properties.get("timestamp");
		if (timestamp != null) {
			try {
				Date d = DateUtility.getISO8601().parse(timestamp.getValue().toString());
				location.setTimestamp(d);
			} catch (ParseException pe) {
				Log.w("Unable to parse date: " + timestamp + " for location: " + location.getRemoteId(), pe);
			}
		}
		return location;
	}

	private Collection<LocationProperty> parseProperties(JsonParser parser, Location location) throws JsonParseException, IOException {
		Collection<LocationProperty> properties = new ArrayList<LocationProperty>();
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String key = parser.getCurrentName();
			JsonToken token = parser.nextToken();
			if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
				parser.skipChildren();
			} else {
				Serializable value = parser.getText();
				if(token.isNumeric()) {
					switch (parser.getNumberType()) {
					case BIG_DECIMAL:
						break;
					case BIG_INTEGER:
						break;
					case DOUBLE:
						value = parser.getDoubleValue();
						break;
					case FLOAT:
						value = parser.getFloatValue();
						break;
					case INT:
						value = parser.getIntValue();
						break;
					case LONG:
						value = parser.getLongValue();
						break;
					default:
						break;
					}
				} else if(token.isBoolean()) {
					value = parser.getBooleanValue();
				}
				LocationProperty property = new LocationProperty(key, value);
				property.setLocation(location);
				properties.add(property);
			}
		}

		return properties;
	}
}