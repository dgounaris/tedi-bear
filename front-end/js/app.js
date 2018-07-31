(function() {

	var app = angular.module('tediApp', [
		// 'angular',
		// 'angular-route',
		'angular-jwt'])
	.config(function($httpProvider, jwtOptionsProvider) {
		/* ================= JWT ================= */
		jwtOptionsProvider.config({
			tokenGetter: function(options) {
				token = localStorage.getItem('isjwt');
				// if (!token) {
				// 	window.location.href = "/login";
				// }
				return token;
			},
			whiteListedDomains: ['localhost']
		});
		$httpProvider.interceptors.push('jwtInterceptor');
		/* ======================================= */

	})
	.run(function ($rootScope, globalFunctions) {
		globalFunctions.init_app();
        globalFunctions.login("test1@user.com", "test");
		//globalFunctions.registerUser("test@user.com", "test");
		//globalFunctions.getuserlist();
		//globalFunctions.getUserDetails("test@user.com");
		//globalFunctions.updateUser();
	});

})();
