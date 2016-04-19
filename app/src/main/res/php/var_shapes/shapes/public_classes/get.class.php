<?php

	class get {
		private static $publicActions = array("getHighScore",
                                              "getBlockSeed",
                                              "getLoginstatus");

		public function serve() {
			if(isset($_GET['action'])) {
				$action = $_GET['action'];
				if(in_array($action,self::$publicActions)) {
					return $this->$action();
				}
				return(new Response(false,'No action was specified or the specified action does not exist.'));
			}
		}

		public function getHighScore() {
			if(isset($_POST['username']) &&
               isset($_POST['token']) &&
               $_POST['username'] != "" &&
               $_POST['token'] != "") {

				$username = $_POST['username'];
				$token = $_POST['token'];
				$score = Control::getHighScore($username,$token);

                return(new Response(true,$score));
			}
			return(new Response(false,'Username or token was not given.'));
		}

        public function getBlockSeed() {
			if(isset($_POST['username']) &&
               isset($_POST['token']) &&
               $_POST['username'] != "" &&
               $_POST['token'] != "") {

				$username = $_POST['username'];
				$token = $_POST['token'];
				$seed = Control::getBlockSeed($username,$token);

                return(new Response(true,$seed));
			}
			return(new Response(false,'Username or token was not given.'));
		}

        public function getLoginStatus() {
            if(isset($_POST['username']) &&
               isset($_POST['token']) &&
               $_POST['username'] != "" &&
               $_POST['token'] != "") {

				$username = $_POST['username'];
				$token = $_POST['token'];
				$status = Control::getLoginStatus($username,$token);

                if ($status) {
                    // Token matches
                    return(new Response(true,'User is logged in.'));
                } else {
                    // Token doesn't match
                    return(new Response(false,'The user is logged out.'));
                }
			}
            return(new Response(false,'The username or token was not given.'));
        }
}
?>