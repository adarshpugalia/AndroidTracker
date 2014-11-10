<?php

   $server = 'localhost';
   $username = 'root';
   $password = '123';
   $database = 'android_tracker';

   /* creating connection to the database. */
   $connection = mysqli_connect($server, $username, $password, $database);

   /* checking for successful connection. */
   if(!$connection)
      die("Connection failed: " . mysqli_connect_error());
   
   /* creating the sql query. */
   $sql_query = "CREATE TABLE users
      (
         phone VARCHAR(25) PRIMARY KEY,
         GCM_ID TEXT NOT NULL,
         name VARCHAR(50) NOT NULL,
         passowrd TEXT NOT NULL
      )"; 

   /* querying the database. */
   if(mysqli_query($connection, $sql_query))
      echo "Table users created successfully!";
   else
      die("Error creating table: " . mysqli_error($connection));

   mysqli_close($connection);
?>