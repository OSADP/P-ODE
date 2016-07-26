
'use strict';

angular.module('EastBoundCtrl', [])

.controller('EastBoundCtrl', ['$http', '$timeout', 'CurrentData', function( $http, $timeout, currentData ) {
    var controller = this;
    controller.data = {}

    currentData.getAllData().then(function( promise ) {
        var data = promise.data
        controller.data.travelTime = data.eastBoundTravelTime
    })

    controller.getData = function() {
        controller.data = {}
        currentData.getAllDataParsed(['ritis', 'vdot', 'blufax', 'rtms', 'wxde']).then(function( response ) {
            var data = response.data,
                sources = response.sources

            angular.forEach(sources, function( source ) {
                controller.data[source] = {
                    speed: data[source].speedEastValue,
                    volume: data[source].volumeEastValue,
                    occupancy: data[source].occupancyEastValue,
                    travelTime: data[source].travelTimeEast,
                    // Get incidents according to direction
                    incidents: (function( incidents ) {
                        if( incidents ) {
                            if( ! Array.isArray(incidents) ) {
                                incidents = new Array(incidents)
                            }
                            var actualIncidents = []
                            angular.forEach(incidents, function( incident ) {
                                if( incident.value.direction === 'east' ) {
                                    actualIncidents.push(incident.value);
                                }
                            })

                            return ( actualIncidents.length > 0 ) ? actualIncidents : undefined
                        }
                    })(data[source].currentIncidents)
                }
            })
        })
    }

    controller.getData()
}])