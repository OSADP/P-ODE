
'use strict';

angular.module('WestBoundCtrl', [])

.controller('WestBoundCtrl', ['$http', '$timeout', 'CurrentData', function( $http, $timeout, currentData ) {
    var controller = this;
    controller.data = {}

    currentData.getAllData().then(function( promise ) {
        var data = promise.data
        controller.data.travelTime = data.calculatedTravelTime
    })

    // 
    controller.getData = function() {
        controller.data = {}
        currentData.getAllDataParsed(['ritis', 'vdot', 'blufax', 'rtms', 'wxde', 'sdc']).then(function( response ) {
            var data = response.data,
                sources = response.sources

            angular.forEach(sources, function( source ) {
                controller.data[source] = {
                    speed: data[source].speedValue,
                    volume: data[source].volumeValue,
                    occupancy: data[source].occupancyValue,
                    travelTime: data[source].calculatedTravelTime,
                    bsm: response.bsm[source],
                    // Get incidents according to direction
                    incidents: (function( incidents ) {
                        if( incidents ) {
                            if( ! Array.isArray(incidents) ) {
                                incidents = new Array(incidents)
                            }
                            var actualIncidents = []
                            angular.forEach(incidents, function( incident ) {
                                    actualIncidents.push(incident.value);
                            })

                            return ( actualIncidents.length > 0 ) ? actualIncidents : undefined
                        }
                    })(data[source].currentIncidents)
                }
            })
        })
    }

    controller.refresh = function() {
        currentData.getAllData().then(function (promise) {
            var data = promise.data;
            controller.data.travelTime = data.calculatedTravelTime;
        })
        controller.getData();
    };

    controller.getData()
}])