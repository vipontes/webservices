package br.net.easify.apiwebservice.Utils;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class DynamicEventsHelper extends ItemTouchHelper.Callback {

    private DynamicEventsCallback callback;

    public DynamicEventsHelper(DynamicEventsCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int dragFlags = 0; // ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        callback.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        callback.onRemove(viewHolder.getAdapterPosition());
    }

    public interface DynamicEventsCallback {
        void onItemMove(int initialPosition, int finalPosition);
        void onRemove(int position);
    }
}
