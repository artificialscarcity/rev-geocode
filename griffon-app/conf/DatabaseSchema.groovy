builder.database(name: "srchcache") {
    table(name: "geoloc") {
        column(name: "id", type: "integer", required: true)
        column(name: "dblLatitude", type: "double", required: true)
        column(name: "dblLongitude", type: "double", required: true)
        column(name: "address", type: "varchar", size: 100, required: false)
    }
}
