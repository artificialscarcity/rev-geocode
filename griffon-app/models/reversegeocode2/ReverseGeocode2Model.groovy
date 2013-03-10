package reversegeocode2

import groovy.beans.Bindable
import griffon.transform.PropertyListener
import groovy.json.JsonSlurper

import java.awt.event.ActionEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.regex.Pattern

class ReverseGeocode2Model {

    final LocationPresentationModel currentLocation = new LocationPresentationModel();

    ReverseGeocode2Model() {
        currentLocation.addPropertyChangeListener(new ModelUpdater())
    }

    private class ModelUpdater implements PropertyChangeListener {
        void propertyChange(PropertyChangeEvent e) {
            if (e.propertyName == 'verified') return;
            if (e.propertyName == 'latitude' || e.propertyName == 'longitude') {
                if (currentLocation.chrValidator.isCharacterValid(e.newValue, e.propertyName))
                {
                    currentLocation[e.propertyName] = e.newValue
                    currentLocation.clsValidator.getCoordinateType()
                    currentLocation['verified'] = true
                    // Ignore if property name is verified or address because this will cause another
                    //      PropertyChangeEvent; how might we be able to determine the source of the call
                    //if (e.propertyName != 'verified' && e.propertyName != 'address') {
                    //        currentLocation['verified'] = IsValidCoordinateSet()
                    //}
                }
                else {
                    println("bad char");
                    currentLocation['verified'] = false;
                }
            }
            else if (e.propertyName == 'address') {
                def testArr = e.newValue.toString().split("\n")
                if (testArr.length > 1) return;

                def addressArr = e.newValue.toString().split(",")
                def newAddress = addressArr[0] + "\n"
                    newAddress += addressArr[1].substring(1, addressArr[1].length())
                    newAddress += ","
                    newAddress += addressArr[2]
                    newAddress += ","
                    newAddress += addressArr[3]
                currentLocation['address'] = newAddress
            }
        }
    }

    Boolean IsValidCoordinateSet() {
        currentLocation.clsValidator.refreshValidation(currentLocation.latitude, currentLocation.longitude);
        // This verified variable is functionally useless, but allows for preview and debugging
        currentLocation['verified'] = currentLocation.clsValidator.IS_VALID;
        if (currentLocation['verified']) {
            if (currentLocation['address'] == "Invalid coordinates.") currentLocation['address'] = "";
            return true;
        }
        else currentLocation['address'] = "Invalid coordinates."; return false;
    }

    private void retrieveJSON(ActionEvent event = null) {
        currentLocation.clsValidator.refreshValidation(currentLocation.latitude, currentLocation.longitude)
        // If it's not a good coordinate, don't waste a request
        if (!currentLocation.clsValidator.IS_VALID) return;
        def theUrl = "http://maps.googleapis.com/maps/api/geocode/json" +
                            "?latlng=${currentLocation.latitude},${currentLocation.longitude}" +
                            "&sensor=false";            // HTTP Builder may offer a more safe and easy solution

        def theAddress = getResponse(theUrl);
        if (!(theAddress == null)) {
            currentLocation['address'] = theAddress.toString();
            // We were successful in retrieving an address, update the object
            //      to prepare for a cache/database commit
            currentLocation.updateLocation();
        }
        else currentLocation['address'] = "The address is not valid.";
    }

    // Retrieve and parse to response
    // Parse is extremely simple due to nature of required data
    private String getResponse(String theUrl) {
        def theResponse = new URL(theUrl)
        def decodeJSONResponse = new JsonSlurper().parseText(theResponse.getText())
        return decodeJSONResponse.results.formatted_address[0]
    }
}