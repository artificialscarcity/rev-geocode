package reversegeocode2

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

application(title: 'ReverseGeocode',
        preferredSize: [650, 225],
        pack: true,
        //location: [50,50],
        locationByPlatform: true,
        iconImage:   imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {
    // add content here
    migLayout(layoutConstraints: 'fill')
    panel(constraints: 'center', border: titledBorder(title: 'Coordinates')) {
        migLayout(layoutConstraints: 'fill')
        for(propName in GeoLocation.PROPERTIES) {
            if (propName.substring(0,1) == "l") {
                label(text: GriffonNameUtils.getNaturalName(propName) + ': ',
                        constraints: 'right')
                textField(columns: 15, constraints: 'grow, wrap',
                        text: bind(propName, source: model.currentLocation,
                                mutual: true))
            }
        }
        label(text: 'Verified:', constraints: 'right')
        textField(columns: 5, constraints: 'grow,wrap',
              text: bind('verified', source: model.currentLocation,
                    mutual: false))
    }
    panel(constraints: 'center', border: titledBorder(title:'Address')) {
        migLayout(layoutConstraints: 'fill')
        textArea(columns: 30, rows: 4, constraints: 'wrap',
                text: bind('address', source: model.currentLocation,
                        mutual: false))
        button("Search", actionPerformed:model.&retrieveJSON, constraints: 'SOUTH',
                enabled: bind {model.currentLocation.verfied})
    }
}
