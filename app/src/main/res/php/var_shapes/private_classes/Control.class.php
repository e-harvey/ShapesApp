<?php
class Control {
    // internal helper methods
    private static function checkToken($username, $tok) {
        global $connection;
        $statement = $connection->prepare("SELECT token FROM user WHERE username =:username");
        $statement->bindParam(':username', $username);
        $statement->execute();
        $token = $statement->fetchAll();

        if ($statement->rowCount() > 0 && $tok == $token[0][0]) {
            return true;
        }
        return false;
    }

    // login services

    /**
     * Check the login status of the user.
     * @param username the user's username.
     * @return true if the given token matches; otherwise false.
     */
    public static function getLoginStatus($username) {
        global $connection;
        $statement = $connection->prepare("SELECT status FROM user WHERE username =:username");
        $statement->bindParam(':username', $username);
        $statement->execute();
        $status = $statement->fetchAll();

        if ($statement->rowCount() > 0)
            return $status[0][0];
        return false;
    }

    /**
     * If the given password matches that of the stored hash for the given
     * user.  Create a token using the username, insert it into the database,
     * and return that token.
     * @param username the user's username
     * @param password the user's password
     * @return the token on success; otherwise false.
     */
    public static function login($username, $password) {
        global $connection;
        $statement = $connection->prepare("SELECT passwdhash FROM user WHERE username =:username");
        $statement->bindParam(':username', $username);
        $statement->execute();
        $hash = $statement->fetchAll();

        if ($statement->rowCount() > 0 && password_verify($password, $hash[0][0]) == true) {
            $token = password_hash($username,  PASSWORD_BCRYPT);

            $statement = $connection->prepare("update user set token = :token, status = 1 where username = :username");
            $statement->bindParam(':token', $token);
            $statement->bindParam(':username', $username);

            if ($statement->execute() && ($statement->rowCount() > 0)) {
                return $token;
            }
        }

        return false;
    }

    /**
     * Attempt to log the user out of their session by setting their
     * token back to zero.
     * @param username the user's username
     * @param tok the user's token.
     * @return true if the user has been logged out; otherwise false.
     */
    public static function logout($username, $tok) {
        global $connection;

        if (Control::checkToken($username, $tok)) {
            $statement = $connection->prepare("update user set token = 0, status = 0 where username =:username");
            $statement->bindParam(':username', $username);
            $statement->execute();
            return true;
        }

        return false;
    }

    // setter services

    /**
     * Attempt to set a new block seed for the given user.  Verify the
     * token and if it matched update the block seed.
     * @param username the user's username.
     * @param tok the user's token.
     * @param seed the new seed to be set for this user.
     */
    public static function setBlockSeed($username, $tok, $seed) {
        global $connection;

        if (Control::checkToken($username, $tok)) {
            $statement = $connection->prepare("select username from blockseed where username = :username");
            $statement->bindParam(':username', $username);
            $statement->execute();
            $result = $statement->fetchAll();

            // If there is already an entry in the blockseed table just update
            if (($statement->rowCount()) > 0 && $result[0][0] == $username) {
                $statement = $connection->prepare("update blockseed set seed = :seed where username =:username");
            } else { // Create a new entry in the table blockseed
                $statement = $connection->prepare("insert into blockseed values (:username, :seed)");
            }
            $statement->bindParam(':username', $username);
            $statement->bindParam(':seed', $seed);
            $statement->execute();
            return true;
        }

        return false;
    }

    /**
     * Attempt to update the high score of the given user.  If the token
     * matches update the highscore.
     * @param username the user's username.
     * @param tok the user's token.
     * @param score the new highscore.
     * @return true if the score is updated successfully; otherwise false.
     */
    public static function setHighScore($username, $tok, $highscore) {
        global $connection;

        if (Control::checkToken($username, $tok)) {
            $statement = $connection->prepare("update user set highscore = :highscore where username =:username");
            $statement->bindParam(':highscore', $highscore);
            $statement->bindParam(':username', $username);
            $statement->execute();
            return true;
        }
        return false;
    }

    /**
     * Attempt to add a new user to the table. Check to see if an existing user
     * with the same username is already in the table and return 2 if so.
     * Otherwise hash the user's password and store it in the database.
     * @param username the user's username
     * @param password the user's desired password.
     */
    public static function addUser($username, $password) {
        global $connection;
        $statement = $connection->prepare("SELECT username FROM user WHERE username =:username");
        $statement->bindParam(':username', $username);

        // Check to see if the username already exists
        if ($statement->execute() == true) {
            if ($statement->rowCount() > 0)
                return(1);
        }
        $hash = password_hash($password,  PASSWORD_BCRYPT);
        $highscore = 0;
        $status = 0;
        $token = 'deadbeefbeefdead';

        $statement = $connection->prepare("insert into user values (:username,  :highscore,  :status, :hash, :token)");
        $statement->bindParam(':username', $username);
        $statement->bindParam(':highscore', $highscore);
        $statement->bindParam(':status', $status);
        $statement->bindParam(':hash', $hash);
        $statement->bindParam(':token', $token);

        // Check to see if the user was added
        if ($statement->execute() == true && $statement->rowCount() > 0) {
            return(0);
        } else {
            return(2);
        }
        return(3);
    }

