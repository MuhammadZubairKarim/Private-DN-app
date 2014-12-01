<?php

/*
 * Following code will list all the matches
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();


if (isset($_GET['WishId'])) {
    
	$WishId = $_GET['WishId'];
	// get all products from wishes table
	$result = mysql_query("SELECT * FROM MatchesTable WHERE WishId = '$WishId'") or die(mysql_error());

	//'$WishId'
}
 else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is/are missing";

    // echoing JSON response
    echo json_encode($response);
}

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["products"] = array(); // Letting this be products for the time being!!
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $product = array();
		$product["id"] = $row["Id"]; 
		$product["pid"] = $row["WishId"]; 
		$product["RetailerId"] = $row["RetailerId"];
        $product["ProductId"] = $row["ProductId"];
		$product["Price"] = $row["Price"];
		$product["UserHasBeenNotified"] = $row["UserHasBeenNotified"];
        $product["UserHasIgnored"] = $row["UserHasIgnored"];
		$product["DateAdded"] = $row["DateAdded"];
		$product["DateRemoved"] = $row["DateRemoved"];
		$product["Active"] = $row["Active"];


        // push single product into final response array
        array_push($response["products"], $product);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No matches found in all of the matches yikes!!";

    // echo no users JSON
    echo json_encode($response);
}
?>
