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

    // Internal class for verifying coordinate data
    public class CoordinateValidator {
        public Boolean IS_VALID;

        // Accepted value ranges
        private static final MIN_LATITUDE = -90;
        private static final MAX_LATITUDE = 90;
        private static final MIN_LONGITUDE = -180;
        private static final MAX_LONGITUDE = 180;

        // Number format byte map
        public enum GeoFormat {
            GEO_SEXAGES, GEO_DECIMAL,
        }
        private Map<GeoFormat, Byte> geoFormatByteMap = new HashMap<GeoFormat, Byte>() {{
            put(GeoFormat.GEO_SEXAGES, 00.byteValue());
            put(GeoFormat.GEO_DECIMAL, 01.byteValue());
        }}
        def geoFormat = Arrays.asList(GeoDirection.findAll().toArray());

        // Direction format byte map
        public enum GeoDirection {
            GEO_OPERATOR, GEO_ORDINAL
        }
        private static final Map<GeoDirection, Byte> geoDirectionByteMap = new HashMap<GeoDirection, Byte>() {{
            put(GeoDirection.GEO_OPERATOR, 10.byteValue());
            put(GeoDirection.GEO_ORDINAL, 11.byteValue());
        }}
        def geoDirection = Arrays.asList(GeoDirection.findAll().toArray());

        public CoordinateValidator() {
            refreshValidation(latitude, longitude);
        }

        public void getCoordinateType() {
            def tmpFrm = geoFormat;
            def tmpDir = geoDirection;
            if ( tmpFrm.size() == 1 && tmpDir.size() == 1 ) {

                if ((this.geoFormatByteMap.get(tmpFrm.first()) && this.geoFormatByteMap.get(tmpFrm.first())) == 01) {
                    println();
                } else if ((this.GeoFormat || this.GeoFormat) == 10) {
                    println();
                } else if (this.GeoFormat == 00) {
                    println();
                } else {
                    println();
                }
            }
        }

        public void alterPossibleSet() {

        }

        public void refreshValidation(String lat, String lng) {
            getCoordinateType();
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
                // THIS ISN'T NEEDED AS THE APPLICATION STANDS
                // However, it may be useful once the text field is
                // a bit smarter (can detect coordinate format) for use
                // with an auto-complete style function
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

    public class CharValidator {
        private static final List<Integer> uniqueSet = Arrays.asList( 1, 9, 10, 11 );
        private static final List<Integer> numericSet = Arrays.asList( 2, 5, 8 );
        private static final List<Integer> numDegSet = Arrays.asList( 3, 4 );
        private static final List<Integer> numMinSet = Arrays.asList( 6, 7 );

        private static final Map<Integer, String> uniqueValidationKeys = new HashMap<Integer, String>() {{
            put(1, "(-|[0-9])");
            put(9, "(\\|S|s|[0-9])");
            put(10, "(\\s|\\|S|s|[0-9])");
            put(11, "(N|n|S|s|\\-)");
        }}

        private static final String numericMask = "([0-9])";
        private static final String numDegMask = "(D|d|.|[0-9])";
        private static final String numMinMask = "(\\'|M|m|[0-9])";

        CharValidator() {

        }

        public Boolean isCharacterValid(String newVal) {
            if (newVal.length() == 0) return true;
            def testChar = newVal.charAt(newVal.length() - 1);
            def regexMask;

            if (uniqueSet.contains(newVal.length())) {
                regexMask = uniqueValidationKeys.get(newVal.length())
            }   else if (numericSet.contains(newVal.length())) {
                regexMask = numericMask
            }   else if (numDegSet.contains(newVal.length())) {
                regexMask = numDegMask
            }   else if (numMinSet.contains(newVal.length())) {
                regexMask = numMinMask
            }   else {
                // WHY ARE WE HERE?
            }

            if (testChar.toString().matches(regexMask.toString())) {
                return true;
            }
            else return false;
        }
    }
}
