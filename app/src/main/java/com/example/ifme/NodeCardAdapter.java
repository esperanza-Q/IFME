package com.example.ifme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NodeCardAdapter extends RecyclerView.Adapter<NodeCardAdapter.CardViewHolder> {

    private final List<NodeCard> cards;

    public NodeCardAdapter(List<NodeCard> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_node_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        NodeCard card = cards.get(position);

        holder.tvBranchNumber.setText("제 " + card.branchNumber + " 분기점");
        holder.tvStatus.setText(card.selected ? "선택됨" : "선택 전");
        holder.tvTitle.setText(card.title);
        holder.tvExpect.setText(card.expect);
        holder.tvWorry.setText(card.worry);
        holder.tvGiveUp.setText(card.giveUp);
        holder.tvSatisfaction.setText(card.satisfaction + "/10");

        holder.layoutValues.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        for (String value : card.values) {
            TextView chip = (TextView) inflater.inflate(
                    R.layout.item_node_value_chip, holder.layoutValues, false);
            chip.setText(value);
            holder.layoutValues.addView(chip);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        final TextView tvBranchNumber, tvStatus, tvTitle, tvExpect, tvWorry, tvGiveUp, tvSatisfaction;
        final LinearLayout layoutValues;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBranchNumber = itemView.findViewById(R.id.tv_branch_number);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvExpect = itemView.findViewById(R.id.tv_expect);
            tvWorry = itemView.findViewById(R.id.tv_worry);
            tvGiveUp = itemView.findViewById(R.id.tv_give_up);
            tvSatisfaction = itemView.findViewById(R.id.tv_satisfaction);
            layoutValues = itemView.findViewById(R.id.layout_values);
        }
    }
}