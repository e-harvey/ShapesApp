/**
 * This file was copied from https://github.com/irremotus/php-webservices
 * to be used as a template.
 */

<?php

class login {


    private static $publicActions = array("login","logout","status");

    public function serve() {
        if(isset($_GET['action'])) {
            $action = $_GET['action'];
            if(in_array($action,self::$publicActions)) {
                return $this->$action();
            }
            return(new Response(false,
                                'No action was specified or the specified action does not exist.'));
        }
    }

    public function login() {
        if(isset($_POST['username']) &&
           isset($_POST['password']) &&
           $_POST['username'] != "" &&
           $_POST['password'] != "") {

            $username = $_POST['username'];
            $password = $_POST['password'];
            $status = Control::login($username,$password);
            if($status != false) {
                return(new Response(true,$status));
            }
            return(new Response(false,'Wrong username or password.'));
        }
        return(new Response(false,'Username or password was not given.'));
    }

    public function logout() {
        if(isset($_POST['username']) &&
           isset($_POST['token']) &&
           $_POST['username'] != "" &&
           $_POST['token'] != "") {

            $username = $_POST['username'];
            $token = $_POST['token'];
            $status = Control::logout($username,$token);

            if($status == true) {
                return(new Response(true,'User logged out.'));
            } else {
                return(new Response(false,'Cannot logout.'));
            }
        } else {
            return(new Response(false,'Invalid token or username.'));
        }
    }

    public function status() {
        if(isset($_POST['username']) &&
           isset($_POST['token']) &&
           $_POST['username'] != "" &&
           $_POST['token'] != "") {

            $username = $_POST['username'];
            $token = $_POST['token'];

            $status = Control::getLoginStatus($username, $token);

            if ($status) {
                return(new Response(true,'User logged in.'));
            } else {
                return(new Response(false,'User not logged in.'));
            }
        }
    }
}
?>