<?php
$PHP_BASE_PATH = dirname(__DIR__.'/../');
$user_conf = NULL;
global $connection;
if (file_exists('config.ini')) {
    $user_conf = parse_ini_file('config.ini');
}
if ($user_conf
    && isset($user_conf['public_class_path'])
    && isset($user_conf['private_class_path'])) {
    $PUBLIC_CLASS_PATH = $PHP_BASE_PATH.'/'.$user_conf['public_class_path'].'/';
    $PRIVATE_CLASS_PATH = $PHP_BASE_PATH.'/'.$user_conf['private_class_path'].'/';
}
$SYSTEM_CLASS_PATH = $PHP_BASE_PATH.'/system_classes/';

$db_conf = NULL;
if (file_exists($PUBLIC_CLASS_PATH.'/../'.'db.ini')) {
    $db_conf = parse_ini_file($PUBLIC_CLASS_PATH.'/../'.'db.ini');
}

if ($db_conf
    && isset($db_conf['user'])
    && isset($db_conf['password'])
    && isset($db_conf['dbname'])) {

    $username = $db_conf['user'];
    $password = $db_conf['password'];
    $dbname = $db_conf['dbname'];
} else {
    die("YOu;re dead.");
}

// Establish connection
$connection = new PDO("mysql:host=localhost;dbname=$dbname", $username, $password);
$connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

session_start();

// If the specified class cannot be found at a later pint this fn with autoload it.
function my_autoload($classname) {
    global $SYSTEM_CLASS_PATH;
    global $PUBLIC_CLASS_PATH;
    global $PRIVATE_CLASS_PATH;
    if (file_exists($SYSTEM_CLASS_PATH.$classname.'.class.php')) {
        require_once($SYSTEM_CLASS_PATH.$classname.'.class.php');
    } else if (file_exists($PRIVATE_CLASS_PATH.$classname.'.class.php')) {
        require_once($PRIVATE_CLASS_PATH.$classname.'.class.php');
    } else if (file_exists($PUBLIC_CLASS_PATH.$classname.'.class.php')) {
        require_once($PUBLIC_CLASS_PATH.$classname.'.class.php');
    }

}
spl_autoload_register('my_autoload');
?>
