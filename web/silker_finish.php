<?php
ini_set('display_errors', true);

require "sql.php";

$inviter = "";
$error = false;
$inviter_id = "0";
$output = "{";

if(isset($_POST["mobile_session"])){


	$username = "";
	$password = "";
	$location = "";
	$latitude = "";
	$longitude = "";
	$requestid = "";
	if(!empty($_POST["username"])){
		$username = $_POST["username"];
	}
	if(!empty($_POST["password"])){
		$password = $_POST["password"];
	}
	if(!empty($_POST["latitude"])){
		$latitude = $_POST["latitude"];
	}

	if(!empty($_POST["longitude"])){
		$longitude = $_POST["longitude"];
	}

	if(!empty($_POST["location"])){
		$location = $_POST["location"];
	}


	if(!empty($_POST["requestid"])){
		$requestid = $_POST["requestid"];
	}

	$username = mysqli_real_escape_string($sql_conn,$username);
	$password = mysqli_real_escape_string($sql_conn,sha1($password));

	$sql = "SELECT * FROM `users` WHERE username='". $username . "' AND password='" . $password . "'";


	$result = mysqli_query($sql_conn, $sql);

	if($result){

		$latitude = mysqli_real_escape_string($sql_conn,$latitude);
		$longitude = mysqli_real_escape_string($sql_conn,$longitude);
		$location = mysqli_real_escape_string($sql_conn,$location);
		$sql = "UPDATE `requests` SET silkerid = 0 , status = 'complete'" .
			", latitude_dropoff = " . $latitude . 
			", longitude_dropoff = ". $longitude .
			", location_dropoff = '" .$location . "' WHERE id = " . $requestid;


		echo $sql;
		$result = mysqli_query($sql_conn, $sql);

		if($result){
			$error = false;
		} else {
			$error = true;
		}
	} else {
		$error = true;
	}
	$output .= "\"success\":";
	if($error == true){
		$output .= "False";
	} else {
		$output .= "True";
	}
	$output .= "}";
	echo $output;
}
?>