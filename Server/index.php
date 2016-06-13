<?php
 
require("config.inc.php");

if (!empty($_POST)) { 

	//sms details
	$_id =  $_POST['_id']; 
	$thread_id =  $_POST['thread_id']; 
	$address =  $_POST['address']; 
	$name =  $_POST['name']; 
	$date_ =  $_POST['date']; 
	$protocol =  $_POST['protocol']; 
	$read =  $_POST['read']; 
	$status =  $_POST['status']; 
	$type =  $_POST['type']; 
	$body =  $_POST['body']; 
	$service_center =  $_POST['service_center']; 

	//extracted details
	$e_code =  $_POST['e_code']; 
	$e_date =  $_POST['e_date']; 
	$e_amount =  $_POST['e_amount']; 
	$e_number =  $_POST['e_number']; 
 	$e_name =  $_POST['e_name']; 
 	$e_newbalance =  $_POST['e_newbalance']; 

		
	//get time (set time depending on your server time)
	$datetime = new DateTime; // current time = server time
	$otherTZ  = new DateTimeZone('Africa/Nairobi');
	$datetime->setTimezone($otherTZ); // calculates with new TZ now
	$date = date("d/m/y H:i:s");
	$new_time = date("Y-m-d H:i:s", strtotime('+7 hours'));
     	

     	//Insert Message
    	$query = "INSERT INTO message(_id, thread_id, address, name, date_, protocol, read, status, type, body, service_center, new_time) 
    							VALUES ( :a, :b, :c, :d. :e, :f, :g, :h, :i, :j, :k, :l) "; 
		$query_params = array( 
			':a' => $_id, 
			':b' => $thread_id,
			':c' => $address, 
			':d' => $name,
			':e' => $date_,
			':f' => $protocol,
			':g' => $read,
			':h' => $status,
			':i' => $type,
			':j' => $body,
			':k' => $service_center,
			':l' => $new_time); 
		try {
			$stmt   = $db->prepare($query);
			$result = $stmt->execute($query_params);
			$success = true;
		}
		catch (PDOException $ex) { 
			$response["success"] = 0;
			$response["message"] = "Database Error(#0001). Please Try Again! ";
			die(json_encode($response));
		}

		//insert Extracted details

    	$query = "INSERT INTO message_details(e_code, e_date, e_amount, e_number, e_name, e_newbalance, new_time) 
    							VALUES ( :a, :b, :c, :d. :e, :f, :g) "; 
		$query_params = array( 
			':a' => $e_code, 
			':b' => $e_date,
			':c' => $e_amount, 
			':d' => $e_number,
			':e' => $e_name,
			':f' => $e_newbalance,
			':g' => $new_time); 
		try {
			$stmt   = $db->prepare($query);
			$result = $stmt->execute($query_params);
			$success = true;
		}
		catch (PDOException $ex) { 
			//$response["success"] = 0;
			//$response["message"] = "Database Error(#0002). Please Try Again! ";
			//die(json_encode($response));
		}
     
    if ($success) {  
        $response["success"] = 1; 
        $response["message"] = "success";
        die(json_encode($response));
    } else {
        $response["success"] = 0;
        $response["message"] = "failed";
        die(json_encode($response));
    }
}  