package com.raywenderlich.android.creatures.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.creatures.R
import com.raywenderlich.android.creatures.app.inflate
import com.raywenderlich.android.creatures.model.Creature
import kotlinx.android.synthetic.main.list_item_creature.view.*

class CreatureAdapter(private val creatures: List<Creature>) :
    RecyclerView.Adapter<CreatureAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.list_item_creature))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(creatures[position])
    }

    override fun getItemCount(): Int = creatures.size

    class ViewHolder(itemView: View) : View.OnClickListener,RecyclerView.ViewHolder(itemView) {
        private lateinit var creature: Creature

        init {
            itemView.setOnClickListener (this)
        }
        fun bind(creature: Creature) {
            this.creature = creature
            val context = itemView.context
            itemView.creatureImage.setImageResource(
                context.resources.getIdentifier(
                    creature.uri,
                    null,
                    context.packageName
                )
            )
            itemView.creatureName.text = creature.fullName
            itemView.creatureNickname.text = creature.nickname
        }

        override fun onClick(v: View?) {
            v?.let {
                val context = it.context
                val intent = CreatureActivity.newIntent(context,creature.id)
                context.startActivity(intent)
            }
        }
    }
}