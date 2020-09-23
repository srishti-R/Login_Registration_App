package com.example.login_registration

data class LoginObject(val username:String, val password:String){
    override fun toString(): String {
        return "{username: $username, password: $password}"
    }
}