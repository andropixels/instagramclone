package com.rahul.instaclone.model

 class usermodel {

     var username: String = ""
     var fullname: String = ""
     var bio: String = ""
     var image: String = ""
     var uid:String=""
     //this is amodel class in which the  data from ourdatabase will get stored
     //this class needs to be passed with the secondary constrcutor which are exctly same as of the database
     //also this class always has an empty constructor and a construtor with the attributes
     //getter and setter doesnt neddd to pass in kotlin code



     constructor()
     constructor(username: String, fullname: String, bio: String, image: String,uid:String) {

         this.username = username
         this.fullname = fullname
         this.bio = bio
         this.image = image
         this.uid=uid


     }
 }