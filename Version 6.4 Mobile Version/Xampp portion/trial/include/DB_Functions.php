<?php

class DB_Functions {

    private $db;

    //put your code here
    // constructor - Establishes connection with database
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }

    // destructor - Closes connection with database
    function __destruct() {
        
    }

    /**
     * Stores details of new user
     * Returns details of existing users
     */
    public function storeUser($username, $email, $password, $first_name, $last_name, $age, $country, $postal_code) {
        $uid = uniqid('', true); //the uniqid is a function do not mess with it!!!
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; 
        $salt = $hash["salt"]; // salt
        $result = mysql_query("INSERT INTO UsersTable(Uuid, Username, Email, Password, Salt, JoinDate, FirstName, LastName, Age, Country, ZipCode) VALUES('$uid', '$username', '$email', '$encrypted_password', '$salt', NOW(), '$first_name', '$last_name', '$age', '$country', '$postal_code')");
		//$variable = "INSERT INTO UsersTable(Uuid, Username, Email, Password, Salt, JoinDate, FirstName, LastName, Age, Country, PostalCode) VALUES('$uid', '$username', '$email', '$encrypted_password', '$salt', NOW(), '$first_name', '$last_name', '$age', '$country', '$postal_code')"; 
		//echo json_encode($variable);
        // check for successful store
        if ($result) {
            // get user details 
            $uid = mysql_insert_id(); 
            $result = mysql_query("SELECT * FROM UsersTable WHERE id = $uid"); //Works fine with 'id' HOWEVER it is named as 'Id' in the DB!!
            // return user details
            return mysql_fetch_array($result);
        } else {
            return false;
        }
    }

    /**
     * Get user by user-name and password
     */
    public function getUserByUsernameAndPassword($username, $password) {
        $result = mysql_query("SELECT * FROM UsersTable WHERE Username = '$username'") or die(mysql_error());
        // check for result 
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
		//echo json_encode('here1');
            $result = mysql_fetch_array($result);
            $salt = $result['Salt'];
            $encrypted_password = $result['Password'];
            $hash = $this->checkhashSSHA($salt, $password);
			//echo json_encode($encrypted_password);
			//echo json_encode($hash);
            // check for password equality
            if ($encrypted_password == $hash) {
			//echo json_encode('here2');
                // user authentication details are correct
                return $result;
            }
        } else {
            // user not found
            return false;
        }
    }

    /**
     * Check if user already exists or not
     */
    public function DoesUserExist($username) {
        $result = mysql_query("SELECT username from UsersTable WHERE Username = '$username'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user exists
            return true;
        } else {
            // user does not exist
            return false;
        }
    }

    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

}

?>
