package com.bartukaraa.chatapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bartukaraa.chatapp.databinding.FragmentChatBinding
import com.bartukaraa.chatapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChatFragment : Fragment() {

    private lateinit var binding : FragmentChatBinding
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : ChatRvAdapter
    private  var chats  = arrayListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatBinding.inflate(inflater,container,false)
        val view = binding.root


        binding.toolbarMain.title = "ChattApp"
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarMain)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_cikis) {
                    //cikis butonu
                    auth.signOut()
                    val aciton = ChatFragmentDirections.actionChatFragmentToLoginFragment()
                    findNavController().navigate(aciton)
                }

                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)


        adapter = ChatRvAdapter()
        binding.chatRecycler.adapter = adapter
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.sendButton.setOnClickListener {
            // send butona tıklandıgında yapılacaklar.

            val chatText = binding.chatText.text.toString()
            val user = auth.currentUser!!.email
            val date = FieldValue.serverTimestamp()

            val dataMap = HashMap<String,Any>()
            dataMap.put("text",chatText)
            dataMap.put("user",user!!)
            dataMap.put("date",date)

            firestore.collection("Chats").add(dataMap).addOnSuccessListener {
                // başarılı eklenirse
                binding.chatText.setText("")
            }.addOnFailureListener {
                // Hata olursa
                binding.chatText.setText("")
                Toast.makeText(requireContext(),"Bir hata Oluştu",Toast.LENGTH_LONG).show()
            }
        }

        firestore.collection("Chats").orderBy("date",Query.Direction.ASCENDING).addSnapshotListener { value, error ->

            if(error != null){
                Toast.makeText(requireContext(),"Error",Toast.LENGTH_LONG).show()
            }else{
                if(value != null){
                    if(value.isEmpty){
                        Toast.makeText(requireContext(),"Mesaj Yok",Toast.LENGTH_LONG).show()
                    }else{

                        val documents = value.documents
                        chats.clear()
                        for(document in documents){
                            val text = document.get("text") as String
                            val user = document.get("user") as String
                            val chat = Chat(user,text)
                            chats.add(chat)
                            adapter.chats = chats
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }



        }


    }





}