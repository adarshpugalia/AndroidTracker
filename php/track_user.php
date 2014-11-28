<?php
	
	/* initializing database variables. */
	$server = 'localhost';
	$database = 'android_tracker';
	$username = 'root';
	$password = '123';

	/* connecting to the databse. */
	$connection = mysqli_connect($server, $username, $password, $database);

	/* checking for successful connection. */
   if(!$connection)
   	die("Connection failed: " . mysqli_connect_error());

   /* for post requests. */
   if($_POST)
   {
   	$phone = $_POST['Phone'];

      /* getting the record from the databse. */
      $sql_query = "SELECT * FROM `user_location` WHERE `phone`='$phone' LIMIT 1";
      $result = mysqli_query($connection, $sql_query);

      /* error in connection. */
      if(!$result)
         die("Error updating location: " . mysqli_error($connection));

      /* if record found, retrieve the location. */
      if(mysqli_num_rows($result)>0)
      {
         $sql_query = "SELECT `latitude`, `longitude` FROM `user_location` WHERE phone='$phone'";
         $result = mysqli_query($connection, $sql_query);

         /* error in connection. */
         if(!$result)
            die("Error updating location: " . mysqli_error($connection));

         $row = mysqli_fetch_row($result);
         $reply = "Success!" . "$" . $row[0] . "$" . $row[1];
         echo $reply;
      }
      /* report error. */
      else
      {
         die("Error while tracking location." . "User not found.");
      }
   }

   mysqli_close($connection);

?>