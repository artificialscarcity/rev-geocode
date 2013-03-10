package reversegeocode2

@groovy.transform.EqualsAndHashCode

/**
 * Created with IntelliJ IDEA.
 * User: manticore
 * Date: 3/6/13
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
class GeoLocation {
    // These are not used until persistence
    // This may allow us to preserve the search term for
    //  an asynchronous auto-complete feature
    Double dblLatitude;
    Double dblLongitude;

    String latitude;
    String longitude;
    Boolean verified;
    String address;

    String toString() { "$address : ($dblLatitude,$dblLongitude)" }

    static final List<String> PROPERTIES = ['latitude', 'longitude',
            'verified', 'address'];

    public GeoLocation() {
    }
}
