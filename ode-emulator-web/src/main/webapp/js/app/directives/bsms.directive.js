'use strict';

angular.module('BsmAgent', [])
.directive('bsmAgent', function () {
       return {
           restrict: 'A',
           scope: {
               data: '='
           },
           template: '<div class="form-group clearfix">\
                        <div class="col-md-4 sage">\
                          <h5>SDC</h5>\
                        </div>\
                        <div class="col-md-8">\
                        <div ng-class="{ darken: data.sdc.bsm.dataValue == undefined }">\
                          <strong>BSM Speed:</strong>\
                          <span id="sdc-bsm" ng-bind="data.sdc.bsm.dataValue ? data.sdc.bsm.dataValue + \' mph\' : \'No Data\'"></span>\
                        </div>\
                      </div>\
                    </div>'
       }
    });