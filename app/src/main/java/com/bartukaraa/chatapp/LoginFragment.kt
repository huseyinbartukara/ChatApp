package com.bartukaraa.chatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bartukaraa.chatapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        val currentUser = auth.currentUser

        if(currentUser != null){
            val aciton = LoginFragmentDirections.actionLoginFragmentToChatFragment()
            findNavController().navigate(aciton)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.signupButton.setOnClickListener {
            // kullanıcı kayıt işlemi yapılacak

            val aciton = LoginFragmentDirections.actionLoginFragmentToChatFragment()
            findNavController().navigate(aciton)

            auth.createUserWithEmailAndPassword(binding.emailText.text.toString(), binding.passwordText.text.toString()).addOnSuccessListener {
                // kullanıcı oluşturuldu
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Bir hata oluştu",Toast.LENGTH_LONG).show()
            }


        }

        binding.loginButton.setOnClickListener {
            // kullanıcı giriş işlemi yapılacak

            auth.signInWithEmailAndPassword(binding.emailText.text.toString(), binding.passwordText.text.toString()).addOnSuccessListener {
                // giriş yapılırsa
                val aciton = LoginFragmentDirections.actionLoginFragmentToChatFragment()
                findNavController().navigate(aciton)
            }.addOnFailureListener {
                // hata olursa
                Toast.makeText(requireContext(),"Bir hata oluştu",Toast.LENGTH_LONG).show()
            }

        }


    }


}
