package com.raywenderlich.android.creatures.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.creatures.R
import com.raywenderlich.android.creatures.model.CompositeItem
import com.raywenderlich.android.creatures.model.Creature
import com.raywenderlich.android.creatures.model.Favorites
import kotlinx.android.synthetic.main.list_item_creature.view.*
import kotlinx.android.synthetic.main.list_item_planet_header.view.*
import java.util.*

class CreatureAdapter(
    private val compositeItems: MutableList<CompositeItem>,
    private val itemDragListener: ItemDragListener):
    RecyclerView.Adapter<CreatureAdapter.ViewHolder>(),
    ItemTouchHelperListener {

    enum class ViewType {
        HEADER, CREATURE
    }
    inner class ViewHolder(itemView: View) :
        View.OnClickListener,
        RecyclerView.ViewHolder(itemView),
        ItemSelectedListener {

        private lateinit var creature: Creature

        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("ClickableViewAccessibility")
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
                itemView.handle.setOnTouchListener { _, motionEvent ->
                    if(motionEvent.action == MotionEvent.ACTION_DOWN){
                        itemDragListener.onItemDrag(this)
                    }
                    false
                }
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

        override fun onItemSelected() {
            itemView.listeItemContainer.setBackgroundColor(
                ContextCompat.getColor(itemView.context, R.color.selectedItem)
            )
        }

        override fun onItemCleared() {
            itemView.listeItemContainer.setBackgroundColor(0)
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
        //Favorites.saveFavorites(compositeItems.map { it.creature.id }, recyclerView.context)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(viewHolder: RecyclerView.ViewHolder, position: Int) {
        Favorites.removeFavorite(compositeItems[position].creature, viewHolder.itemView.context)
        compositeItems.removeAt(position)
        notifyItemRemoved(position)
    }

}