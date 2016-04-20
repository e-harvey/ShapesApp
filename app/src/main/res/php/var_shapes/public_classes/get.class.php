<?php

class get {
    private static $publicActions = array("getHighScore",
                                          "getBlockSeed",
                                          "getLoginstatus",
                                          "getDailyChallengeSeed");

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
           $_POST['username'] != "") {

            $username = $_POST['username'];
            $score = Control::getHighScore($username);

            return(new Response(true,$score));
        }
        return(new Response(false,'Username or token was not given.'));
    }

    public function getBlockSeed() {
        if(isset($_POST['username']) &&
           $_POST['username'] != "") {

            $username = $_POST['username'];
            $seed = Control::getBlockSeed($username);

            return(new Response(true,$seed));
        }
        return(new Response(false,'Username or token was not given.'));
    }

    public function getLoginStatus() {
        if(isset($_POST['username']) &&
           $_POST['username'] != "") {

            $username = $_POST['username'];
            $status = Control::getLoginStatus($username);

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

    public function getDailyChallengeSeed() {
        $seed = Control::getDailyChallengeSeed();

        if ($seed) {
            return(new Response(true,$seed));
        }
        return(new Response(false,'Failed to get daily challenge seed.'));
    }
}
?>