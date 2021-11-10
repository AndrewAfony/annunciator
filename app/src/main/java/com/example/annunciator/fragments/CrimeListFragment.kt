package com.example.annunciator.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.annunciator.Crime
import com.example.annunciator.R
import com.example.annunciator.databinding.CrimeListFragmentBinding
import java.util.*

class CrimeListFragment : Fragment() {

    companion object {
        fun newInstance() = CrimeListFragment()
    }

    private var _binding: CrimeListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private var adapter: CrimeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CrimeListFragmentBinding.inflate(inflater, container, false)

        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class CrimeHolder(view: View): RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val buttonRequiresPolicy: Button? = itemView.findViewById(R.id.buttonRequiresPolicy)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.handcuffs)

        init {
            itemView.setOnClickListener(this)

            buttonRequiresPolicy?.let {
                it.setOnClickListener {
                    Toast.makeText(context, "Policy is coming", Toast.LENGTH_LONG).show()
                }
            }
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.format(Date())

            solvedImageView.visibility = when(crime.isSolved) {
                true -> View.VISIBLE
                else -> View.GONE
            }

            buttonRequiresPolicy?.let {
                it.visibility = when(crime.isSolved) {
                    true -> View.GONE
                    else -> View.VISIBLE
                }
            }
        }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed", Toast.LENGTH_LONG).show()
        }

    }

    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {

            val view = when (viewType) {
                0 -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                else -> layoutInflater.inflate(R.layout.list_item_crime_serious, parent, false)
            }
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

        override fun getItemViewType(position: Int): Int {
            return if(!crimes[position].requiresPolicy) 0
            else 1
        }

    }

    private fun updateUI() {
        val crimes = viewModel.crimes
        adapter = CrimeAdapter(crimes)
        binding.crimeRecyclerView.adapter = adapter
    }
}