<?php

//Deal Notifier Database - trial
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['UserId']) && isset($_POST['OfferPrice']) && isset($_POST['CategoryId']) && isset($_POST['Brand']) && isset($_POST['ModelNumber']) && isset($_POST['Upc']) && isset($_POST['Keyword1']) && isset($_POST['Keyword2']) && isset($_POST['Keyword3']) && isset($_POST['Keyword4']) && isset($_POST['Keyword5'])) {
    
	
	$uid = $_POST['UserId'];
	$OfferPrice = $_POST['OfferPrice'];
    $CategoryId = $_POST['CategoryId'];
	$Brand = $_POST['Brand'];
	$ModelNumber = $_POST['ModelNumber'];
	$UPC = $_POST['Upc'];
    $Keyword1 = $_POST['Keyword1'];
	$Keyword2 = $_POST['Keyword2'];
	$Keyword3 = $_POST['Keyword3'];
	$Keyword4 = $_POST['Keyword4'];
	$Keyword5 = $_POST['Keyword5'];
	
    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	
    $result = mysql_query("INSERT INTO WishesTable(UserId, OfferPrice, IntCategoryId, Brand, ModelNumber, Upc, Keyword1, Keyword2, Keyword3, Keyword4, Keyword5) VALUES('$uid', '$OfferPrice', '$CategoryId', '$Brand', '$ModelNumber', '$UPC', '$Keyword1', '$Keyword2', '$Keyword3', '$Keyword4', '$Keyword5')");
	
	// check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product successfully created.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "OoOOOOps! why did this error occurred.";
        
        // echoing JSON response
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