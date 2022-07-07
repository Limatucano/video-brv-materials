package com.raywenderlich.android.creatures.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.creatures.R
import com.raywenderlich.android.creatures.app.Constants
//import com.raywenderlich.android.creatures.app.inflate
import com.raywenderlich.android.creatures.model.Creature
import kotlinx.android.synthetic.main.list_item_creature_card.view.*
import kotlinx.android.synthetic.main.list_item_creature_card.view.creatureCard
import kotlinx.android.synthetic.main.list_item_creature_card.view.creatureImage
import kotlinx.android.synthetic.main.list_item_creature_card.view.fullName
import kotlinx.android.synthetic.main.list_item_creature_card.view.nameHolder
import kotlinx.android.synthetic.main.list_item_creature_card_jupiter.view.*
import java.util.*

class CreatureCardAdapter(
    private val creatures: MutableList<Creature>,
    private val itemDragListener: ItemDragListener):
    RecyclerView.Adapter<CreatureCardAdapter.ViewHolder>(),
    ItemTouchHelperListener{

    enum class ScrollDirection {
        UP, DOWN
    }
    enum class ViewType {
        JUPITER, MARS, OTHER
    }
    var scrollDirection = ScrollDirection.DOWN
    var jupiterSpanSize = 2

    inner class ViewHolder(itemView: View) : View.OnClickListener, RecyclerView.ViewHolder(itemView) {
        private lateinit var creature: Creature

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(creature: Creature) {
            this.creature = creature
            val context = itemView.context
            val imageResource = context.resources.getIdentifier(creature.uri, null, context.packageName)
            itemView.creatureImage.setImageResource(imageResource)
            itemView.fullName.text = creature.fullName
            itemView.setOnTouchListener { _, motionEvent ->
                itemDragListener.onItemDrag(this)

                false
            }
            setBackgroundColors(context, imageResource)
            animateView(itemView)
        }

        override fun onClick(view: View?) {
            view?.let {
                val context = it.context
                val intent = CreatureActivity.newIntent(context, creature.id)
                context.startActivity(intent)
            }
        }
        private fun animateView(viewToAnimate: View){
            if (viewToAnimate.animation == null){
                val animId = if(scrollDirection == ScrollDirection.UP) R.anim.slide_from_top else R.anim.slide_from_bottom
                val animation = android.view.animation.AnimationUtils.loadAnimation(viewToAnimate.context,animId)
                viewToAnimate.animation = animation
            }
        }
        private fun setBackgroundColors(context: Context, imageResource: Int) {
            val image = BitmapFactory.decodeResource(context.resources, imageResource)
            Palette.from(image).generate { palette ->
                palette?.let {
                    val backgroundColor = it.getDominantColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                    itemView.creatureCard.setBackgroundColor(backgroundColor)
                    itemView.nameHolder.setBackgroundColor(backgroundColor)
                    val textColor = if (isColorDark(backgroundColor)) Color.WHITE else Color.BLACK
                    itemView.fullName.setTextColor(textColor)
                    if(itemView.slogan != null){
                        itemView.slogan.setTextColor(textColor)
                    }
                }
            }
        }

        private fun isColorDark(color: Int): Boolean {
            val darkness = 1 - (0.299 * Color.red(color) +
                                0.587 * Color.green(color) +
                                0.114 * Color.blue(color)) / 255
            return darkness >= 0.5
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = when(viewType){
        ViewType.JUPITER.ordinal -> ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_creature_card_jupiter,
                parent,
                false
            )
        )
        ViewType.MARS.ordinal -> ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_creature_card_mars,
                parent,
                false
            )
        )
        ViewType.OTHER.ordinal -> ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_creature_card,
                parent,
                false
            )
        )
        else -> throw IllegalArgumentException("invalid viewType")
    }

    override fun getItemViewType(position: Int): Int = when(creatures[position].planet){
        Constants.JUPITER -> ViewType.JUPITER.ordinal
        Constants.MARS -> ViewType.MARS.ordinal
        else -> ViewType.OTHER.ordinal
    }

    override fun getItemCount() = creatures.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(creatures[position])
    }

    fun spanSizeAtPosition(position: Int): Int = when(creatures[position].planet){
        Constants.JUPITER -> jupiterSpanSize
        else -> 1
    }

    override fun onItemMove(
        recyclerView: RecyclerView,
        fromPosition: Int,
        toPosition: Int
    ): Boolean {
        if(fromPosition < toPosition){
            for(i in fromPosition until toPosition){
                Collections.swap(creatures, i, i + 1)
            }
        }else{
            for(i in fromPosition downTo toPosition){
                if(i==0){
                    Collections.swap(creatures, i, i)
                }else {
                    Collections.swap(creatures, i, i - 1)
                }

            }
        }
        //Favorites.saveFavorites(compositeItems.map { it.creature.id }, recyclerView.context)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }


}