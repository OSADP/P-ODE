'use strict';

angular.module('AgentDirective', [])

.directive('regularAgent', function() {
  return {
    restrict: 'A',
    scope: {
      title: '@',
            color: '@',
            data: '='
    },
    template: '<div class="form-group clearfix">\
                <div class="col-md-4" ng-class="color">\
                    <h5>\
                        <span data-ng-bind="title"></span>\
                        <span class="glyphicon glyphicon-exclamation-sign btn-icon" ng-show="data.incidents"\
                            title="Incident Data Available"\
                            ng-class="{ open: clicked }"\
                            ng-click="toggleModal()"></span>\
                    </h5>\
                </div>\
                <div class="col-md-8">\
                    <div ng-class="{ darken: data.volume.value == undefined || data.volume.value === \'null\' }">\
                        <strong>Volume:</strong>\
                        <span ng-bind="data.volume.value && data.volume.value != \'null\' ? data.volume.value + \' vehicles\' : \'No Data\'"></span>\
                    </div>\
                    <div class="even" ng-class="{ darken: data.occupancy.value == undefined || data.occupancy.value === \'null\' }">\
                        <strong>Occupancy:</strong>\
                        <span ng-bind="data.occupancy.value && data.occupancy.value != \'null\' ? data.occupancy.value + \'%\' : \'No Data\'"></span>\
                    </div>\
                    <div ng-class="{ darken: data.speed.value == undefined || data.speed.value === \'null\' }">\
                        <strong>Speed:</strong>\
                        <span ng-bind="data.speed.value && data.occupancy.value != \'null\' ? data.speed.value + \' mph\' : \'No Data\'"></span>\
                    </div>\
                    <div class="even" ng-show="title === \'VDOT\'" ng-class="{ darken: data.bsm.dataValue == undefined || data.bsm.dataValue === \'null\' }">\
                        <strong>BSM Speed:</strong>\
                        <span ng-bind="data.bsm.dataValue && data.bsm.dataValue != \'null\' ? data.bsm.dataValue + \' mph\' : \'No Data\'"></span>\
                    </div>\
                </div>\
            </div>',
    link: function( $scope ) {
        $scope.clicked = false

        $scope.toggleModal = function() {
            var mainModule = $scope.$parent.$parent
            mainModule.incidents = $scope.data.incidents
            mainModule.blur = true
            console.log($scope.data);
        }
    }
  }
})

