application {
    title = 'ReverseGeocode2'
    startupGroups = ['reverseGeocode2']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "reverseGeocode2"
    'reverseGeocode2' {
        model      = 'reversegeocode2.ReverseGeocode2Model'
        view       = 'reversegeocode2.ReverseGeocode2View'
        controller = 'reversegeocode2.ReverseGeocode2Controller'
    }

}
