package reversegeocode2

/**
 * Created with IntelliJ IDEA.
 * User: manticore
 * Date: 3/8/13
 * Time: 8:27 AM
 * To change this template use File | Settings | File Templates.
 */
class GeoLocationTest extends GroovyTestCase {
    void testValidator() {
        def goodValLat = [41.23, 44.25, 39.17, 40.56]
        def goodValLng = [-88.26, -90.3, -87.43, -88.17]

        def badValLat = [99.12, 41.14, "-44.-1", "-44.35"]
        def badValLng = [-88.87, -188, "-88.6", "-88.6.5"]

        def geoLoc = new GeoLocation()

        for (def i=0; i<=3; i++)
        {
            geoLoc.clsValidator.refreshValidation(goodValLat[i].toString(), goodValLng[i].toString())
            assert geoLoc.clsValidator.IS_VALID

            geoLoc.clsValidator.refreshValidation(badValLat[i].toString(), badValLng[i].toString())
            assert !geoLoc.clsValidator.IS_VALID
        }
    }

    void testGetDblLatitude() {
        def geoLoc = new GeoLocation()
        geoLoc.dblLatitude = 41.23
        assert geoLoc.getDblLatitude().class == Double
        assert geoLoc.dblLatitude == 41.23
    }
}
