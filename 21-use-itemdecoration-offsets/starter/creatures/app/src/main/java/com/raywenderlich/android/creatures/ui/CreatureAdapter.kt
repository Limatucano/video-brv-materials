package com.raywenderlich.android.creatures.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.creatures.R
import com.raywenderlich.android.creatures.model.CompositeItem
import com.raywenderlich.android.creatures.model.Creature
import com.raywenderlich.android.creatures.model.Favorites
import kotlinx.android.synthetic.main.list_item_creature.view.*
import kotlinx.android.synthetic.main.list_item_planet_header.view.*
import java.util.*

class CreatureAdapter(private val compositeItems: MutableList<CompositeItem>): RecyclerView.Adapter<CreatureAdapter.ViewHolder>(), ItemTouchHelperListener {

    enum class ViewType {
        HEADER, CREATURE
    }
    class ViewHolder(itemView: View) : View.OnClickListener, RecyclerView.ViewHolder(itemView) {
        private lateinit var creature: Creature

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(composite: CompositeItem) {
            if(composite.isHeader){
                itemView.headerName?.text = composite.header.name
            }else{
                this.creature = composite.creature
                val context = itemView.context
                itemView.creatureImage.setImageResource(
                    context.resources.getIdentifier(creature.uri, null, context.packageName))
                itemView.fullName.text = creature.fullName
                itemView.nickname.text = creature.nickname
                animateView(itemView)
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                val context = it.context
                val intent = CreatureActivity.newIntent(context, creature.id)
                context.startActivity(intent)
            }
        }

        private fun animateView(viewToAnimate : View){
            if(viewToAnimate.animation == null){
                val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.scale_animation)
                viewToAnimate.animation = animation
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType) {
            ViewType.HEADER.ordinal -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_planet_header, parent, false))
            ViewType.CREATURE.ordinal -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_creature, parent, false))
            else -> throw IllegalArgumentException("Ilegal viewType value")
        }
    }

    override fun getItemCount() = compositeItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(compositeItems[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when(compositeItems[position].isHeader){
            true -> ViewType.HEADER.ordinal
            false -> ViewType.CREATURE.ordinal
        }
    }
    fun updateCreatures(creatures: List<CompositeItem>) {
        this.compositeItems.clear()
        this.compositeItems.addAll(creatures)
        notifyDataSetChanged()
    }

    override fun onItemMove(
        recyclerView: RecyclerView,
        fromPosition: Int,
        toPosition: Int
    ): Boolean {
        if(fromPosition < toPosition){
            for(i in fromPosition until toPosition){
                Collections.swap(compositeItems, i, i + 1)
            }
        }else{
            for(i in fromPosition downTo toPosition){
                Collections.swap(compositeItems, i, i - 1)
            }
        }
        Favorites.saveFavorites(compositeItems.map { it.creature.id }, recyclerView.context)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

}