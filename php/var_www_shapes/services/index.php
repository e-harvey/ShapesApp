<?php
	require_once('./config.php');
	
	$services = new Services();
	$services->serve();

	return;
?>
