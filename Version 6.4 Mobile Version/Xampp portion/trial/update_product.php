<?php

/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['pid']) && isset($_POST['CategoryIds']) && isset($_POST['OfferPrice']) && isset($_POST['Brand']) && isset($_POST['ModelNumber']) && isset($_POST['UPC']) && isset($_POST['Keyword1']) && isset($_POST['Keyword2']) && isset($_POST['Keyword3']) && isset($_POST['Keyword4']) && isset($_POST['Keyword5'])) {
    
	//  
    $pid = $_POST['pid'];
	$CategoryId = $_POST['CategoryIds'];
    $OfferPrice = $_POST['OfferPrice'];
    $Brand = $_POST['Brand'];
	$ModelNumber = $_POST['ModelNumber'];
	$UPC = $_POST['UPC'];
    $Keyword1 = $_POST['Keyword1'];
	$Keyword2 = $_POST['Keyword2'];
	$Keyword3 = $_POST['Keyword3'];
	$Keyword4 = $_POST['Keyword4'];
	$Keyword5 = $_POST['Keyword5'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql update row with matched pid
    $result = mysql_query("UPDATE WishesTable SET IntCategoryId = '$CategoryId', OfferPrice = '$OfferPrice', Brand = '$Brand', ModelNumber = '$ModelNumber', Upc = '$UPC', Keyword1 = '$Keyword1', Keyword2 = '$Keyword2', Keyword3 = '$Keyword3', Keyword4 = '$Keyword4', Keyword5 = '$Keyword5' WHERE Id = $pid");
//  
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Product successfully updated.";
        
        // echoing JSON response
        echo json_encode($response);
    } else {
        
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>
