package com.example.annunciator.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.annunciator.Crime
import com.example.annunciator.R
import com.example.annunciator.databinding.FragmentCrimeBinding
import java.io.File
import java.text.DateFormat
import java.util.*

private const val TAG = "CrimeFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = "requestKey"
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2

class CrimeFragment : Fragment() {

    private var _binding: FragmentCrimeBinding? = null
    private val binding get() = _binding!!

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri : Uri

    private val arguments : CrimeFragmentArgs by navArgs()

    private lateinit var pickContact: ActivityResultLauncher<Void>

    private val viewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()

        val crimeID: UUID = UUID.fromString(arguments.crimeID)
        viewModel.loadCrime(crimeID)

        pickContact = registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = contactUri?.let {
                requireActivity().contentResolver.query (it, queryFields, null, null, null)
            }
            cursor?.use {
                if (it.count > 0) {
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    viewModel.saveCrime(crime)
                    binding.buttonChooseSuspect.text = suspect
                }
            }
        }
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
                    photoFile = viewModel.getPhotoFile(crime)
                    photoUri = FileProvider.getUriForFile(requireActivity(), "com.example.annunciator.fileprovider", photoFile)
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

        binding.buttonDate.setOnClickListener {

            val newCalendar = DatePickerFragment.newInstance(crime.date, REQUEST_DATE)
            newCalendar.show(childFragmentManager, DIALOG_DATE)
            childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner) { key, bundle ->

                val result = bundle.getSerializable("bundleKey")

                this.crime.date = result as Date
                updateUI()
            }
        }

        binding.buttonSendReport.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also {
                startActivity(it)
            }

        }

        binding.buttonChooseSuspect.apply {
            setOnClickListener {
                pickContact.launch(null)
            }
        }

        binding.buttonAddPhoto.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(
                    captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(cameraActivity.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

                startActivityForResult(captureImage, REQUEST_PHOTO)
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
        binding.buttonDate.text = DateFormat.getDateInstance().format(crime.date)
        binding.crimeSolved.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }

        if(crime.suspect.isNotEmpty()) {
            binding.buttonChooseSuspect.text = crime.suspect
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        }
        else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.getDateInstance().format(crime.date).toString()

        var suspect = if(crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        }
        else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }
}