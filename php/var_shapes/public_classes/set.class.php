<?php

class set {

    private static $publicActions = array("setPassword",
                                          "setBlockSeed",
                                          "addUser","deleteUser",
                                          "setHighScore", "addNewFriend");

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

    public function setPassword() {
        if(isset($_POST['username']) &&
           isset($_POST['oldPassword']) &&
           isset($_POST['newPassword']) &&
           $_POST['username'] != "" &&
           $_POST['oldPassword'] != "" &&
           $_POST['newPassword'] != "") {

            $username = $_POST['username'];
            $oldPassword = $_POST['oldPassword'];
            $newPassword = $_POST['newPassword'];
            $status = Control::setPassword($username,$oldPassword,$newPassword);
            if ($status == true) {
                return(new Response(true,NULL));
            } else {
                return(new Response(false,'Failed to update password.'));
            }
        }
        return(new Response(false,'Username or password was not given.'));
    }

    public function setBlockSeed() {
        if(isset($_POST['username']) &&
           isset($_POST['token']) &&
           isset($_POST['seed']) &&
           $_POST['username'] != "" &&
           $_POST['token'] != "" &&
           $_POST['seed'] != "") {

            $username = $_POST['username'];
            $token = $_POST['token'];
            $seed = $_POST['seed'];
            $status = Control::setBlockSeed($username,$token,$seed);

            if ($status == true) {
                return(new Response(true, 'Updated blockseed.'));
            } else if ($status == 2) {
                return(new Response(false,'Please login first.'));
            } else {
                return(new Response(false,'Failed to update blockseed.'));
            }
        } else {
            return(new Response(false,'Invalid username or token.'));
        }
    }

    public function addUser() {
        if(isset($_POST['username']) &&
           isset($_POST['password']) &&
           $_POST['username'] != "" &&
           $_POST['password'] != "") {

            $username = $_POST['username'];
            $password = $_POST['password'];
            $status = Control::addUser($username,$password);

            if ($status == 0) {
                return(new Response(true,'Successfully add the user.'));
            } else if ($status == 1) {
                return(new Response(false,'That username already exists.'));
            } else if ($status == 2) {
                return(new Response(false,'Failed to add user to db.'));
            } else {
                return(new Response(false,'Bad news bears.'));
            }
        } else {
            return(new Response(false, 'You must provide a valid non-empty username and password.'));
        }
    }

    public function deleteUser() {
        if(isset($_POST['username']) &&
           isset($_POST['password']) &&
           $_POST['username'] != "" &&
           $_POST['password'] != "") {

            $username = $_POST['username'];
            $password = $_POST['password'];
            $status = Control::deleteUser($username,$password);

            if ($status == true) {
                return(new Response(true,'Successfully removed the user.'));
            } else {
                return(new Response(false,'Cannot remove that user.'));
            }
        } else {
            return(new Response(false,'You must provide a valid non-empty username and password.'));
        }
    }

    public function setHighScore() {
        if(isset($_POST['username']) &&
           isset($_POST['token']) &&
           isset($_POST['score']) &&
           $_POST['username'] != "" &&
           $_POST['token'] != "" &&
           $_POST['score'] != "") {

            $username = $_POST['username'];
            $token = $_POST['token'];
            $score = $_POST['score'];
            $status = Control::setHighScore($username,$token,$score);

            if ($status == true) {
                return(new Response(true,'Successfully updated highscore.'));
            } else {
                return(new Response(false,'Cannot update highscore.'));
            }
        } else {
            return(new Response(false,'You must provide a valid non-empty username, token, and score.'));
        }
    }

    public function addNewFriend() {
        if(isset($_POST['usernameOwner']) &&
           isset($_POST['usernameFriend']) &&
           isset($_POST['token']) &&
           $_POST['usernameOwner'] != "" &&
           $_POST['usernameFriend'] != "" &&
           $_POST['token'] != "") {

            $usernameOwner = $_POST['usernameOwner'];
            $usernameFriend = $_POST['usernameFriend'];
            $token = $_POST['token'];

            $status = Control::addNewFriend($usernameOwner, $usernameFriend, $token);

            if ($status == true) {
                return(new Response(true,'Successfully added friend.'));
            } else {
                return(new Response(false,'Failed to add friend.'));
            }
        } else {
            return(new Response(false,'You must provide a valid non-empty username for yourself and your friend.'));
        }
    }

}
?>