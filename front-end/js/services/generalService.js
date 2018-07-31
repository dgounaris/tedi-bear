(function() {

	angular.module('tediApp')
	.factory('globalFunctions', function($rootScope, $http) {
		var globalFunctionsFactory = {};

		globalFunctionsFactory.init_app = function(){
			$rootScope.tediAPI = 'https://localhost:8443';
		}

		globalFunctionsFactory.login = function(inputEmail, inputPassword) {
            var endpoint = '/login';
			var url = $rootScope.tediAPI + endpoint;

			var parameter = {
				"email": inputEmail,
				"password": inputPassword
			};

			return $http({
				method: "POST",
				url: url,
                headers: { 'Content-Type': 'application/json' },
                data: parameter,
			}).then(function onSuccess(response) {		// Handle success
				console.log(response);
				localStorage.isjwt = response.headers("Authorization").replace('Bearer ', '');
				console.log(localStorage.isjwt);
				console.log("Login Successful");
			}).catch(function onError(response) {		// Handle error
				console.log(response);
				console.log("Login Failed");
			});

			// $.ajax({
			// 	type: "POST",
			// 	url: url,
			// 	contentType: "application/json; charset=utf-8",
			// 	data: parameter,
			// 	success : function(data) {
			// 		localStorage.isjwt = data.token;
			// 		console.log("Login Successful");
			// 	}
			// });

			// $.ajax({
  			//    url: "https://localhost:8443/login?email="+inputEmail+"&password="+"toor",
  			//    data: {
  			//       format: 'json'
  			//    },
  			//    error: function(response) {
  			//       console.log(response);
  			//    },
  			//    dataType: 'json',
  			//    success: function(data) {
  			//       console.log(data);
  			//    },
  			//    type: 'POST'
  			// });
  			// var xhttp = new XMLHttpRequest();
  			// xhttp.open("POST", "https://localhost:8443/login", true);
  			// xhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
  			// xhttp.send("email="+inputEmail+"&password="+inputPassword);
  			//
  			// xhttp.onreadystatechange = function() {
  			// 	if (this.readyState == XMLHttpRequest.DONE && this.status == 200) {
  			// 		console.log("1");
  			// 		var headers = this.getAllResponseHeaders();
  			// 		console.log(headers);
  			// 		console.log(this);
  			// 	}
  			// };
		}

		globalFunctionsFactory.getuserlist = function() {
            var endpoint = '/admin/userlist';
			var url = $rootScope.tediAPI + endpoint;

			return $http({
				method: "GET",
				url: url,
                headers: { 'Content-Type': 'application/json' },
			}).then(function onSuccess(response) {		// Handle success
				console.log(response);
				console.log("UserList Successful");
			}).catch(function onError(response) {		// Handle error
				console.log(response);
				console.log("UserList Failed");
			});
		}

		globalFunctionsFactory.updateUser = function(email, password, name, surname, telNumber, picture) {
			var endpoint = '/user/update';
			var url = $rootScope.tediAPI + endpoint;

			var newUser = {
				"email": email,
				"password": password,
				"name": name,
				"surname": surname,
				"telNumber": telNumber,
				"picture": picture
			};

			return $http({
				method: "PUT",
				url: url,
				data: newUser,
			}).then(function onSuccess(response) {		// Handle success
				console.log(response);
				console.log("Update Successful");
			}).catch(function onError(response) {		// Handle error
				console.log(response);
				console.log("Update Failed");
			});
		}

		return globalFunctionsFactory;
	})

})();
