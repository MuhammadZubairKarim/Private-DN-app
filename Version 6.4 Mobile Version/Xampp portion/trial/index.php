<?php

// Database trial

/** Changes made in this doc are in the following sections:
  * 1. Returning user data once the 'check for user' is found NOT to be empty
  * 2. All instances of uid has been converted into id and has been commented out also
  * 3. The input fields for registering new users 
  * 4. Storing details of new user
	
	
/**
 * File to handle all API requests
 * Accepts GET and POST
 * 
 * Each request will be identified by TAG
 * Response will be JSON data

  /**
 * check for POST request 
 */
if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // get tag
    $tag = $_POST['tag'];

    // include db handler
    require_once 'include/DB_Functions.php';
    $db = new DB_Functions();

    // response Array
    $response = array("tag" => $tag, "success" => 0, "error" => 0);

    // check for tag type
    if ($tag == 'login') {
        // Request type is check Login
        $username = $_POST['Username']; // Somehow this section is acting as case in-sensitive!!
        $password = $_POST['Password'];

        // check for user
        $user = $db->getUserByUsernameAndPassword($username, $password);
        if ($user != false) {
            // user found
            // echo json with success = 1
            $response["success"] = 1;
            $response["uid"] = $user["Uuid"];
            $response["user"]["username"] = $user["Username"];
            $response["user"]["email"] = $user["Email"];
			$response["user"]["join_date"] = $user["JoinDate"];
			$response["user"]["last_active_date"] = $user["LastActiveDate"];
			$response["user"]["first_name"] = $user["FirstName"];
			$response["user"]["last_name"] = $user["LastName"];
			$response["user"]["age"] = $user["Age"];
			$response["user"]["country"] = $user["Country"];
			$response["user"]["postal_code"] = $user["ZipCode"];
            echo json_encode($response);
        } else {
            // user not found
            // echo json with error = 1
            $response["error"] = 1;
            $response["error_msg"] = "Incorrect email or password! Please check again!!";
            echo json_encode($response);
        }
    } else if ($tag == 'register') {
        // Request type is for Registering a new user
        $username = $_POST['Username'];
        $email = $_POST['Email'];
        $password = $_POST['Password'];
		$first_name = $_POST['FirstName'];
		$last_name = $_POST['LastName'];
		$age = $_POST['Age'];
		$country = $_POST['Country'];
		$postal_code = $_POST['ZipCode'];

        // check if user is already existed
        if ($db->DoesUserExist($username)) {
            // user is already existed - error response
            $response["error"] = 2;
            $response["error_msg"] = "You already exist in out system!";
            echo json_encode($response);
        } else {
            // store user
            $user = $db->storeUser($username, $email, $password, $first_name, $last_name, $age, $country, $postal_code);
			
            if ($user) {
                // user stored successfully
                $response["success"] = 1;
				$response["uid"] = $user["Uuid"];
                $response["user"]["username"] = $user["Username"];
                $response["user"]["email"] = $user["Email"];
				$response["user"]["join_date"] = $user["JoinDate"];
				$response["user"]["last_active_date"] = $user["LastActiveDate"];
				$response["user"]["first_name"] = $user["FirstName"];
				$response["user"]["last_name"] = $user["LastName"];
				$response["user"]["age"] = $user["Age"];
				$response["user"]["country"] = $user["Country"];
				$response["user"]["postal_code"] = $user["ZipCode"];
                echo json_encode($response);
            } else {
                // failed to store user data
                $response["error"] = 1;
                $response["error_msg"] = "Check even harder";
                echo json_encode($response);
            }
        }
    } else {
        echo "Invalid Request";
    }
} else {
    echo "Access Denied";
}

?>
