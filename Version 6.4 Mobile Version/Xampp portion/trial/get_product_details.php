<?php

/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// check for post data
// Renamed the Global $_GET variable to "Id" from "pid" to match specs! 
if (isset($_GET["Id"])) {
    $pid = $_GET['Id'];

    // get a product from products table
    $result = mysql_query("SELECT * FROM WishesTable WHERE Id = $pid");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {

            $result = mysql_fetch_array($result);

            $product = array();
            $product["pid"] = $result["Id"]; // Do not know for sure what the $product["any text"] represents!!
            $product["Active"] = $result["Active"];
            $product["Filled"] = $result["Filled"];
            $product["Purchased"] = $result["Purchased"];
            $product["DateAdded"] = $result["DateAdded"];
            $product["OfferPrice"] = $result["OfferPrice"];
			$product["CategoryId"] = $result["IntCategoryId"];
			$product["Brand"] = $result["Brand"];
			$product["ModelNumber"] = $result["ModelNumber"];
			$product["UPC"] = $result["Upc"];
			$product["Keyword1"] = $result["Keyword1"];
			$product["Keyword2"] = $result["Keyword2"];
			$product["Keyword3"] = $result["Keyword3"];
			$product["Keyword4"] = $result["Keyword4"];
			$product["Keyword5"] = $result["Keyword5"];
            // success
            $response["success"] = 1;

            // user node
            $response["product"] = array();

            array_push($response["product"], $product);

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "Zub1 No product found"; // For debugging purposes only

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "Zub 2 No product found"; // For debugging purposes only

        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>