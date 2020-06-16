#!/usr/bin/expect

#If it all goes pear shaped the script will timeout after 20 seconds.
set timeout 20
#Second argument is assigned to the variable user
set user [lindex $argv 0]
#Third argument is assigned to the variable password
set password [lindex $argv 1]
#This spawns the telnet program and connects it to the variable name
spawn telnet creeper.ktwit.net 8080 
#The script expects login
expect "username: " 
#The script sends the user variable
send "$user\n"
#The script expects Password
expect "password: "
#The script sends the password variable
send "$password\n"
#This hands control of the keyboard over two you (Nice expect feature!)
interact
