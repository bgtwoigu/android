package twitter4j.api;

import twitter4j.GeoQuery;
import twitter4j.TwitterListener;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twi  Ex "r.1.1
 */
public interface GeoMethodsAsync {
    /**
     * Search for places (cities and neighborhoods) that can be attached to a <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0update">statuses/update</a>.  Given a latitude and a longitude pair, or an IP address, return a list of all the valid cities and neighborhoods that can be used as a place_id when updating a status.  Conceptually, a query can be made from the user's location, retrieve a list of places, have the user validate the location he or she is at, and then send the ID of this location up with a call to <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-statuses%C2%A0update">statuses/update</a>.
     * <br>This method calls http://api.twitter.com/1/geo/nearby_places.json
     * @param query search query
     * @see <a href="http://apiwiki.twitter.com/Twitter-REST-API-Method%3A-GET-geo-nearby_places">Twitter API Wiki / Twitter REST API Method: GET geo nearby_places</a>
     * @deprecated <a href="http://code.google.com/p/twitter-api/issues/detail?id=1754">Issue 1754</a>
     * @since Twitter4J 2.1.1
     */
    void getNearbyPlaces(GeoQuery query,TwitterListener listener);

    /**
     * Search for places (cities and neighborhoods) that can be attached to a statuses/update. Given a latitude and a longitude, return a list of all the valid places that can be used as a place_id when updating a status. Conceptually, a query can be made from the user's location, retrieve a list of places, have the user validate the location he or she is at, and then send the ID of this location up with a call to statuses/update.<br>
      * There are multiple granularities of places that can be returned -- "neighborhoods", "cities", etc. At this time, only United States data is available through this method.<br>
     * This API call is meant to be an informative call and will deliver generalized results about geography.
     * <br>This method calls http://api.twitter.com/1/geo/reverse_geocode.json
     * @param query search query
     * @see <a href="http://dev.twitter.com/doc/get/geo/reverse_geocode">GET geo/reverse_geocode | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void reverseGeoCode(GeoQuery query,TwitterListener listener);

    /**
     * Find out more details of a place that was returned from the {@link twitter4j.api.GeoMethodsAsync#reverseGeoCode(twitter4j.GeoQuery)} method.
     * <br>This method calls http://api.twitter.com/1/geo/id/:id.json
     * @param id The ID of the location to query about.
     * @see <a href="http://dev.twitter.com/doc/get/geo/id/:place_id">GET geo/id/:place_id | dev.twitter.com</a>
     * @since Twitter4J 2.1.1
     */
    void getGeoDetails(String id,TwitterListener listener);
}
