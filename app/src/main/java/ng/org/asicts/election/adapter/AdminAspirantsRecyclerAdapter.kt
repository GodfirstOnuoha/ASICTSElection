package ng.org.asicts.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ng.org.asicts.election.databinding.AspirantsListItemBinding
import ng.org.asicts.election.model.Aspirants

class AdminAspirantsRecyclerAdapter(private val aspirantsList: List<Aspirants>) :
    RecyclerView.Adapter<AdminAspirantsRecyclerAdapter.AdminAspirantsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminAspirantsViewHolder {
        return AdminAspirantsViewHolder(
            AspirantsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AdminAspirantsViewHolder, position: Int) {
        val aspirants = aspirantsList[position]
        holder.bind(aspirants)
    }

    override fun getItemCount(): Int = aspirantsList.size

    class AdminAspirantsViewHolder(private val binding: AspirantsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(aspirants: Aspirants) {
            binding.aspirantImage.setImageResource(aspirants.image!!)
            binding.aspirantNameText.text = aspirants.name
            binding.aspirantDepartmentText.text = aspirants.department
            binding.aspirantLevelText.text = aspirants.level
            val votes = if (aspirants.votes > 1) "${aspirants.votes} votes" else "${aspirants.votes} vote"
            binding.votingText.text = votes
        }
    }
}