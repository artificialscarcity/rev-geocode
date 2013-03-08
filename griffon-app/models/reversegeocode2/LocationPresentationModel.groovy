package reversegeocode2

import groovy.beans.Bindable

/**
 * Created with IntelliJ IDEA.
 * User: manticore
 * Date: 3/6/13
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
class LocationPresentationModel {
    @Bindable String latitude;
    @Bindable String longitude;
    @Bindable Boolean verified;
    @Bindable String address;

    @Bindable GeoLocation = new GeoLocation();

    private propertyUpdater = { e ->
        // ??
        if (e.propertyName == 'GeoLocation') {
            for(property in GeoLocation.PROPERTIES) {
                def bean = e.newValue
                delegate[property] = bean != null ? bean[property] : null
            }
        }
    }

    void updateLocation() {
        if (GeoLocation) {
            for(property in GeoLocation.PROPERTIES) {
                GeoLocation[property] = this[property];
            }

            // Commit the acceptable lat/lng values to the underlying object
            //      to prepare for cache
            GeoLocation['dblLatitude'] = Double.parseDouble(this['latitude'].toString())
            GeoLocation['dblLongitude'] = Double.parseDouble(this['longitude'].toString())
        }
    }
}
