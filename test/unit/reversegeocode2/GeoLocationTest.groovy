package reversegeocode2

/**
 * Created with IntelliJ IDEA.
 * User: manticore
 * Date: 3/8/13
 * Time: 8:27 AM
 * To change this template use File | Settings | File Templates.
 */
class GeoLocationTest extends GroovyTestCase {

    void testGetDblLatitude() {
        def geoLoc = new GeoLocation()
        geoLoc.dblLatitude = 41.23
        assert geoLoc.getDblLatitude().class == Double
        assert geoLoc.dblLatitude == 41.23
    }
}
