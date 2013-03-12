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
    private Queue<Character> latDegVal
    private Queue<Character> lngDegVal

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
        public static final MIN_LATITUDE = -90;
        public static final MAX_LATITUDE = 90;
        public static final MIN_LONGITUDE = -180;
        public static final MAX_LONGITUDE = 180;



        // Number format byte map
        public enum GeoFormat {
            GEO_SEXAGES, GEO_DECIMAL,
        }
        private final Map<GeoFormat, Byte> geoFormatByteMap = new HashMap<GeoFormat, Byte>() {{
            put(GeoFormat.GEO_SEXAGES, 00.byteValue());
            put(GeoFormat.GEO_DECIMAL, 01.byteValue());
        }}

        // Direction format byte map
        public enum GeoDirection {
            GEO_OPERATOR, GEO_ORDINAL
        }
        private final Map<GeoDirection, Byte> geoDirectionByteMap = new HashMap<GeoDirection, Byte>() {{
            put(GeoDirection.GEO_OPERATOR, 10.byteValue());
            put(GeoDirection.GEO_ORDINAL, 11.byteValue());
        }}

        // Enum sets for managing possibilities
        public EnumSet<GeoFormat> geoFormats;
        public EnumSet<GeoDirection> geoDirections;

        public CoordinateValidator() {
            refreshValidation();
            geoFormats = EnumSet.of(GeoFormat.GEO_DECIMAL, GeoFormat.GEO_SEXAGES);
            geoDirections = EnumSet.of(GeoDirection.GEO_OPERATOR, GeoDirection.GEO_ORDINAL)
        }

        // The following two methods (getCoordinateType and alterPossibleSet need significant work
        // they receive the state change from the character validator and switches it's mask accordingly
        //  We need some sort of fuzzy validation if the coordinate could be of multiple
        //  formats. Still can't get key listeners working, but they would help greatly...
        public void getCoordinateType() {
            def tmpFrm = geoFormats;
            def tmpDir = geoDirections;
            if ( tmpFrm == null || tmpDir == null) return;
            if ( tmpFrm.size() == 1 && tmpDir.size() == 1 ) {

                if ((this.geoFormatByteMap.get(tmpFrm.first()) && this.geoFormatByteMap.get(tmpFrm.first())) == 01) {
                    println(1);
                } else if ((this.geoFormatByteMap.get(tmpFrm.first()) || this.geoFormatByteMap.get(tmpFrm.first())) == 10) {
                    println(2);
                } else if (this.geoFormatByteMap.get(tmpFrm.first()) == 00) {
                    println(3);
                } else {
                    println(4);
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

        public void refreshValidation() {
            getCoordinateType();
            IS_VALID = (isValidLatitude(latitude) && isValidLongitude(longitude))
        }

        private Boolean isValidLatitude(String lat) {
            // It may be effective to make this method return a
            // three-way switch, where the null state implies the
            // validator couldn't yet make a decision
            if (lat == null) return true;
            if (lat == "-") return true;
            if (lat == "") return true;
            if (lat.matches(SexagesimalCoordinate.SEXAGESIMAL_PATTERN)) {
                latitude = SexagesimalCoordinate.convertToDecimal(lat);
                if (!(latitude.matches(DecimalCoordinate.DECIMAL_PATTERN))) {
                    return false
                }
            }
            if (Double.parseDouble(latitude) < MAX_LATITUDE && MIN_LATITUDE < Double.parseDouble(latitude)) return true;
            return false;
        }

        private Boolean isValidLongitude(String lng) {
            if (lng == null) return true;
            if (lng == "-") return true;
            if (lng == "") return true;
            if (lng.matches(SexagesimalCoordinate.SEXAGESIMAL_PATTERN)) {
                longitude = SexagesimalCoordinate.convertToDecimal(lng);
                if (!longitude.matches(DecimalCoordinate.DECIMAL_PATTERN)) {
                    return false;
                }
            }
            if (Double.parseDouble(longitude) < MAX_LONGITUDE && MIN_LONGITUDE < Double.parseDouble(longitude)) return true;
            return false;
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
            public static final SEXAGESIMAL_PATTERN = "\\d{1,3}(D|\\s)\\d{1,2}(M|m|\'|\\s)\\d{1,2}(S|s|\"|\\s)\\s(N|n|S|s|E|e|W|)";

            static String convertToDecimal(String theCoordinate) {
                def coorArray = theCoordinate.split("\\D")
                def ordDirect = theCoordinate.substring(theCoordinate.length() - 1)
                def sexagesimalCrd
                if (coorArray.length > 1) {
                    sexagesimalCrd = coorArray[0].toString() + "."
                    // Need to test minute and seconds ranges
                    def deciCrd = (Integer.parseInt(coorArray[1]) / 60 ) + (Integer.parseInt(coorArray[2]) / 3600)
                    if (coorArray.length == 3) {
                        if (deciCrd.toString().length() > 8) sexagesimalCrd += deciCrd.toString().substring(3, 9)
                        else sexagesimalCrd += deciCrd.toString().substring(3, deciCrd.toString().length())
                    }
                } else sexagesimalCrd = coorArray[0]
                if (ordDirect.matches("(W|w|S|s)")) {
                    sexagesimalCrd = Double.parseDouble(sexagesimalCrd) * -1
                }
                return sexagesimalCrd
            }
        }
    }

    public class CharValidator {
        private static final List<Character> ACPT_DELIMITERS = ["\\s", "\'", "\"", ".", "D", "d", "M", "m", "S", "s" ];
        private static final List<Character> ACPT_VECTORCHARS = ["N", "n", "S", "s", "E", "e", "W", "w", "+", "-"];

        private enum CharacterState {
            UNDEF, DEGREES, MINUTES, SECONDS, DECIMAL
        }
        public CharacterState CHAR_STATE;
        //private static final List<Integer> uniqueSet = Arrays.asList( 1, 10, 11, 12 );
        //private static final List<Integer> numDegSet = Arrays.asList( 2, 3, 4 );
        //private static final List<Integer> numMinSet = Arrays.asList( 5, 6, 7 );
        //private static final List<Integer> numSecSet = Arrays.asList( 8, 9 );

        //private static final Map<Integer, String> uniqueValidationKeys = new HashMap<Integer, String>() {{
        //    put(1, "(-|[0-9])");
        //    put(10, "(\\s|\"|S|s|N|n|W|w|E|e[0-9])");
        //    put(11, "(\\s|N|n|S|s|W|w|E|e|\\-)");
        //    put(12, "(N|n|S|s|W|w|E|e|\\-)");
        //}}

        //private static final String numDegMask = "(\\s|D|d|.|[0-9])";
        //private static final String numMinMask = "(\\'|M|m|[0-9])";
        //private static final String numSecMask = "(\"|\\s|S|s|[0-9])"

        CharValidator() {
            CHAR_STATE = CharacterState.DEGREES;
        }

        public Boolean isCharacterValid(String newVal, String prop) {
            if (newVal.length() == 0) return true;
            def testChar = newVal.charAt(newVal.length() - 1);
            //def regexMask;

            if (ACPT_DELIMITERS.contains(testChar)) {

            }
            else if (ACPT_VECTORCHARS.contains(testChar)) {

            }
            else if (testChar.isDigit()) {

            }
            else {
                //WHY ARE WE HERE?
            }


            //if (uniqueSet.contains(newVal.length())) {
            //    regexMask = uniqueValidationKeys.get(newVal.length())
            //}   else if (numSecSet.contains(newVal.length())) {
            //    regexMask = numSecMask
            //}   else if (numDegSet.contains(newVal.length())) {
            //    regexMask = numDegMask
            //}   else if (numMinSet.contains(newVal.length())) {
            //    regexMask = numMinMask
            //}   else {
                // WHY ARE WE HERE?
            //    regexMask = ""
            //}

            // This length testing fall-through is bulky and difficult to read
            // Is there a way to simplify it?
            //if (testChar.toString().matches(regexMask.toString())) {
            //    if (newVal == "-") clsValidator.alterPossibleSet(MaskCommand.ELIM_ORDINAL)
            //    else if (newVal.length() == 2) {
            //        if (testChar == ".") {
            //            clsValidator.alterPossibleSet(MaskCommand.ELIM_SEXAGES)
            //            if (newVal.substring(1,1) == "-") clsValidator.alterPossibleSet(MaskCommand.ELIM_ORDINAL)
            //        }
            //    }
            //    else if (newVal.length() == 3) {
            //        if (["D", "d", "\\s"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.ELIM_DECIMAL)
            //        if (testChar == ".") {
            //            clsValidator.alterPossibleSet(MaskCommand.ELIM_SEXAGES)
            //            if (newVal.substring(1,1) == "-") clsValidator.alterPossibleSet(MaskCommand.ELIM_ORDINAL)
            //        }
            //        if (testChar.isDigit()) {
            //            if (prop == "latitude") clsValidator.alterPossibleSet(MaskCommand.WEAK_SEXAOPER)
            //            else clsValidator.alterPossibleSet(MaskCommand.PUSH_RESTRICTIONS)
            //        }
            //    }
            //    else if (newVal.length() == 4) {
            //        if (["D", "d", "\\s"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.WEAK_SEXAOPER)
            //        if (testChar == ".") clsValidator.alterPossibleSet(MaskCommand.WEAK_DECIOPER)
            //    }
            //    else if (newVal.length() == 6) {
            //        if (["'", "M", "m"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.ELIM_DECIMAL)
            //    }
            //    else if (newVal.length() == 7) {
            //        if (["'", "M", "m"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.WEAK_SEXAOPER)
            //    }
            //    else if (newVal.length() == 9) {
            //        if (["\"", "S", "s"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.ELIM_DECIMAL)
            //    }
            //    else if (newVal.length() == 10) {
            //        if (testChar == "\\s") clsValidator.alterPossibleSet(MaskCommand.ELIM_OPERATOR)
            //        if (["\"", "S", "s"].contains(testChar)) clsValidator.alterPossibleSet(MaskCommand.WEAK_SEXAOPER)
            //        if (testChar.isDigit()) clsValidator.alterPossibleSet(MaskCommand.WEAK_DECIOPER)
            //    }
            //    else if (newVal.length() == 11) {
            //        if (!(testChar == "-")) clsValidator.alterPossibleSet(MaskCommand.ELIM_OPERATOR)
            //        else return; // SPECIALIZED CALL NEEDED HERE
            //    }

            //    return true;
            //}
            //else return false;
        }
    }
}
