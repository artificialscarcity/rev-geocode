package reversegeocode2

import groovy.beans.Bindable
import griffon.transform.PropertyListener
import groovy.json.JsonSlurper

import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.regex.Pattern

//@PropertyListener(selectionUpdater)

class ReverseGeocode2Model {

    final LocationPresentationModel currentLocation = new LocationPresentationModel();

    ReverseGeocode2Model() {
        currentLocation.addPropertyChangeListener(new ModelUpdater())
    }

    private class ModelUpdater implements PropertyChangeListener {
        void propertyChange(PropertyChangeEvent e) {
            currentLocation[e.propertyName] = e.newValue
            if (e.propertyName != 'verified' && e.propertyName != 'address') {
                    currentLocation['verified'] = IsValidCoordinateSet()
            }
        }
    }

    Boolean IsValidCoordinateSet() {
        currentLocation.GeoLocation.clsValidator.refreshValidation(currentLocation.latitude, currentLocation.longitude);
        currentLocation['verified'] = currentLocation.GeoLocation.clsValidator.IS_VALID;
        if (currentLocation['verified']) {
            if (currentLocation['address'] == "Invalid coordinates.") currentLocation['address'] = "";
            return true;
        }
        else currentLocation['address'] = "Invalid coordinates."; return false;
    }

    private void retrieveJSON(ActionEvent event = null) {
        if (!currentLocation.verified) return;
        def theUrl = "http://maps.googleapis.com/maps/api/geocode/json" +
                            "?latlng=${currentLocation.latitude},${currentLocation.longitude}" +
                            "&sensor=false";

        def theAddress = getResponse(theUrl);
        if (!(theAddress == null)) {
            currentLocation['address'] = theAddress.toString();
            currentLocation.updateLocation();
        }
        else currentLocation['address'] = "The address is not valid.";
    }

    private String getResponse(String theUrl) {
        def theResponse = new URL(theUrl)
        def decodeJSONResponse = new JsonSlurper().parseText(theResponse.getText())
        return decodeJSONResponse.results.formatted_address[0]
    }
}