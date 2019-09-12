package com.terricom.mytype.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.data.Pazzle
import com.terricom.mytype.databinding.ItemProfileDreamBoardBinding

class PazzleAdapter(val viewModel: ProfileViewModel, val onClickListener: OnClickListener
) : ListAdapter<Pazzle, PazzleAdapter.PazzleViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (pazzle: Pazzle) -> Unit) {
        fun onClick(pazzle: Pazzle) = clickListener(pazzle)
    }


    class PazzleViewHolder(private var binding: ItemProfileDreamBoardBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(pazzle: Pazzle, viewModel: ProfileViewModel) {
            binding.pazzle = pazzle
                binding.puzzle1.visibility = if (pazzle.position!!.contains(0)) View.INVISIBLE else View.VISIBLE
                binding.puzzle2.visibility = if (pazzle.position!!.contains(1)) View.INVISIBLE else View.VISIBLE
                binding.puzzle3.visibility = if (pazzle.position!!.contains(2)) View.INVISIBLE else View.VISIBLE
                binding.puzzle4.visibility = if (pazzle.position!!.contains(3)) View.INVISIBLE else View.VISIBLE
                binding.puzzle5.visibility = if (pazzle.position!!.contains(4)) View.INVISIBLE else View.VISIBLE
                binding.puzzle6.visibility = if (pazzle.position!!.contains(5)) View.INVISIBLE else View.VISIBLE
                binding.puzzle7.visibility = if (pazzle.position!!.contains(6)) View.INVISIBLE else View.VISIBLE
                binding.puzzle8.visibility = if (pazzle.position!!.contains(7)) View.INVISIBLE else View.VISIBLE
                binding.puzzle9.visibility = if (pazzle.position!!.contains(8)) View.INVISIBLE else View.VISIBLE
                binding.puzzle10.visibility = if (pazzle.position!!.contains(9)) View.INVISIBLE else View.VISIBLE
                binding.puzzle11.visibility = if (pazzle.position!!.contains(10)) View.INVISIBLE else View.VISIBLE
                binding.puzzle12.visibility = if (pazzle.position!!.contains(11)) View.INVISIBLE else View.VISIBLE
                binding.puzzle13.visibility = if (pazzle.position!!.contains(12)) View.INVISIBLE else View.VISIBLE
                binding.puzzle14.visibility = if (pazzle.position!!.contains(13)) View.INVISIBLE else View.VISIBLE
                binding.puzzle15.visibility = if (pazzle.position!!.contains(14)) View.INVISIBLE else View.VISIBLE
                binding.puzzle16.visibility = if (pazzle.position!!.contains(15)) View.INVISIBLE else View.VISIBLE
                binding.puzzle17.visibility = if (pazzle.position!!.contains(16)) View.INVISIBLE else View.VISIBLE
                binding.puzzle18.visibility = if (pazzle.position!!.contains(17)) View.INVISIBLE else View.VISIBLE
                binding.puzzle19.visibility = if (pazzle.position!!.contains(18)) View.INVISIBLE else View.VISIBLE
                binding.puzzle20.visibility = if (pazzle.position!!.contains(19)) View.INVISIBLE else View.VISIBLE

            binding.lifecycleOwner =this
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun markAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun markDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Pazzle>() {
        override fun areItemsTheSame(oldItem: Pazzle, newItem: Pazzle): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: Pazzle, newItem: Pazzle): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PazzleViewHolder {
        return PazzleViewHolder(ItemProfileDreamBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: PazzleViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
        val product = getItem(position)

        product.let {
            holder.bind(product, viewModel)
            holder.itemView.setOnClickListener {
                onClickListener.onClick(product)
                notifyDataSetChanged()
                viewModel.setPazzle(product)

            }
        }
    }
    override fun onViewAttachedToWindow(holder: PazzleViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: PazzleViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}