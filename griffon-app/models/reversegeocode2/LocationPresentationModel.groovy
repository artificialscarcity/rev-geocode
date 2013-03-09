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

    CoordinateValidator clsValidator = new CoordinateValidator();
    CharValidator chrValidator = new CharValidator();

    private String viewMask = "";

    public enum MaskCommand {
        PUSH_RESTRICTIONS,
        ELIM_DECIMAL, ELIM_SEXAGES, ELIM_OPERATOR, ELIM_ORDINAL,
        WEAK_SEXAOPER, WEAK_DECIOPER
    }

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

        // Direction format byte map
        public enum GeoDirection {
            GEO_OPERATOR, GEO_ORDINAL
        }
        private static final Map<GeoDirection, Byte> geoDirectionByteMap = new HashMap<GeoDirection, Byte>() {{
            put(GeoDirection.GEO_OPERATOR, 10.byteValue());
            put(GeoDirection.GEO_ORDINAL, 11.byteValue());
        }}

        // Enum sets for managing possibilities
        public EnumSet<GeoFormat> geoFormats;
        public EnumSet<GeoDirection> geoDirections;

        public CoordinateValidator() {
            refreshValidation(latitude, longitude);
        }

        public void getCoordinateType() {
            def tmpFrm = geoFormats;
            def tmpDir = geoDirections;
            if ( tmpFrm == null || tmpDir == null) return;
            if ( tmpFrm.size() == 1 && tmpDir.size() == 1 ) {

                if ((this.geoFormatByteMap.get(tmpFrm.first()) && this.geoFormatByteMap.get(tmpFrm.first())) == 01) {

                } else if ((this.geoFormatByteMap.get(tmpFrm.first()) || this.geoFormatByteMap.get(tmpFrm.first())) == 10) {
                    println();
                } else if (this.geoFormatByteMap.get(tmpFrm.first()) == 00) {
                    println();
                } else {
                    println();
                }
            }
        }

        public void alterPossibleSet(MaskCommand theCmd) {
            if (theCmd == MaskCommand.PUSH_RESTRICTIONS) return;

            if (theCmd == MaskCommand.ELIM_DECIMAL) {
                geoFormats = EnumSet.of(GeoFormat.GEO_SEXAGES)
            } else if (theCmd == MaskCommand.ELIM_SEXAGES) {
                geoFormats = EnumSet.of(GeoFormat.GEO_DECIMAL)
            } else if (theCmd == MaskCommand.WEAK_DECIOPER) {
                geoFormats = EnumSet.of(GeoFormat.GEO_DECIMAL)
                geoDirections = EnumSet.of(GeoDirection.GEO_OPERATOR)
            } else if (theCmd == MaskCommand.WEAK_SEXAOPER) {
                geoFormats = EnumSet.of(GeoFormat.GEO_SEXAGES)
                geoDirections = EnumSet.of(GeoDirection.GEO_OPERATOR)
            } else if (theCmd == MaskCommand.ELIM_OPERATOR) {
                geoDirections = EnumSet.of(GeoDirection.GEO_ORDINAL)
            }
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
                if (newVal.length() == 3) {
                    if (["D", "d", "\\s"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.ELIM_DECIMAL)
                    if (testChar == ".") clsValidator.alterPossibleSet(MaskCommand.ELIM_SEXAGES)
                    if (testChar.isDigit()) clsValidator.alterPossibleSet(MaskCommand.WEAK_SEXAOPER)
                }
                if (newVal.length() == 4) {
                    if (["D", "d", "\\s"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.WEAK_SEXAOPER)
                    if (testChar == ".") clsValidator.alterPossibleSet(MaskCommand.WEAK_DECIOPER)
                }
                if (newVal.length() == 6) {
                    if (["'", "M", "m"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.ELIM_DECIMAL)
                }
                if (newVal.length() == 7) {
                    if (["'", "M", "m"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.WEAK_SEXAOPER)
                }
                if (newVal.length() == 9) {
                    if (["\"", "S", "s"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.ELIM_DECIMAL)
                }
                if (newVal.length() == 10) {
                    if (testChar == "\\s") clsValidator.alterPossibleSet(MaskCommand.ELIM_OPERATOR)
                    if (["\"", "S", "s"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.WEAK_SEXAOPER)
                    if (testChar.isDigit()) clsValidator.alterPossibleSet(MaskCommand.WEAK_DECIOPER)
                }
                if (newVal.length() == 11) {
                    if (!(testChar == "-")) clsValidator.alterPossibleSet(MaskCommand.ELIM_OPERATOR)
                    else return; // SPECIALIZED CALL NEEDED HERE
                }

                return true;
            }
            else return false;
        }
    }
}
