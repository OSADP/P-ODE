'use strict';

angular.module('PodeService', [])
.service('CurrentData', ['$http', '$exceptionHandler', function( $http, $exceptionHandler ){
    /**
     * Web service address
     * @type {String}
     */
    var URL = '/ode-emulator-web/resources/getCurrentData'
    /**
     * Scope protection for itself
     * @type {Object}
     */
    var service = this

    /**
     * A raw unparsed request from URL service
     * @return {Object} promise
     */
    service.getAllData = function () {
        return $http.get(URL, function(promise) {
            return promise
        })
    }
    /**
     * Request data from URL and parse it according to sources
     * @param  {Array} sources A list of sources to parse
     * @return {Object} A parsed response
     */
    service.getAllDataParsed = function( sources ) {
        if( ! sources )
            return $exceptionHandler('Undefined Parameter:', 'Parameter "sources" is required.')
        else if ( ! Array.isArray(sources) )
            return $exceptionHandler('Invalid Parameter Type:', 'Parameter "sources" should be an array.')
        else
            return service.getAllData().then(function(promise) {
                var data = promise.data
                var array = []
                // TODO: BSM data
                var bsm = []
                angular.forEach( sources, function( source ) {
                    array[source] = []
                    var _isEast = false
                    var value = {}
                    var obj = []
                    if( source )
                    for( var datum in data ) {
                        if (datum === 'basicSafetyMessages') {
                            var msgs = data[datum];
                            angular.forEach(msgs, function (msg) {
                                if (msg.source === source) {
                                    bsm[source] = msg;
                                }
                            });
                        } else {
                            var contents = data[datum]
                            if (contents != null) {
                                if (!Array.isArray(contents)) contents = new Array(contents)
                                angular.forEach(contents, function (content) {
                                    if (content.source) {
                                        // TODO: BSM data
                                        if (content.source === source.toLowerCase()) {
                                            if (obj[datum] === undefined)
                                                obj[datum] = {
                                                    value: content.dataValue,
                                                    type: content.dataType
                                                }
                                            else {
                                                if (!Array.isArray(obj[datum])) {
                                                    obj[datum] = new Array(obj[datum])
                                                }
                                                obj[datum].push({
                                                    value: content.dataValue,
                                                    type: content.dataType
                                                })
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                    array[source]= obj
                })
                return {
                    data: array,
                    sources: sources,
                    // TODO: BSM data
                    bsm: bsm
                }
            })
    }

    service.getData = function (source) {
        return service.getAllData().then(function(promise) {
            var data = promise.data
            var obj = []
            var array = []
            array[source] = []
            var _isEast = false
            var value = {}
            if( source )
            for( var datum in data ) {
                var contents = data[datum]
                if( contents != null ) {
                    if( ! Array.isArray( contents ) ) contents = new Array(contents)
                    angular.forEach(contents, function( content ) {
                        if( content.source )
                            if(content.source === source.toLowerCase()) {
                                obj[datum] = content.dataValue
                            }
                        
                    })
                }
            }
            array[source]= obj
            return array
        })
    }
}])