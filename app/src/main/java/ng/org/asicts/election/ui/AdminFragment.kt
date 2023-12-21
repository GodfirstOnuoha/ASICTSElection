package ng.org.asicts.election.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ng.org.asicts.election.adapter.AdminAspirantsRecyclerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ng.org.asicts.election.databinding.FragmentAdminBinding
import ng.org.asicts.election.model.Aspirants
import ng.org.asicts.election.model.Code
import ng.org.asicts.election.model.Election
import ng.org.asicts.election.model.Student
import ng.org.asicts.election.util.AspirantsData
import ng.org.asicts.election.util.CodeData
import ng.org.asicts.election.util.StudentData
import ng.org.asicts.election.util.Constants
import ng.org.asicts.election.util.Constants.ASICT
import ng.org.asicts.election.util.Constants.ASPIRANTS
import ng.org.asicts.election.util.Constants.ASSISTANT_SECRETARY_GENERAL
import ng.org.asicts.election.util.Constants.CODES
import ng.org.asicts.election.util.Constants.DIRECTOR_OF_ICT
import ng.org.asicts.election.util.Constants.DIRECTOR_OF_INFORMATION
import ng.org.asicts.election.util.Constants.DIRECTOR_OF_SOCIAL
import ng.org.asicts.election.util.Constants.DIRECTOR_OF_SPORTS
import ng.org.asicts.election.util.Constants.DIRECTOR_OF_WELFARE
import ng.org.asicts.election.util.Constants.ELECTION
import ng.org.asicts.election.util.Constants.FINANCIAL_SECRETARY
import ng.org.asicts.election.util.Constants.PRESIDENT
import ng.org.asicts.election.util.Constants.SECRETARY_GENERAL
import ng.org.asicts.election.util.Constants.STUDENTS
import ng.org.asicts.election.util.Constants.TREASURER
import ng.org.asicts.election.util.Constants.USED_CODES
import ng.org.asicts.election.util.Constants.VICE_PRESIDENT
import ng.org.asicts.election.util.Constants.VOTED_STUDENTS
import ng.org.asicts.election.util.toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var presidentListener: ListenerRegistration
    private lateinit var vicePresidentListener: ListenerRegistration
    private lateinit var secretaryListener: ListenerRegistration
    private lateinit var financialSecretaryListener: ListenerRegistration
    private lateinit var informationListener: ListenerRegistration
    private lateinit var ictListener: ListenerRegistration
    private lateinit var welfareListener: ListenerRegistration
    private lateinit var socialListener: ListenerRegistration
    private lateinit var sportsListener: ListenerRegistration
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.firestore

        loadRealtimeAspirants()

        db.collection(Constants.ASICT).document(ELECTION).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val election = task.result.toObject<Election>()
                    binding.electionSwitch.isChecked = election?.status == "STARTED"
                }
            }

        binding.electionSwitch.setOnClickListener {
            if (!binding.electionSwitch.isChecked) {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle("Stop Election?")
                    .setMessage("You're about to STOP the ASICTS Election.")
                    .setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        binding.electionSwitch.isChecked = true
                    }
                    .setPositiveButton("Continue") { dialogInterface, _ ->
                        binding.electionSwitch.isChecked = false

                        val simpleDateFormat =
                            SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val currentTime = simpleDateFormat.format(Date())

                        db.collection(ASICT)
                            .document(ELECTION)
                            .update(
                                mapOf(
                                    "status" to "ENDED",
                                    "endTime" to currentTime,
                                )
                            )
                            .addOnSuccessListener {
                                requireContext().toast("Election stopped successfully.")
                            }
                            .addOnFailureListener { e ->
                                requireContext().toast("Unable to stop election: \n${e.message}")
                            }

                        dialogInterface.dismiss()
                    }
                    .create().show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle("Start Election?")
                    .setMessage("You're about to START the ASICTS Election.")
                    .setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        binding.electionSwitch.isChecked = false
                    }
                    .setPositiveButton("Continue") { dialogInterface, _ ->
                        binding.electionSwitch.isChecked = true

                        val currentTime =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        val currentDate =
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        val election = Election("STARTED", currentDate, currentTime)

                        db.collection(ASICT)
                            .document(ELECTION)
                            .set(election)
                            .addOnSuccessListener {
                                requireContext().toast("Election started successfully.")
                            }
                            .addOnFailureListener { e ->
                                requireContext().toast("Unable to start election: \n${e.message}")
                            }
                        dialogInterface.dismiss()
                    }
                    .create().show()
            }
        }

        db.collection(ASPIRANTS).document(Constants.VOTES)
            .collection(AspirantsData.presidentAspirantList[0].position!!)
            .document(
                AspirantsData.presidentAspirantList[0].name!!
            ).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (!document.exists()) {
                        requireContext().toast("Loading Aspirants Details")
                        AspirantsData.presidentAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }

                        AspirantsData.vicePresidentAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }

                        AspirantsData.secretaryGeneralAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }

                        AspirantsData.financialSecretaryAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }

                        AspirantsData.directorOfInformationAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }

                        AspirantsData.directorOfIctAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }

                        AspirantsData.directorOfWelfareAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }

                        AspirantsData.directorOfSocialAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }

                        AspirantsData.directorOfSportsAspirantList.forEach { aspirants ->
                            db.collection(ASPIRANTS).document(Constants.VOTES)
                                .collection(aspirants.position!!)
                                .document(aspirants.name!!)
                                .set(aspirants)
                        }
                    } else {
                        requireContext().toast("Aspirants loaded already")
                    }
                    binding.progressLayout.visibility = View.GONE
                } else {
                    requireContext().toast("Unsuccessful task: \n" + task.exception?.message)
                }
            }

        /*StudentData.studentList.forEach { student ->
            db.collection(STUDENTS)
                .add(student)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        requireContext().toast("All Students have been uploaded")
                    } else {
                        requireContext().toast("Error: \n" + task.exception?.message)
                    }
                }
        }*/

        /*db.collection(VOTED_STUDENTS)
            .add(Student())

        db.collection(USED_CODES)
            .add(Code())*/

        /*CodeData.codeList.forEach { codes ->
            db.collection(CODES)
                .add(codes)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        requireContext().toast("All Codes have been uploaded")
                    } else {
                        requireContext().toast("Error: \n" + task.exception?.message)
                    }
                }
        }*/

        /*db.collection(STUDENTS).document(StudentData.studentList[0].regno!!).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentCode = task.result
                    if (!documentCode.exists()) {
                        requireContext().toast("Uploading All Student's Data")
                        StudentData.studentList.forEach { student ->
                            db.collection(STUDENTS)
                                .add(student)
                        }
                    } else {
                        requireContext().toast("Students loaded already")
                    }
                }
            }*/

        /*db.collection(CODES).document(CodeData.codeList[0].value!!).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentCode = task.result
                    if (!documentCode.exists()) {
                        requireContext().toast("Uploading All Codes")
                        CodeData.codeList.forEach { codes ->
                            db.collection(CODES)
                                .add(codes)
                        }
                    } else {
                        requireContext().toast("Codes loaded already")
                    }
                }
            }*/
    }

    private fun loadRealtimeAspirants() {
        presidentListener = db.collection(ASPIRANTS).document(Constants.VOTES).collection(PRESIDENT)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if ((snapshot != null) && !snapshot.isEmpty) {
                    val presidentList = arrayListOf<Aspirants>()
                    for (aspirants in snapshot) {
                        presidentList.add(aspirants.toObject())
                    }
                    binding.presidentRecycler.apply {
                        setHasFixedSize(true)
                        adapter = AdminAspirantsRecyclerAdapter(presidentList)
                    }
                }
            }

        vicePresidentListener =
            db.collection(ASPIRANTS).document(Constants.VOTES).collection(VICE_PRESIDENT)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val vicePresidentList = arrayListOf<Aspirants>()
                        for (aspirants in snapshot) {
                            vicePresidentList.add(aspirants.toObject())
                        }
                        binding.vicePresidentRecycler.apply {
                            setHasFixedSize(true)
                            adapter = AdminAspirantsRecyclerAdapter(vicePresidentList)
                        }
                    }
                }

        secretaryListener =
            db.collection(ASPIRANTS).document(Constants.VOTES).collection(SECRETARY_GENERAL)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val secretaryList = arrayListOf<Aspirants>()
                        for (aspirants in snapshot) {
                            secretaryList.add(aspirants.toObject())
                        }
                        binding.secretaryGeneralRecycler.apply {
                            setHasFixedSize(true)
                            adapter = AdminAspirantsRecyclerAdapter(secretaryList)
                        }
                    }
                }

        financialSecretaryListener =
            db.collection(ASPIRANTS).document(Constants.VOTES).collection(FINANCIAL_SECRETARY)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val financialSecretaryList = arrayListOf<Aspirants>()
                        for (aspirants in snapshot) {
                            financialSecretaryList.add(aspirants.toObject())
                        }
                        binding.financialSecretaryRecycler.apply {
                            setHasFixedSize(true)
                            adapter = AdminAspirantsRecyclerAdapter(financialSecretaryList)
                        }
                    }
                }

        informationListener =
            db.collection(ASPIRANTS).document(Constants.VOTES).collection(DIRECTOR_OF_INFORMATION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val directorOfInformationList = arrayListOf<Aspirants>()
                        for (aspirants in snapshot) {
                            directorOfInformationList.add(aspirants.toObject())
                        }
                        binding.directorOfInformationRecycler.apply {
                            setHasFixedSize(true)
                            adapter = AdminAspirantsRecyclerAdapter(directorOfInformationList)
                        }
                    }
                }

        ictListener = db.collection(ASPIRANTS).document(Constants.VOTES).collection(DIRECTOR_OF_ICT)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val directorOfIctList = arrayListOf<Aspirants>()
                    for (aspirants in snapshot) {
                        directorOfIctList.add(aspirants.toObject())
                    }
                    binding.directorOfIctRecycler.apply {
                        setHasFixedSize(true)
                        adapter = AdminAspirantsRecyclerAdapter(directorOfIctList)
                    }
                }
            }

        welfareListener =
            db.collection(ASPIRANTS).document(Constants.VOTES).collection(DIRECTOR_OF_WELFARE)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val directorOfWelfareList = arrayListOf<Aspirants>()
                        for (aspirants in snapshot) {
                            directorOfWelfareList.add(aspirants.toObject())
                        }
                        binding.directorOfWelfareRecycler.apply {
                            setHasFixedSize(true)
                            adapter = AdminAspirantsRecyclerAdapter(directorOfWelfareList)
                        }
                    }
                }

        socialListener =
            db.collection(ASPIRANTS).document(Constants.VOTES).collection(DIRECTOR_OF_SOCIAL)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val directorOfSocialList = arrayListOf<Aspirants>()
                        for (aspirants in snapshot) {
                            directorOfSocialList.add(aspirants.toObject())
                        }
                        binding.directorOfSocialRecycler.apply {
                            setHasFixedSize(true)
                            adapter = AdminAspirantsRecyclerAdapter(directorOfSocialList)
                        }
                    }
                }

        sportsListener =
            db.collection(ASPIRANTS).document(Constants.VOTES).collection(DIRECTOR_OF_SPORTS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val directorOfSportsList = arrayListOf<Aspirants>()
                        for (aspirants in snapshot) {
                            directorOfSportsList.add(aspirants.toObject())
                        }
                        binding.directorOfSportsRecycler.apply {
                            setHasFixedSize(true)
                            adapter = AdminAspirantsRecyclerAdapter(directorOfSportsList)
                        }
                    }
                }

        binding.progressLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presidentListener.remove()
        vicePresidentListener.remove()
        secretaryListener.remove()
        financialSecretaryListener.remove()
        informationListener.remove()
        ictListener.remove()
        welfareListener.remove()
        socialListener.remove()
        sportsListener.remove()
        _binding = null
    }
}