    /**
     * Delete the given user from all tables if the password given
     * matches that of the password stored.
     * @param username the user's username
     * @param password the user's password
     * @param true if the user was deleted, false if the user wasn't deleted, and
     * 2 if the password didn't match.
     */
    public static function deleteUser($username, $password) {
        global $connection;
        $statement = $connection->prepare("SELECT passwdhash FROM user WHERE username =:username");
        $statement->bindParam(':username', $username);
        $statement->execute();
        $hash = $statement->fetchAll();

        if ($statement->rowCount() > 0 && password_verify($password, $hash[0][0]) == true) {
            $s1 = $connection->prepare("delete FROM user WHERE username =:username");
            $s2 = $connection->prepare("delete FROM blockseed WHERE username =:username");
            $s3 = $connection->prepare("delete FROM friends WHERE user =:username");
            $s1->bindParam(':username', $username);
            $s2->bindParam(':username', $username);
            $s3->bindParam(':username', $username);

            $s1->execute();
            $s2->execute();
            $s3->execute();
            return true;
        }
        return false;
    }

    /**
     * Update the password for the given user if the oldPassword matches the existing one.
     * @param username the user's username.
     * @param oldPassword the old password.
     * @param newPassword the new passowrd.
     * @return true if the password was updated, false if it wasn't, 2 if the
     * oldPassword doesn't match.
     */
    public static function setPassword($username, $oldPassword, $newPassword) {
        global $connection;
        $statement = $connection->prepare("SELECT passwdhash FROM user WHERE username =:username");
        $statement->bindParam(':username', $username);
        $statement->execute();
        $oldHash = $statement->fetchAll();

        if ($statement->rowCount() > 0 && password_verify($oldPassword, $oldHash[0][0]) == true) {
            $statement = $connection->prepare("update user set passwdhash = :hash where username = :username");
            $newHash = password_hash($newPassword, PASSWORD_BCRYPT);
            $statement->bindParam(':hash', $newHash);
            $statement->bindParam(':username', $username);
            if ($statement->execute() && ($statement->rowCount() > 0)) {
                return true;
            }
        }
        return false;
    }

    // getter services

    /**
     * Get the highscore for the given user.
     * @param username the user's username.
     * @param tok the user's token.
     * @return the user's highscore if it's found; otherwise false.
     */
    public static function getHighScore($username) {
        global $connection;
        $statement = $connection->prepare("SELECT highscore FROM user WHERE username =:username");
        $statement->bindParam(':username', $username);
        $statement->execute();
        $score = $statement->fetchAll();

        if ($statement->rowCount() > 0)
            return $score[0][0];
        return false;
    }

    /**
     * Retrieve the block seed for the given user.
     * @param username the user's username
     * @return the seed on success; otherwise false
     */
    public static function getBlockSeed($username) {
        global $connection;
        $statement = $connection->prepare("SELECT seed FROM blockseed WHERE username =:username");
        $statement->bindParam(':username', $username);
        $statement->execute();
        $seed = $statement->fetchAll();

        if ($statement->rowCount() > 0)
            return $seed[0][0];
        return false;
    }

    /**
     * Retrieve the daily challenge seed (updated every day at 10pm).
     * @return the seed on success; otherwise false.
     */
    public static function getDailyChallengeSeed() {
        global $connection;
        $statement = $connection->prepare("SELECT seed FROM dailychallenge");
        $statement->bindParam(':username', $username);
        $statement->execute();
        $seed = $statement->fetchAll();

        if ($statement->rowCount() > 0)
            return $seed[0][0];
        return false;
    }

    /**
     * Attempt to set a new block seed for the given user.  Verify the
     * token and if it matched update the block seed.
     * @param username the user's username.
     * @param tok the user's token.
     */
    public static function getTopFriend($username, $tok) {
        global $connection;

        if (Control::checkToken($username, $tok)) {
            $statement = $connection->prepare("(select highscore,username from user having username in (select friend from friends where user = :username)) order by highscore desc");
            $statement->bindParam(':username', $username);
            $statement->execute();
            $result = $statement->fetchAll();

            if (($statement->rowCount()) > 0) {
                return $result[0][0].",".$result[0][1];;
            }
        }

        return false;
    }

    /**
     * Search for a given username in the user table.
     * @param the username to search for.
     * @return true if the username is found; otherwise false.
     */
    public static function findUser($username) {
        global $connection;

        $statement = $connection->prepare("select username from user where username = :username");
	    $statement->bindParam(':username', $username);
	    $statement->execute();
        $result = $statement->fetchAll();

        // If there is already an entry in the blockseed table just update
        if (($statement->rowCount()) > 0 && $result[0][0] == $username) {
            return true;
        }

        return false;
    }

    /**
     * Try to add a new friends to the owner's friend table.
     * @param usernameOwner the owner of this username.
     * @param usernameFriend the desired friend.
     * @return true on success; otherwise false.
     */
    public static function addNewFriend($usernameOwner, $usernameFriend, $tok) {
        global $connection;

        if (Control::checkToken($usernameOwner, $tok)) {
            $statement = $connection->prepare("insert into friends values (:usernameOwner, :usernameFriend)");
            $statement->bindParam(':usernameOwner', $usernameOwner);
            $statement->bindParam(':usernameFriend', $usernameFriend);
            $statement->execute();

            if (($statement->rowCount()) > 0) {
                return true;
            }
        }

        return false;
    }
}
?>