package com.example.annunciator.fragments

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.annunciator.Crime
import com.example.annunciator.R
import com.example.annunciator.databinding.CrimeListFragmentBinding
import java.text.DateFormat

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    companion object {
        fun newInstance() = CrimeListFragment()
    }

    private var _binding: CrimeListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private var adapter: CrimeAdapter? = CrimeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CrimeListFragmentBinding.inflate(inflater, container, false)

        binding.buttonAddCrime.setOnClickListener { addNewCrime() }

        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.crimeRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crimesListLiveData.observe(viewLifecycleOwner) { crimes ->
                crimes?.let { updateUI(crimes) }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                addNewCrime()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

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
//        private val buttonRequiresPolicy: Button? = itemView.findViewById(R.id.buttonRequiresPolicy)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.handcuffs)

        init {
            itemView.setOnClickListener(this)

//            buttonRequiresPolicy?.let {
//                it.setOnClickListener {
//                    Toast.makeText(context, "Policy is coming", Toast.LENGTH_LONG).show()
//                }
//            }
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.getDateInstance().format(this.crime.date)

            when (crime.isSolved) {
                true -> {
                    solvedImageView.visibility = View.VISIBLE
                    titleTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    titleTextView.setTextColor(Color.parseColor("#FF9F9F9F"))
                    dateTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    dateTextView.setTextColor(Color.parseColor("#FF9F9F9F"))
                }
                else -> solvedImageView.visibility = View.GONE
            }

//            buttonRequiresPolicy?.let {
//                it.visibility = when(crime.isSolved) {
//                    true -> View.GONE
//                    else -> View.VISIBLE
//                }
//            }
        }

        override fun onClick(v: View?) {

            val action = CrimeListFragmentDirections.openCertainCrime(crime.id.toString()) // from.action(args)

            itemView.findNavController().navigate(action)
        }

    }

    private inner class CrimeAdapter : ListAdapter<Crime, CrimeHolder>(CrimeDiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {

//            val view = when (viewType) {
//                0 -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
//                else -> layoutInflater.inflate(R.layout.list_item_crime_serious, parent, false)
//            }
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)

            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(getItem(position))
        }

//        override fun getItemViewType(position: Int): Int {
//            return if(!crimes[position].requiresPolicy) 0
//            else 1
//        }



    }

    private fun updateUI(crimes: List<Crime>) {
        adapter?.submitList(crimes)
    }

    private fun addNewCrime() {
        val crime = Crime(title = "New crime")
        viewModel.addCrime(crime)
    }

    object CrimeDiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }

    }
}