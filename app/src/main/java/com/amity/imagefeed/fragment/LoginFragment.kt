package com.amity.imagefeed.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amity.imagefeed.R
import com.amity.imagefeed.databinding.FragmentLoginBinding
import com.amity.imagefeed.viewmodel.LoginViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()

    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.loginButton?.setOnClickListener {
            val userId = binding?.userIdEditText?.text.toString()
            val displayName = binding?.displayNameEditText?.text.toString()
            viewModel.login(
                userId = userId,
                displayName = displayName,
                onLoginSuccess = { navigateToImageFeedPage() },
                onLoginError = { presentErrorDialog(it) }
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    private fun navigateToImageFeedPage() {
        findNavController().navigate(R.id.action_LoginFragment_to_ImageFeedFragment)
    }

    private fun presentErrorDialog(throwable: Throwable) {
        Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
    }
}