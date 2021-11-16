package com.example.annunciator.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.annunciator.Crime
import com.example.annunciator.R
import com.example.annunciator.databinding.FragmentCrimeBinding
import java.text.DateFormat
import java.util.*

private const val TAG = "CrimeFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = "requestKey"

class CrimeFragment : Fragment() {

    private var _binding: FragmentCrimeBinding? = null
    private val binding get() = _binding!!

    private lateinit var crime: Crime

    private val arguments : CrimeFragmentArgs by navArgs()

    private val viewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()

        val crimeID: UUID = UUID.fromString(arguments.crimeID)
        viewModel.loadCrime(crimeID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()  
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }

        binding.editText.addTextChangedListener(textWatcher)

        binding.crimeSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }

        binding.button.setOnClickListener {

            val newCalendar = DatePickerFragment.newInstance(crime.date, REQUEST_DATE)
            newCalendar.show(childFragmentManager, DIALOG_DATE)
            childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner) { key, bundle ->

                val result = bundle.getSerializable("bundleKey")

                this.crime.date = result as Date
                updateUI()
            }

        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveCrime(crime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI() {
        binding.titleCrime.text = crime.title
        binding.button.text = DateFormat.getDateInstance().format(crime.date)
        binding.crimeSolved.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }
}