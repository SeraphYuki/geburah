<?php


$sql_servername = "localhost";
$sql_username = "";
$sql_password = "";
$sql_database = "";

$sql_conn = mysqli_connect($sql_servername, $sql_username, $sql_password, 
	$sql_database);

if($sql_conn == false){
	die("Connection error: " . mysqli_connect_error());
}

?>