package serhii.bulakh.educationandroidchart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import serhii.bulakh.educationandroidchart.R;
import serhii.bulakh.educationandroidchart.models.ExpenseDetail;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<ExpenseDetail> expenseList;
    private OnExpenseClickListener listener;

    public ExpenseAdapter(List<ExpenseDetail> expenseList, OnExpenseClickListener listener) {
        this.expenseList = new ArrayList<>(expenseList);
        this.listener = listener;
    }
    public void updateList(List<ExpenseDetail> newList) {
        expenseList.clear();
        expenseList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseDetail expenseDetail = expenseList.get(position);
        holder.bind(expenseDetail, position);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView textCategory;
        private TextView textAmount;
        private TextView textComment;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.textCategory);
            textAmount = itemView.findViewById(R.id.textAmount);
            textComment = itemView.findViewById(R.id.textComment);
        }

        public void bind(ExpenseDetail expenseDetail, int position) {
            textCategory.setText(expenseDetail.getCategory());
            textAmount.setText(String.format(Locale.getDefault(), "%.2f", expenseDetail.getAmount()));
            textComment.setText(expenseDetail.getComment());

            itemView.setOnClickListener(v -> listener.onExpenseEdit(position, expenseDetail.getCategory(), expenseDetail.getAmount(), expenseDetail.getComment()));
            itemView.setOnLongClickListener(v -> {
                listener.onExpenseDelete(position);
                return true;
            });
        }
    }

    public interface OnExpenseClickListener {
        void onExpenseEdit(int position, String newCategory, float newAmount, String newComment);
        void onExpenseDelete(int position);
    }
}


