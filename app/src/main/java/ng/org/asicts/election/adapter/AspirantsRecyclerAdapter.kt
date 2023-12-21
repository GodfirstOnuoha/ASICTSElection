package ng.org.asicts.election.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import ng.org.asicts.election.databinding.AspirantsLayoutBinding
import ng.org.asicts.election.model.Aspirants
import ng.org.asicts.election.util.Constants
import ng.org.asicts.election.util.Constants.SHARED_PREF
import ng.org.asicts.election.util.Constants.VOTED_COUNT
import ng.org.asicts.election.util.toast
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AspirantsRecyclerAdapter(
    private val aspirantsList: List<Aspirants>,
    private val baseLayout: LinearLayout,
    private val doneLayout: LinearLayout,
    private val cardView: MaterialCardView,
    private val db: FirebaseFirestore
) :
    RecyclerView.Adapter<AspirantsRecyclerAdapter.AspirantsViewHolder>() {

    private val prefs = baseLayout.context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AspirantsViewHolder {
        return AspirantsViewHolder(
            AspirantsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AspirantsViewHolder, position: Int) {
        val aspirants = aspirantsList[position]
        holder.bind(aspirants)
        holder.voteButton.setOnClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context)
                .setCancelable(false)
                .setTitle("Vote this aspirant?")
                .setMessage("You're about to vote for ${aspirants.name} as ${aspirants.position}. \n(This is permanent and can't be undone)")
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("Vote") { dialogInterface, _ ->
                    db.collection(Constants.ASPIRANTS).document(Constants.VOTES)
                        .collection(aspirants.position!!)
                        .document(aspirants.name!!)
                        .update("votes", FieldValue.increment(1))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                holder.itemView.context.toast("Successfully voted for ${aspirants.position}")
                            } else {
                                holder.itemView.context.toast("Error: \n" + task.exception?.message)
                            }
                        }

                    TransitionManager.beginDelayedTransition(baseLayout, AutoTransition())
                    cardView.visibility = View.GONE

                    var count = prefs.getInt(VOTED_COUNT, 0)

                    if (count == 9) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            baseLayout.visibility = View.GONE
                            doneLayout.visibility = View.VISIBLE
                        }, 700)
                    } else {
                        editor.putInt(VOTED_COUNT, ++count)
                        editor.apply()

                        if (count == 9) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                baseLayout.visibility = View.GONE
                                doneLayout.visibility = View.VISIBLE
                            }, 700)
                        }
                    }
                    dialogInterface.dismiss()
                }
                .create().show()
        }
    }

    override fun getItemCount(): Int = aspirantsList.size

    class AspirantsViewHolder(private val binding: AspirantsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val voteButton: Button = binding.voteButton

        fun bind(aspirants: Aspirants) {
            binding.aspirantImage.setImageResource(aspirants.image!!)
            binding.aspirantNameText.text = aspirants.name
            binding.aspirantDepartmentText.text = aspirants.department
            binding.aspirantLevelText.text = aspirants.level
        }
    }
}