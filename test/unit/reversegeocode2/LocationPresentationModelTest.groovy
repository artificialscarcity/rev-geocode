package reversegeocode2

/**
 * Created with IntelliJ IDEA.
 * User: manticore
 * Date: 3/8/13
 * Time: 8:56 AM
 * To change this template use File | Settings | File Templates.
 */
class LocationPresentationModelTest extends GroovyTestCase {
    void testUpdateLocation() {
        def model = new LocationPresentationModel()

        model.latitude = "40.12"
        model.longitude = "-88.16"
        model.verified = true
        model.address = "Fake Address"

        model.updateLocation()

        assert model.latitude == model.GeoLocation.latitude
        assert model.longitude == model.GeoLocation.longitude
        assert model.verified == model.GeoLocation.verified
        assert model.address == model.GeoLocation.address
        assert Double.parseDouble(model.latitude) == model.GeoLocation.dblLatitude
        assert Double.parseDouble(model.longitude) == model.GeoLocation.dblLongitude
    }
}
