package com.projemanag.firebase

import android.app.Activity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.projemanag.activities.*
import com.projemanag.models.Board
import com.projemanag.models.User
import com.projemanag.utils.constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()


    fun createboard(activity:CreateBoardActivity,board: Board)
    {
        mFirestore.collection(constants.boards).document(getcurrentuserid()).set(board, SetOptions.merge()).addOnSuccessListener {
            Toast.makeText(activity,"Board Creation Successfully",Toast.LENGTH_LONG).show()
            activity.boardCreatedSuccessfull()
        }.addOnFailureListener {
            exception->
            activity.hideprocessdialog()
        }
    }

    fun registerUser(activity:SignUpActivity, UserInfo: User)
    {
        mFirestore.collection(constants.users).document(getcurrentuserid()).set(UserInfo,
            SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
        }
            .addOnFailureListener {
                activity.hideprocessdialog()
                Toast.makeText(activity,it.toString(),Toast.LENGTH_LONG).show()
            }
        // set method https://www.google.com/search?q=setOptions.merge%28%29+in+android&rlz=1C1GCEJ_enIN1017IN1017&sxsrf=ALiCzsZNMAmovY-tZm06GlcTJ38d4c4MXA%3A1670231585144&ei=IbaNY7q9CJrZ1sQPueOJoAQ&ved=0ahUKEwj60czCkeL7AhWarJUCHblxAkQQ4dUDCA8&uact=5&oq=setOptions.merge%28%29+in+android&gs_lcp=Cgxnd3Mtd2l6LXNlcnAQAzIHCCEQoAEQCjIHCCEQoAEQCjIHCCEQoAEQCjIHCCEQoAEQCjoKCAAQRxDWBBCwAzoECCMQJzoFCAAQhgM6BQghEKABSgQIQRgASgQIRhgAUIQWWMwuYI0waAFwAXgBgAGwAogB5hqSAQgwLjEuMTIuMZgBAKABAcgBCMABAQ&sclient=gws-wiz-serp#fpstate=ive&vld=cid:c3a55f91,vid:TBr_5QH1EvQ
    }
    fun signinUser(activity: Activity)
    {
        mFirestore.collection(constants.users).document(getcurrentuserid()).get().addOnSuccessListener { Document->
            //toObject() method uses reflection to fill up your UserData model from the user document.
            val loginsuccess = Document.toObject(User::class.java)!!
            when(activity){
                is SignInActivity->{
                    activity.signinsuccess(loginsuccess)
                }
                is MainActivity->{
                    activity.updateNavigationUserDetails(loginsuccess)
                }
                is ProfileActivity->{
                    activity.updateProfileUserDetails(loginsuccess)
                }
            }

        }.addOnFailureListener {
            when(activity){
                is SignInActivity->{
                    activity.hideprocessdialog()
                }
                is MainActivity->{
                    activity.hideprocessdialog()
                }
            }

        }
    }
    fun updateUserProfileData(activity:ProfileActivity,userHashMap: HashMap<String,Any>){
        mFirestore.collection(constants.users).document(getcurrentuserid()).update(userHashMap).addOnSuccessListener {
            Toast.makeText(activity,"ProfileUpdated",Toast.LENGTH_LONG).show()
            activity.profileUpdateSuccess()
            activity.hideprocessdialog()
        }.addOnFailureListener {
            Toast.makeText(activity,"Profile was not updated",Toast.LENGTH_LONG).show()
        }

    }

    //#get_the_currently_signed-in_user use currentuser
    fun getcurrentuserid():String{
        val auth = FirebaseAuth.getInstance().currentUser
        var currentuserid = ""
        if(auth!=null)
        {
            currentuserid = auth.uid
        }
        return currentuserid
    }


}