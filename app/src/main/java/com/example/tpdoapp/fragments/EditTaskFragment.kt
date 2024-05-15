package com.example.tpdoapp.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tpdoapp.MainActivity
import com.example.tpdoapp.R
import com.example.tpdoapp.databinding.FragmentEditTaskBinding
import com.example.tpdoapp.model.Task
import com.example.tpdoapp.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTaskFragment : Fragment(R.layout.fragment_edit_task), MenuProvider {

    private var editTaskBinding: FragmentEditTaskBinding? = null
    private val binding get() = editTaskBinding

    private lateinit var tasksViewModel: TaskViewModel
    private lateinit var currentTask: Task

    private val args: EditTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editTaskBinding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        tasksViewModel = (activity as MainActivity).taskViewModel
        currentTask = args.task!!

        binding?.let { binding ->
            binding.editNoteTitle.setText(currentTask.taskTitle)
            binding.editNotePriority.setText(currentTask.taskDeadline) // Setting description to editNotePriority
            binding.editNoteDeadlineButton.text = currentTask.taskDesc // Setting task deadline date to editNoteDeadlineButton
            binding.editNoteDesc.setText(currentTask.taskPriority) // Setting task priority data to editNoteDesc

            binding.editNoteFab.setOnClickListener {
                val taskTitle = binding.editNoteTitle.text.toString().trim()
                val taskDesc = binding.editNotePriority.text.toString().trim() // Fetching task description
                val taskPriority = binding.editNoteDesc.text.toString().trim() // Fetching task priority data
                val taskDeadline = binding.editNoteDeadlineButton.text.toString().trim() // Fetching task deadline date

                if (taskTitle.isNotEmpty()) {
                    val task = Task(currentTask.id, taskTitle, taskDesc, taskPriority, taskDeadline)
                    tasksViewModel.updateTask(task)
                    view.findNavController().popBackStack(R.id.homeFragment, false)
                } else {
                    Toast.makeText(context, "Please enter task title", Toast.LENGTH_SHORT).show()
                }
            }

            binding.editNoteDeadlineButton.setOnClickListener {
                showDatePicker()
            }
        }
    }

    private fun deleteTask() {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle("Delete Task")
            setMessage("Do you want to delete this task?")
            setPositiveButton("Delete") { _, _ ->
                tasksViewModel.deleteTask(currentTask)
                Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                binding?.editNoteDeadlineButton?.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_task, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteMenu -> {
                deleteTask()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editTaskBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // Handle menu creation if needed
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Handle menu item selection if needed
        return false
    }
}
