package net.neoturbine.veles.qso.list;

import android.support.v7.widget.RecyclerView;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.databinding.QsoListContentBinding;

class QSOItemViewHolder extends RecyclerView.ViewHolder {
    private final QSOItemViewModel mViewModel;
    private final QsoListContentBinding mBinding;

    QSOItemViewHolder(QsoListContentBinding binding, QSOItemViewModel viewModel) {
        super(binding.getRoot());
        mViewModel = viewModel;
        binding.setViewmodel(mViewModel);
        mBinding = binding;
    }

    void bind(QSO qso) {
        mViewModel.setQSO(qso);
        mBinding.executePendingBindings();
    }
}
