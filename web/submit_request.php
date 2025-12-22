<?php
ini_set('display_errors', true);

require "sql.php";

$error = false;
$output = "";
$inviter = "";
$inviter_id = "0";

if(isset($_POST["mobile_session"])){

	$username = $password = $location = $requestfor ="";
	$offer = $amount = "";
	$latitude = $longitude = "0";
	
	if(!empty($_POST["username"])){
		$username = $_POST["username"];
	}
	if(!empty($_POST["password"])){
		$password = $_POST["password"];
	}
	if(!empty($_POST["offer"])){
		$offer = $_POST["offer"];
	}
	if(!empty($_POST["amount"])){
		$amount = $_POST["amount"];
	}
	if(!empty($_POST["requestfor"])){
		$requestfor = $_POST["requestfor"];
	}
	if(!empty($_POST["location"])){
		$location = $_POST["location"];
	}
	if(!empty($_POST["latitude"])){
		$latitude = $_POST["latitude"];
	}
	if(!empty($_POST["longitude"])){
		$longitude = $_POST["longitude"];
	}


	$username = mysqli_real_escape_string($sql_conn,$username);
	$password = mysqli_real_escape_string($sql_conn,sha1($password));

	$sql = "SELECT * FROM `users` WHERE username='". $username . "' AND password='" . $password . "'";


	$result = mysqli_query($sql_conn, $sql);

	if($result && mysqli_num_rows($result) != 0){

		$user_id = mysqli_fetch_assoc($result)["id"];
	
		$sql = "SELECT * FROM `requests` where userid='". $user_id . "'";

		$result = mysqli_query($sql_conn, $sql);

		if($result && mysqli_num_rows($result) != 0){

			$error = 1;

		
		} else {
			$requestfor = mysqli_real_escape_string($sql_conn, $requestfor);
			$location = mysqli_real_escape_string($sql_conn, $location);
			$offer = mysqli_real_escape_string($sql_conn, $offer);
			$amount = mysqli_real_escape_string($sql_conn, $amount);
			$latitude = mysqli_real_escape_string($sql_conn, $latitude);
			$longitude = mysqli_real_escape_string($sql_conn, $longitude);

			$sql = "";

			$sql = "INSERT INTO `requests` (status, latitude, longitude, requestfor, location, offer, amount, userid, time) VALUES ("
				. "'pending', " .  $latitude . ", " . $longitude . ", '" .  $requestfor . "', '" . $location . "', " . $offer . ", " . $amount . ", " . $user_id . ", '" . time() ."')";



			mysqli_query($sql_conn, $sql);
		}
	} else { $error = true; }


}

	echo "{\"success\":";
	if($error == true){
		echo "False";
	} else {
		echo "True";
	}
	echo "}";

?>