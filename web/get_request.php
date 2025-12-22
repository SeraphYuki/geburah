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
		
	if(!empty($_POST["username"])){
		$username = $_POST["username"];
	}
	if(!empty($_POST["password"])){
		$password = $_POST["password"];
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

			$rows = mysqli_fetch_assoc($result);
			$requestfor = $rows["requestfor"];
			$status = $rows["status"];
			$location = $rows["location"];
			$amount = $rows["amount"];
			$offer = $rows["offer"];
			$latitude = $rows["latitude_dropoff"];
			$longitude = $rows["longitude_dropoff"];
			$imgid = $rows["imgid"];
			$silkerid = $rows["silkerid"];

			// json_encode($rows);

			$output .= "\"requestfor\": \"" . $requestfor . 
			"\",\"status\": \"" . $status .
			"\",\"location\": \"" . $location .
			 "\",\"amount\": \"" . $amount . 
			 "\",\"offer\":  " . $offer . 
			 ",\"latitude\":  " . $latitude . 
			 ",\"longitude\":  " . $longitude .
			 ",\"imgid\": " . $imgid .
			 ",\"silkerid\": " . $silkerid . ", ";


		} else {
			$error = 1;
		}
	}

}

	$output .= "\"success\":";
	if($error == true){
		$output .= "False";
	} else {
		$output .= "True";
	}
	$output .= "}";
	echo $output;


?>