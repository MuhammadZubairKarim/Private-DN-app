<?php

/*
 * Following code will list all the categories
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

	// MySQL query to get all items from Categories table
	$result = mysql_query("SELECT * FROM CategoriesTable") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // categories node
    $response["categories"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $category = array();
		$category["Id"] = $row["Id"]; 
		$category["Name"] = $row["Name"];
      

        // push single category into final response array
        array_push($response["categories"], $category);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no list of categories were found
    $response["success"] = 0;
    $response["message"] = "No categories found in the list";

    // echo no users JSON
    echo json_encode($response);
}
?>
