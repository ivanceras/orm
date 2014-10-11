var app = angular.module('ivanceras', [
  "ngRoute", "ngSanitize"
]);

app.config(function($routeProvider, $locationProvider) {
  $routeProvider.when('/',    {templateUrl: "templates/home.html"});
  $routeProvider.when('/about',    {templateUrl: "templates/about.html"});
  $routeProvider.when('/contact',    {templateUrl: "templates/contact.html"});
  $routeProvider.when('/download',    {templateUrl: "templates/download.html"});
  $routeProvider.when('/tutorial',    {templateUrl: "templates/tutorial.html"});
  $routeProvider.when('/guide',    {templateUrl: "templates/guide.html"});
  $routeProvider.when('/demo',    {templateUrl: "templates/demo.html"});
  $routeProvider.when('/orm',    {templateUrl: "templates/ivanceras-orm.html"});
  $routeProvider.when('/fluentsql',    {templateUrl: "templates/fluentsql.html"});
});

app.service('analytics', [
  '$rootScope', '$window', '$location', function($rootScope, $window, $location) {
    var send = function(evt, data) {
      ga('send', evt, data);
    }
  }
]);



app.controller('mainController', function($rootScope, $scope, analytics){
    console.log("Routing");
    $rootScope.$on("$routeChangeStart", function(){
        $rootScope.loading = true;
    });

    $rootScope.$on("$routeChangeSuccess", function(){
        $rootScope.loading = false;
    });

});





app.controller('markdown', function($scope, $http){

    $scope.init = function(url){
        $scope.url = url;
        //console.log("initialize: "+url);
        $http.get(url)
            .success(
            function(response){
                $scope.raw = response;
                $scope.rendered = marked(response);
            }
        )
    }


});
