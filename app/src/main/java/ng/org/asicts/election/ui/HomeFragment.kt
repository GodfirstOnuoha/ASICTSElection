package ng.org.asicts.election.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ng.org.asicts.election.adapter.AspirantsRecyclerAdapter
import ng.org.asicts.election.databinding.FragmentHomeBinding
import ng.org.asicts.election.util.AspirantsData.directorOfIctAspirantList
import ng.org.asicts.election.util.AspirantsData.directorOfInformationAspirantList
import ng.org.asicts.election.util.AspirantsData.directorOfSocialAspirantList
import ng.org.asicts.election.util.AspirantsData.directorOfSportsAspirantList
import ng.org.asicts.election.util.AspirantsData.directorOfWelfareAspirantList
import ng.org.asicts.election.util.AspirantsData.financialSecretaryAspirantList
import ng.org.asicts.election.util.AspirantsData.presidentAspirantList
import ng.org.asicts.election.util.AspirantsData.secretaryGeneralAspirantList
import ng.org.asicts.election.util.AspirantsData.vicePresidentAspirantList
import ng.org.asicts.election.util.Constants.SHARED_PREF
import ng.org.asicts.election.util.Constants.VOTED_COUNT

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.firestore

        val count = 0
        val pref = requireContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(VOTED_COUNT, count)
        editor.apply()

        binding.presidentRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                presidentAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.presidentCardView, db
            )
        }

        binding.vicePresidentRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                vicePresidentAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.vicePresidentCardView, db
            )
        }

        binding.secretaryGeneralRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                secretaryGeneralAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.secretaryGeneralCardView, db
            )
        }

        binding.financialSecretaryRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                financialSecretaryAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.financialSecretaryCardView, db
            )
        }

        binding.directorOfInformationRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                directorOfInformationAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.directorOfInformationCardView, db
            )
        }

        binding.directorOfIctRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                directorOfIctAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.directorOfIctCardView, db
            )
        }

        binding.directorOfWelfareRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                directorOfWelfareAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.directorOfWelfareCardView, db
            )
        }

        binding.directorOfSocialRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                directorOfSocialAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.directorOfSocialCardView, db
            )
        }

        binding.directorOfSportsRecycler.apply {
            setHasFixedSize(true)
            adapter = AspirantsRecyclerAdapter(
                directorOfSportsAspirantList,
                binding.baseLayout, binding.doneLayout,
                binding.directorOfSportsCardView, db
            )
        }

        binding.signOutButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            //findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}