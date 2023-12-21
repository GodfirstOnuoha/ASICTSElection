package ng.org.asicts.election.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ng.org.asicts.election.R
import ng.org.asicts.election.databinding.FragmentLoginBinding
import ng.org.asicts.election.model.Code
import ng.org.asicts.election.model.Election
import ng.org.asicts.election.model.Student
import ng.org.asicts.election.util.Constants
import ng.org.asicts.election.util.Constants.CODES
import ng.org.asicts.election.util.Constants.STUDENTS
import ng.org.asicts.election.util.Constants.USED_CODES
import ng.org.asicts.election.util.Constants.VOTED_STUDENTS
import ng.org.asicts.election.util.hideKeyboard
import ng.org.asicts.election.util.shakeError
import ng.org.asicts.election.util.toast
import ng.org.asicts.election.util.vibrate


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var election: Election
    private lateinit var electionListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.firestore

        election = Election()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressLayout.visibility = View.GONE
        }, 3000)

        electionListener = db.collection(Constants.ASICT)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if ((snapshot != null) && !snapshot.isEmpty) {
                    for (elect in snapshot) {
                        election = elect.toObject()

                    }
                }
            }

        binding.regnoEditText.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    binding.regnoEditText.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.passwordEditText.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    binding.passwordEditText.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.loginButton.setOnClickListener {
            binding.regnoEditText.error = null
            binding.passwordEditText.error = null
            requireActivity().hideKeyboard()

            if (binding.regnoEditText.editText?.text.toString() == "20171055955" && binding.passwordEditText.editText?.text.toString() == "55955017102") {
                binding.progressLayout.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
                }, 100)
            } else {
                when (election.status) {
                    "STARTED" -> {
                        validateInputs()
                    }
                    "ENDED" -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setCancelable(false)
                            .setTitle("Election Update")
                            .setMessage("The ASICTS Election have ended.")
                            .setPositiveButton("Ok") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create().show()
                    }
                    else -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setCancelable(false)
                            .setTitle("Election Update")
                            .setMessage("The ASICTS Election have not started yet.")
                            .setPositiveButton("Ok") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create().show()
                    }
                }
            }
        }
    }

    private fun validateInputs() {
        binding.regnoEditText.error = null
        binding.passwordEditText.error = null

        val student = Student(binding.regnoEditText.editText?.text.toString())
        val code = Code(binding.passwordEditText.editText?.text.toString())

        if (student.regno!!.isEmpty()) {
            requireContext().toast("Registration Number required.")
            binding.regnoEditText.startAnimation(requireContext().shakeError())
            requireContext().vibrate()
            binding.regnoEditText.error = "Registration Number required"
            binding.regnoEditText.requestFocus()
            return
        } else {
            binding.loginButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE

            db.collection(STUDENTS)
                .whereEqualTo("regno", student.regno)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val studentSize = querySnapshot.documents.size
                    if (studentSize < 1) {
                        requireContext().toast("Your Registration Number is incorrect or doesn't exist.")
                        binding.regnoEditText.startAnimation(requireContext().shakeError())
                        requireContext().vibrate()
                        binding.regnoEditText.error =
                            "Your Registration Number is incorrect or doesn't exist."
                        binding.regnoEditText.requestFocus()

                        binding.loginButton.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    } else {
                        db.collection(VOTED_STUDENTS)
                            .whereEqualTo("regno", student.regno)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                val votedStudentSize = snapshot.documents.size
                                if (votedStudentSize > 0) {
                                    requireContext().toast("You have voted already.")
                                    binding.regnoEditText.startAnimation(requireContext().shakeError())
                                    requireContext().vibrate()
                                    binding.regnoEditText.error = "You have voted already."
                                    binding.regnoEditText.requestFocus()

                                    binding.loginButton.isEnabled = true
                                    binding.progressBar.visibility = View.GONE
                                } else {
                                    if (code.value!!.isEmpty()) {
                                        requireContext().toast("Password required.")
                                        binding.passwordEditText.startAnimation(requireContext().shakeError())
                                        requireContext().vibrate()
                                        binding.passwordEditText.error = "Password required"
                                        binding.passwordEditText.requestFocus()

                                        binding.progressBar.visibility = View.GONE
                                        binding.loginButton.isEnabled = true

                                    } else {
                                        db.collection(CODES)
                                            .whereEqualTo("value", code.value)
                                            .get()
                                            .addOnSuccessListener { taskSanapshot ->
                                                val codeSize = taskSanapshot.documents.size
                                                if (codeSize < 1) {
                                                    requireContext().toast("Invalid Password")
                                                    binding.passwordEditText.startAnimation(
                                                        requireContext().shakeError()
                                                    )
                                                    requireContext().vibrate()
                                                    binding.passwordEditText.error =
                                                        "Invalid Password"
                                                    binding.passwordEditText.requestFocus()

                                                    binding.progressBar.visibility = View.GONE
                                                    binding.loginButton.isEnabled = true
                                                } else {
                                                    db.collection(USED_CODES)
                                                        .whereEqualTo("value", code.value)
                                                        .get()
                                                        .addOnSuccessListener {
                                                            val usedCodeSize = it.documents.size
                                                            if (usedCodeSize > 0) {
                                                                requireContext().toast("Invalid! This code have been used already.")
                                                                binding.passwordEditText.startAnimation(
                                                                    requireContext().shakeError()
                                                                )
                                                                requireContext().vibrate()
                                                                binding.passwordEditText.error =
                                                                    "Invalid! This code have been used already."
                                                                binding.passwordEditText.requestFocus()

                                                                binding.progressBar.visibility = View.GONE
                                                                binding.loginButton.isEnabled = true
                                                            } else {
                                                                binding.progressBar.visibility = View.GONE
                                                                binding.loginButton.isEnabled = true

                                                                db.collection(VOTED_STUDENTS)
                                                                    .add(student)

                                                                db.collection(USED_CODES)
                                                                    .add(code)

                                                                //StudentData.votedStudentList.add(student)
                                                                //CodeData.usedCodeList.add(code)

                                                                binding.progressLayout.visibility = View.VISIBLE
                                                                Handler(Looper.getMainLooper()).postDelayed({
                                                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                                                }, 100)
                                                            }
                                                        }
                                                }
                                            }


                                    }
                                }
                            }
                    }
                }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        electionListener.remove()
        _binding = null
    }
}