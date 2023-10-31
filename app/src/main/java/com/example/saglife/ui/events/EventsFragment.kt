package com.example.saglife.ui.events

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saglife.Event_information
import com.example.saglife.R
import com.example.saglife.databinding.FragmentCalendarBinding
import com.example.saglife.models.EventItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Date

class EventsFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val db = Firebase.firestore

        db.collection("event").get().addOnSuccessListener { result ->
            for (document in result) {
                println("${document.id} => ${document.data}")
                val name = document.get("Name").toString()
                val dateStart = Date()
                val dateEnd = Date()
                val description = document.get("Description").toString()
                val photoPath = document.get("Photo").toString()

            }
            val listView : ListView = binding.eventListView





        }

        /*val intent = Intent(context, Event_information::class.java)
        startActivity(intent)
        onDestroyView()*/


        println("test")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}