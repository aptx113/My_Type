package com.terricom.mytype.shaperecord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentShapeRecordBinding
import java.util.*

class ShapeRecordFragment: Fragment(), ShapeCalendarFragment.EventBetweenCalendarAndFragment {

    private val viewModel: ShapeRecordViewModel by lazy {
        ViewModelProviders.of(this).get(ShapeRecordViewModel::class.java)
    }
    private lateinit var binding: FragmentShapeRecordBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShapeRecordBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val shape = ShapeRecordFragmentArgs.fromBundle(arguments!!).selectedProperty

        binding.smartCustomCalendar.setEventHandler(this)
        binding.smartCustomCalendar.selectDateOut.observe(this, androidx.lifecycle.Observer {
            viewModel.setDate(it)
        })
        binding.shape = shape
        if (shape.timestamp != null){
            viewModel.updateShape(shape)
            binding.shapeRecordTitle.setText(App.applicationContext().getString(R.string.shaperecord_edit_accumulation))

            viewModel.setDate(shape.timestamp!!)
            viewModel.weight.value = shape.weight
            viewModel.bodyFat.value = shape.bodyFat
            viewModel.muscle.value = shape.muscle
            viewModel.bodyAge.value = shape.bodyAge
            viewModel.bodyWater.value = shape.bodyWater
            viewModel.tdee.value = shape.tdee
            viewModel.docId.value = shape.docId
            binding.textShapeSave.setText(App.applicationContext().getString(R.string.add_new_confirm))

            binding.smartCustomCalendar.filterdate(shape.timestamp)
            binding.smartCustomCalendar.getThisMonth()
            binding.smartCustomCalendar.setSelecteDate(shape.timestamp)

            binding.smartCustomCalendar.isSelected = true
            binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
                binding.smartCustomCalendar.updateCalendar()
            })

            binding.buttonShaperecordSave.setOnClickListener {
                it.background = App.applicationContext().getDrawable(R.color.colorSecondary)
                viewModel.updateShape2Firebase()
                viewModel.clearData()
            }

        } else {


        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.smartCustomCalendar.getThisMonth()
        binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.updateCalendar()
        })
        binding.smartCustomCalendar.selectDateOut.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.filterdate(it)
        })

        binding.buttonShaperecordSave.setOnClickListener {

            it.background = App.applicationContext().getDrawable(R.color.colorSecondary)
            if ((viewModel.weight.value ?: 0.0f)
                    .plus(viewModel.bodyWater.value ?: 0.0f)
                    .plus(viewModel.bodyFat.value ?: 0.0f)
                    .plus(viewModel.tdee.value ?: 0.0f)
                    .plus(viewModel.muscle.value ?: 0.0f)
                    .plus(viewModel.bodyAge.value ?: 0.0f) != 0.0f){
                if (isConnected()) {
                }else{
                    Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                    //告訴使用者網路無法使用
                }
                viewModel.addShape()
                viewModel.clearData()
            }else if ((viewModel.weight.value ?: 0.0f).plus(viewModel.bodyWater.value ?: 0.0f)
                    .plus(viewModel.bodyFat.value ?: 0.0f).plus(viewModel.tdee.value ?: 0.0f)
                    .plus(viewModel.muscle.value ?: 0.0f).plus(viewModel.bodyAge.value ?: 0.0f) == 0.0f){
                Toast.makeText(App.applicationContext(),resources.getText(R.string.shaperecord_input_hint), Toast.LENGTH_SHORT).show()
                it.background = App.applicationContext().getDrawable(R.color.colorMyType)
            }

        }
        }


        viewModel.date.observe(this, androidx.lifecycle.Observer {
            viewModel.getThisMonth()
        })

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).back2DiaryFragment()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
            (activity as MainActivity).back2DiaryFragment()

        }


        viewModel.addShapeResult.observe(this, androidx.lifecycle.Observer {
            if (it == true){
                this.findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.ADDED_SUCCESS))
            } else if (it == false){
                findNavController().navigate(NavigationDirections.navigateToMessageDialog(
                    MessageDialog.MessageType.MESSAGE.apply { value.message = getString(R.string.dialog_message_shape_record_failure)}
                ))
            }
        })

        binding.buttonShapeShowInfo.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToShapeRecordDialog())
        }

        return binding.root
    }

    override fun onCalendarNextPressed() {
        binding.smartCustomCalendar.selectDateOut.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.filterdate(it)
        })
        binding.smartCustomCalendar.getThisMonth()
        binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.updateCalendar()
        })

    }

    override fun onCalendarPreviousPressed() {
        binding.smartCustomCalendar.selectDateOut.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.filterdate(it)
        })
        binding.smartCustomCalendar.getThisMonth()
        binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.updateCalendar()
        })

    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).backFromEditPage()
    }

}