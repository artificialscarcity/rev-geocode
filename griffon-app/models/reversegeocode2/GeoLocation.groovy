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

    Double dblLatitude;
    Double dblLongitude;

    String latitude;
    String longitude;
    Boolean verified;
    String address;

    CoordinateValidator clsValidator;

    String toString() { "$address : ($dblLatitude,$dblLongitude)" }

    static final List<String> PROPERTIES = ['latitude', 'longitude',
            'verified', 'address'];

    public GeoLocation() {
        clsValidator = new CoordinateValidator();
    }

    public class CoordinateValidator {
        // Accepted value ranges
        private static final MIN_LATITUDE = -90;
        private static final MAX_LATITUDE = 90;
        private static final MIN_LONGITUDE = -180;
        private static final MAX_LONGITUDE = 180;

        public Boolean IS_VALID;

        public CoordinateValidator() {
            refreshValidation(latitude, longitude);
        }

        public void refreshValidation(String lat, String lng) {
            if (isValidLatitude(lat) && isValidLongitude(lng)) IS_VALID = true else IS_VALID = false;
        }

        private Boolean isValidLatitude(String lat) {
            if (lat == null) return true;
            if (lat == "") return true;
            if (lat.matches(DecimalCoordinate.DECIMAL_PATTERN)) {}
            else if (lat.matches(SexagesimalCoordinate.SEXAGESIMAL_PATTERN)) {
                lat = SexagesimalCoordinate.convertToDecimal(lat);
                if (lat.matches(DecimalCoordinate.DECIMAL_PATTERN)) {} else return false;
            }
            else return false;

            if (lat == "-") {lat = "-0.000001"}
            if (MIN_LATITUDE < Double.parseDouble(lat)
                    && Double.parseDouble(lat) < MAX_LATITUDE) return true;
        }

        private Boolean isValidLongitude(String lng) {
            if (lng == null) return true;
            if (lng == "") return true;
            if (lng.matches(DecimalCoordinate.DECIMAL_PATTERN)){}
            else if (lng.matches(SexagesimalCoordinate.SEXAGESIMAL_PATTERN)) {
                lng = SexagesimalCoordinate.convertToDecimal(lng);
                if (lng.matches(DecimalCoordinate.DECIMAL_PATTERN)) {} else return false;
            }
            else return false;

            if (lng == "-") lng = "-0.000001";
            if (MIN_LONGITUDE < Double.parseDouble(lng)
                    && Double.parseDouble(lng) < MAX_LONGITUDE) return true;
        }

        class DecimalCoordinate {
            public static final DECIMAL_PATTERN = "(|\\-)\\d{0,3}(|\\.\\d{0,6})";

            static String convertToSexagesimal(String theCoordinate) {

            }
        }

        class SexagesimalCoordinate {
            public static final SEXAGESIMAL_PATTERN = "\\d{1,3}(D|\\s)\\d{1,2}(M|\\s)\\d{1,2}(S|\\s)";

            static String convertToDecimal(String theCoordinate) {
                def coorArray = theCoordinate.split("\\D")
                def sexagesimalCrd
                if (coorArray.length > 1) {
                    sexagesimalCrd = coorArray[0] + "." + (coorArray[1] / 60)
                        if (coorArray.length == 3) sexagesimalCrd += (coorArray[2] / 3600)
                } else sexagesimalCrd = coorArray[0]

                return sexagesimalCrd
            }
        }
    }
}
