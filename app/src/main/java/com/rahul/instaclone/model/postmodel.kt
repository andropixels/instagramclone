package com.rahul.instaclone.model

 class postmodel {

    var imageo:String=""
    var caption:String=""
     var publisher:String=""
     var username:String=""



    constructor()

    constructor(imageo:String,caption:String,publisher:String,username:String){

        this.imageo=imageo
        this.caption=caption
        this.publisher=publisher
        this.username=username

    }
}