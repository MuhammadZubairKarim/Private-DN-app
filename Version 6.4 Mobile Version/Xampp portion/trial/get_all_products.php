<?php

/*
 * Following code will list all the products
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();


if (isset($_GET['UserId'])) {
    
	$uid = $_GET['UserId'];
	// get all products from wishes table
	// change from uid to UserId to match column name in DB
	$result = mysql_query("SELECT * FROM WishesTable WHERE UserId = '$uid'") or die(mysql_error());

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
    $response["products"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $product = array();
		$product["pid"] = $row["Id"]; //Updated with DB column name
		$product["uid"] = $row["UserId"]; //Updated with DB column name
		$product["OfferPrice"] = $row["OfferPrice"];
        $product["Brand"] = $row["Brand"];
		$product["ModelNumber"] = $row["ModelNumber"];
		$product["UPC"] = $row["Upc"];
        $product["Keyword1"] = $row["Keyword1"];
		$product["Keyword2"] = $row["Keyword2"];
		$product["Keyword3"] = $row["Keyword3"];
		$product["Keyword4"] = $row["Keyword4"];
		$product["Keyword5"] = $row["Keyword5"];
        $product["DateAdded"] = $row["DateAdded"];
       //$product["DateLastChecked"] = $row["DateLastChecked"];



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
    $response["message"] = "No products found in all products";

    // echo no users JSON
    echo json_encode($response);
}
?>
