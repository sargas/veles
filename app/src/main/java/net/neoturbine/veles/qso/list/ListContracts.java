package net.neoturbine.veles.qso.list;

@SuppressWarnings("WeakerAccess")
public interface ListContracts {
    interface View {
        void launchAddQSO();
        void openID(long id);
    }
}
