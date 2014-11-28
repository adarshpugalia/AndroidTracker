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
   	$latitude = $_POST['Latitude'];
      $longitude = $_POST['Longitude'];

      /* getting the record from the databse. */
      $sql_query = "SELECT * FROM `user_location` WHERE `phone`='$phone' LIMIT 1";
      $result = mysqli_query($connection, $sql_query);

      /* error in connection. */
      if(!$result)
         die("Error updating location: " . mysqli_error($connection));

      /* if record found, update the location. */
      if(mysqli_num_rows($result)>0)
      {
         $sql_query = "UPDATE `user_location` SET `latitude`='$latitude',`longitude`='$longitude' WHERE `phone`='$phone'";
         if(mysqli_query($connection, $sql_query))
            echo "Success from update!";
         else
            die("Error updating location: " . mysqli_error($connection));
      }
      /* insert the location of the new user. */
      else
      {
         /* updating the location of the new user. */
         $sql_query = "INSERT INTO `user_location` (phone, latitude, longitude)
                  VALUES ('$phone', '$latitude', '$longitude')";

         if(mysqli_query($connection, $sql_query))
            echo "Success from insert!";
         else
            die("Error updating location: " . mysqli_error($connection));
      }
   }

   mysqli_close($connection);

?>