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

    void testValidator() {
        def goodValLat = [41.23, 44.25, 39.17, 40.56]
        def goodValLng = [-88.26, -90.3, -87.43, -88.17]

        def badValLat = [99.12, 41.14, "-44.-1", "-44.35"]
        def badValLng = [-88.87, -188, "-88.6", "-88.6.5"]

        def model = new LocationPresentationModel()

        for (def i=0; i<=3; i++)
        {
            model.clsValidator.refreshValidation(goodValLat[i].toString(), goodValLng[i].toString())
            assert model.clsValidator.IS_VALID

            model.clsValidator.refreshValidation(badValLat[i].toString(), badValLng[i].toString())
            assert !model.clsValidator.IS_VALID
        }
    }

    void testCharValidator() {
        def model = new LocationPresentationModel()

        def testLat = [40.1245, 18.2468, -65.8793]
        def testLng = [-88.1846, 37.5246, -91.4581]
        def testStr

        for (int i=0; i<=2; i++) {
            testStr = ""
            for (char c in testLat[i].toString().toCharArray()) {
                testStr += c
                assert model.chrValidator.isCharacterValid(testStr, 'latitude')
            }
            testStr = ""
            for (char c in testLng[i].toString().toCharArray()) {
                testStr += c
                assert model.chrValidator.isCharacterValid(testStr, 'longitude')
            }
        }

        testLat = ["40 35'55\" N", "37 11'33\" N", "11 25'23\" S"]
        testLng = ["111 32'14\" W", "25 11'54\" E", "88 5'13\" W"]

        for (int i=0; i<=2; i++) {
            testStr = ""
            for (char c in testLat[i].toString().toCharArray()) {
                testStr += c
                assert model.chrValidator.isCharacterValid(testStr, 'latitude')
            }
            testStr = ""
            for (char c in testLng[i].toString().toCharArray()) {
                testStr += c
                assert model.chrValidator.isCharacterValid(testStr, 'longitude')
            }
        }
    }
}
